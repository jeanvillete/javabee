package org.javabee.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.com.tatu.helper.FileHelper;
import org.com.tatu.helper.GeneralsHelper;
import org.com.tatu.helper.parameter.ConsoleParameters;
import org.com.tatu.helper.zip.UnZipHelper;
import org.com.tatu.helper.zip.ZipHelper;
import org.javabee.commons.JavaBeeConstants;
import org.javabee.commons.JavaBeeUtils;
import org.javabee.entities.DependencyTO;
import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeTO;
import org.javabee.service.Console;
import org.javabee.service.JavaBee;
import org.simplestructruedata.data.SSDContextManager;
import org.simplestructruedata.data.SSDContextManager.SSDRootObject;
import org.simplestructruedata.entities.SSDObject;
import org.simplestructruedata.entities.SSDObjectArray;
import org.simplestructruedata.entities.SSDObjectLeaf;
import org.simplestructruedata.entities.SSDObjectNode;

public class ConsoleBO implements Console {

	private JavaBee				javabeeService = new JavaBeeBO();
	
	@Override
	public void libraries(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -libraries\n\n");
			
			// ids parameter
			String libraries = null;
			if (!GeneralsHelper.isStringOk( libraries = consoleParameter.getValue("-ids") )) {
				throw new IllegalArgumentException("Parameter not -ids not found, and it's mandatory to -libraries command");
			}
			
			// target directory parameter
			String targetDirectoryParam = null;
			if (!GeneralsHelper.isStringOk( targetDirectoryParam = consoleParameter.getValue("-target_directory") ) 
					&& !GeneralsHelper.isStringOk( targetDirectoryParam = consoleParameter.getValue("-td") )) {
				throw new IllegalArgumentException("Parameter not -ids not found, and it's mandatory to -libraries command");
			}
			
			// managed dependencies parameter
			String manageDependencies = consoleParameter.getValue("-manage_dependencies");
			if (!GeneralsHelper.isStringOk(manageDependencies)) {
				manageDependencies = consoleParameter.getValue("-md");
				if (!GeneralsHelper.isStringOk(manageDependencies)) {
					manageDependencies = "false";
				}
			}
			
			this.libraries(libraries, targetDirectoryParam, manageDependencies);
			
			System.out.print("Command executed successfully, libraries copied to: " + targetDirectoryParam);
		} catch (Exception e) {
			System.out.print("Error: " + e.getMessage());
			return;
		}
	}
	
	private void libraries(String libraries, String targetDirectoryParam, String manageDependencies) throws IOException {
		File targetDirectory = new File(targetDirectoryParam);
		if (!targetDirectory.exists() || !targetDirectory.isDirectory()) {
			throw new IllegalStateException("The target directory don't exist or is not a directory: " + targetDirectoryParam);
		}
		
		// decide inject dependencies
		Boolean injectDependencies = manageDependencies.equals("true") || manageDependencies.equals("1") ||
				manageDependencies.equals("yes") || manageDependencies.equals("y");
		
		for (JarTO jar : this.javabeeService.listToMount(libraries, injectDependencies)) {
			File fileInsideLibrary = new File(JavaBeeUtils.formatJarAddress(jar));
			FileUtils.copyFileToDirectory(fileInsideLibrary, targetDirectory);
		}
	}

	@Override
	public void mount(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -mount\n\n");
			
			String fileSourceParam = null;
			if (!GeneralsHelper.isStringOk( fileSourceParam = consoleParameter.getValue("-file") )) {
				throw new IllegalArgumentException("Parameter -file not found, and it's mandatory to -mount command");
			}
			File fileSource = new File(fileSourceParam);
			File tmpDir = JavaBeeUtils.createTmpDir(JavaBeeConstants.JAVABEE_TMP_DIR);
			
			UnZipHelper unzipping = new UnZipHelper(fileSource, tmpDir);
			unzipping.decompress();
			
			File javabeeDescriptor = new File(tmpDir, JavaBeeConstants.JAVABEE_FILE_DESCRIPTOR);
			if (!javabeeDescriptor.exists() || !javabeeDescriptor.isFile()) {
				throw new IllegalStateException("The file " + JavaBeeConstants.JAVABEE_FILE_DESCRIPTOR + " descriptor has not been found.");
			}
			
			SSDContextManager ssdContext = SSDContextManager.build(javabeeDescriptor);
			SSDRootObject root = ssdContext.getRootObject();
			for (SSDObject ssdObject : root.getArray("manage-libraries").getElements()) {
				SSDObjectNode node = (SSDObjectNode) ssdObject;
				String paramTargetDirectory = node.getLeaf("target-directory").getValue();
				File targetDirectory = new File(tmpDir, paramTargetDirectory);
				if (!targetDirectory.exists() || !targetDirectory.isDirectory()) {
					throw new IllegalStateException("The target directory " + paramTargetDirectory + " declared at " + JavaBeeConstants.JAVABEE_FILE_DESCRIPTOR + " don't exist");
				}

				// preparing remove
				List<String> listRemoving = new ArrayList<String>();
				for (SSDObject ssdObjectRemoving : node.getArray("selective-removing").getElements()) {
					SSDObjectLeaf removing = (SSDObjectLeaf) ssdObjectRemoving;
					listRemoving.add(removing.getValue());
				}
				FileHelper fh = new FileHelper(targetDirectory.getCanonicalPath());
				fh.delete(listRemoving.toArray(new String[]{}));
			}
			
			// zipping the prepared tmp directory
			File jbf = new File(this.getCurrentDirectory(consoleParameter), FileHelper.removeExtension(fileSource.getName()) + JavaBeeConstants.JAVABEE_FILE_EXTENSION);
			ZipHelper zipping = new ZipHelper(tmpDir, jbf);
			zipping.compress();
			
			tmpDir.delete();
			
			System.out.print("Command executed successfully, mount completed: " + jbf.getCanonicalPath());
		} catch (Exception e) {
			System.out.print("Error: " + e.getMessage());
			return;
		}
	}
	
	@Override
	public void unmount(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -unmount\n\n");
			
			// source file
			String jbfFileSourceParam = null;
			if (!GeneralsHelper.isStringOk( jbfFileSourceParam = consoleParameter.getValue("-file") )) {
				throw new IllegalArgumentException("Parameter -file not found, and it's mandatory to -unmount command");
			}
			File jbfSourceFile = new File(jbfFileSourceParam);
			
			// target directory
			String targetDirectoryParam = consoleParameter.getValue("-to");
			File targetDirectory = GeneralsHelper.isStringOk(targetDirectoryParam) ? new File(targetDirectoryParam) : this.getCurrentDirectory(consoleParameter);
			
			File tmpDir = JavaBeeUtils.createTmpDir(JavaBeeConstants.JAVABEE_TMP_DIR);
			
			UnZipHelper unzipping = new UnZipHelper(jbfSourceFile, tmpDir);
			unzipping.decompress();
			
			File javabeeDescriptor = new File(tmpDir, JavaBeeConstants.JAVABEE_FILE_DESCRIPTOR);
			if (!javabeeDescriptor.exists() || !javabeeDescriptor.isFile()) {
				throw new IllegalStateException("The file " + JavaBeeConstants.JAVABEE_FILE_DESCRIPTOR + " descriptor has not been found.");
			}
			
			SSDContextManager ssdContext = SSDContextManager.build(javabeeDescriptor);
			SSDRootObject root = ssdContext.getRootObject();
			for (SSDObject ssdObject : root.getArray("manage-libraries").getElements()) {
				SSDObjectNode manageLibraries = (SSDObjectNode) ssdObject;
				
				SSDObjectArray setId = manageLibraries.getArray("set-id");
				StringBuffer stringIds = new StringBuffer();
				for (int i = 0; i < setId.getSize() ; i++) {
					stringIds.append(setId.getLeaf(i).getValue());
					if ((i+1) < setId.getSize()) {
						stringIds.append(", ");
					}
				}
				File libTargetDirectory = new File(tmpDir, manageLibraries.getLeaf("target-directory").getValue());
				this.libraries(stringIds.toString(), libTargetDirectory.getCanonicalPath(), manageLibraries.getLeaf("inject-dependencies").getValue());
			}
			
			// zipping the prepared tmp directory
			File jbfOutFile = new File(targetDirectory, root.getLeaf("extract-name").getValue());
			ZipHelper zipping = new ZipHelper(tmpDir, jbfOutFile);
			zipping.compress();
			
			tmpDir.delete();
			
			System.out.print("Command executed successfully, unmount completed: " + jbfOutFile.getCanonicalPath());
		} catch (Exception e) {
			System.out.print("Error: " + e.getMessage());
			return;
		}
	}
	
	@Override
	public void list(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -list\n\n");
			boolean showHeader = true;
			String showHeaderParam = null;
			if ((showHeaderParam = consoleParameter.getValue("-show_header")) != null || (showHeaderParam = consoleParameter.getValue("-sh")) != null) {
				showHeader = showHeaderParam.equals("true") || showHeaderParam.equals("1") ||
						showHeaderParam.equals("yes") || showHeaderParam.equals("y");
			}
			String columns = null;
			if ((columns = consoleParameter.getValue("-columns")) == null) {
				 columns = consoleParameter.getValue("-c");
			}
			boolean showDependencies = true;
			String showDependenciesParam = null;
			if ((showDependenciesParam = consoleParameter.getValue("-show_dependencies")) != null || (showDependenciesParam = consoleParameter.getValue("-sd")) != null) {
				showDependencies = showDependenciesParam.equals("true") || showDependenciesParam.equals("1") ||
						showDependenciesParam.equals("yes") || showDependenciesParam.equals("y");
			}
			List<JarTO> listJar = this.javabeeService.listJars();
			String sortColumn = null;
			if ((sortColumn = consoleParameter.getValue("-sort_column")) != null || (sortColumn = consoleParameter.getValue("-sc")) != null) {
				if (sortColumn.contains("id")) {
					Collections.sort(listJar, JarUtilComparator.getIdComparator());
				}
				if (sortColumn.contains("name")) {
					Collections.sort(listJar, JarUtilComparator.getNameComparator());
				}
				if (sortColumn.contains("version")) {
					Collections.sort(listJar, JarUtilComparator.getVersionComparator());
				}
				if (sortColumn.contains("filename")) {
					Collections.sort(listJar, JarUtilComparator.getFileNameComparator());
				}
			}
			// check show header
			if (showHeader) {
				this.printListWithHeader(listJar, columns, showDependencies);
			} else {
				this.printListWithoutHeader(listJar, columns, showDependencies);
			}
			
			boolean showSize = true;
			String showSizeParam = null;
			if ((showSizeParam = consoleParameter.getValue("-show_size")) != null || (showSizeParam = consoleParameter.getValue("-sz")) != null) {
				showSize = showSizeParam.equals("true") || showSizeParam.equals("1") ||
						showSizeParam.equals("yes") || showSizeParam.equals("y");
			}
			if (showSize) {
				System.out.print("\nsize: " + listJar.size() + "\n");
			}
		} catch (Exception e) {
			System.out.println("1[" + e.getMessage() + "]");
			return;
		}
	}
	
	private void printListWithoutHeader(List<JarTO> listJar, String columns, boolean showDependencies) {
		if (!GeneralsHelper.isStringOk(columns)) {
			columns = "id,name,version,filename";
		}
		for (JarTO jar : listJar) {
			if (columns.contains("id")) {
				System.out.println(jar.getId());
			}
			if (columns.contains("name")) {
				System.out.println(jar.getName());
			}
			if (columns.contains("version")) {
				System.out.println(jar.getVersion());
			}
			if (columns.contains("filename")) {
				System.out.println(jar.getFilename());
			}
			if (showDependencies) {
				if (GeneralsHelper.isCollectionOk(jar.getListDependencies()))
					for (DependencyTO dependency : jar.getListDependencies())
						System.out.println("+" + dependency.getId());
			}
		}
	}
	
	private void printListWithHeader(List<JarTO> listJar, String columns, boolean showDependencies) {
		if (!GeneralsHelper.isStringOk(columns)) {
			columns = "id,name,version,filename";
		}
		for (JarTO jar : listJar) {
			if (columns.contains("id")) {
				System.out.println("id:\t\t" + jar.getId());
			}
			if (columns.contains("name")) {
				System.out.println("name:\t\t" + jar.getName());
			}
			if (columns.contains("version")) {
				System.out.println("version:\t" + jar.getVersion());
			}
			if (columns.contains("filename")) {
				System.out.println("filename:\t" + jar.getFilename());
			}
			if (showDependencies) {
				if (GeneralsHelper.isCollectionOk(jar.getListDependencies()))
					for (DependencyTO dependency : jar.getListDependencies())
						System.out.println("+ dependency id: " + dependency.getId());
			}
		}
	}
	
	@Override
	public void add() {
		try {
			JavaBeeTO javabee = this.javabeeService.getCurrentState();
			String currentFileAddress = getDialogueResponse("Library File (full) Address");
			File targetFile = new File(currentFileAddress);
			if (!targetFile.exists() || !targetFile.isFile()) {
				System.out.println("File unaccessable: " + currentFileAddress);
				return;
			}
			String name = getDialogueResponse("library NAME");
			String version = getDialogueResponse("libray VERSION");
			JarTO jar = new JarTO(name + "_" + version);
			jar.setFilename(targetFile.getName());
			jar.setName(name);
			jar.setVersion(version);
			String dependencyInput = getDialogueResponse("Do you want declare SOME known dependency for this library?" +
					JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS);
			while (GeneralsHelper.isStringOk(dependencyInput) && dependencyInput.equals("y")) {
				String dependencyId = getDialogueResponse("dependency id");
				if (!javabee.getJars().containsKey(dependencyId)) {
					System.out.println("There's no library with the declared id");
				} else {
					jar.getListDependencies().add(new DependencyTO(dependencyId));
				}
				dependencyInput = getDialogueResponse("Do you want declare ANOTHER known dependency for this library?" +
						JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS);
			}
			javabee.getJars().put(jar.getId(), jar);
			File fileInsideLibrary = new File(JavaBeeUtils.formatJarAddress(jar));
			FileUtils.copyFile(targetFile, fileInsideLibrary);
			this.javabeeService.updateState();
			System.out.println("Library added successfully! " + jar.getId());
		} catch (Exception e) {
			System.out.println("1[" + e.getMessage() + "]");
			return;
		}
	}
	
	@Override
	public void delete(String idJar) {
		try {
			JavaBeeTO javabee = this.javabeeService.getCurrentState();
			JarTO jar=null;
			if ((jar = javabee.getJars().remove(idJar)) != null) {
				File fileInsideLibrary = new File(JavaBeeUtils.formatJarAddress(jar));
				fileInsideLibrary.delete();
				File folderVersion = fileInsideLibrary.getParentFile();
				folderVersion.delete();
				File jarNameFolder = folderVersion.getParentFile();
				if (!(jarNameFolder.list().length > 0)) {
					jarNameFolder.delete();
				}
				this.javabeeService.updateState();
				System.out.println("Jar removed successfully! Jar Id: " + idJar);
			} else {
				throw new IllegalArgumentException("Command failed! Jar Id: " + idJar + " not found.");
			}
		} catch (Exception e) {
			System.out.println("1[" + e.getMessage() + "]");
			return;
		}
	}
	
	@Override
	public void printVersion() {
		System.out.println("command javabee -version: " + JavaBeeConstants.JAVA_BEE_VERSION);
	}
	
	@Override
	public void printHelp() {
		StringBuffer helpMessage = new StringBuffer();
		helpMessage.append("command javabee -help\n");
		helpMessage.append(" -help[-h]                    show possible actions with its needed parameters\n");
		helpMessage.append(" -version[-v]                 show version of the current JavaBee\n");
		helpMessage.append(" -add(wizard prompt)          add a new library to JavaBee manage\n");
		helpMessage.append(" -delete[-d] \"jar id\"         delete a library from the current JavaBee\n");
		helpMessage.append(" -update[-u] \"jar id\"         update info about some library\n");
		helpMessage.append(" -export \"target file\"        export the current JavaBee's state\n");
		helpMessage.append(" -import \"source file\"        import a JavaBee's state\n");
		helpMessage.append(" -list[-l]                    show the current stored libraries\n");
		helpMessage.append("   ( optional )\n");
		helpMessage.append("   -columns[-c]               choice select columns(id,name,version,filename)\n");
		helpMessage.append("   -show_header[-sh]          list header"+ JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS +"\n");
		helpMessage.append("   -show_dependencies[-sd]    list dependencies"+ JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS +"\n");
		helpMessage.append("   -sort_column[-sc]          order by column ASC(id,name,version,filename)\n");
		helpMessage.append("   -sort_size[-sz]            show size at the end"+ JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS +"\n");
		helpMessage.append(" -libraries                   mount a directory with all desired libraries\n");
		helpMessage.append("   ( mandatory )\n");
		helpMessage.append("   -ids                       a set with all desired id libraries\n");
		helpMessage.append("   -target_directory          the target diretory to copy the desired libraries\n");
		helpMessage.append("   ( optional )\n");
		helpMessage.append("   -manage_dependencies[-md]  inject or not dependencies"+ JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS +"\n");
		helpMessage.append(" -mount                       mount a file .jbf (JavaBee File) from a completed application file\n");
		helpMessage.append("   ( mandatory )\n");
		helpMessage.append("   -file                      the current completed and compressed file address\n");
		helpMessage.append(" -unmount                     return the .jbf (JavaBee File) to application's properly state\n");
		helpMessage.append("   ( mandatory )\n");
		helpMessage.append("   -file                      a .jbf valid file address\n");
		helpMessage.append("   ( optional )\n");
		helpMessage.append("   -to                        the target directory to application compressed file\n");
		System.out.print(helpMessage.toString());
	}
	
	private static String getDialogueResponse(String mensage) {
		System.out.print(mensage + ": ");
		return new Scanner(System.in).next().trim();
	}

	private File getCurrentDirectory(ConsoleParameters consoleParameters) {
		String currentDirectoryParam = consoleParameters.getValue(JavaBeeConstants.CURRENT_DIRECTORY_PARAM, true);
		currentDirectoryParam = currentDirectoryParam.trim();
		return new File(currentDirectoryParam);
	}
}
