package org.javabee.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import org.javabee.entities.JavaBeeAppDescriptorTO;
import org.javabee.entities.JavaBeeTO;
import org.javabee.entities.ManageDirectoryTO;
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
				throw new IllegalArgumentException("Parameter -ids not found, and it's mandatory to -libraries command");
			}
			
			// target directory parameter
			String targetDirectoryParam = null;
			if (!GeneralsHelper.isStringOk( targetDirectoryParam = consoleParameter.getValue("-target_directory") ) 
					&& !GeneralsHelper.isStringOk( targetDirectoryParam = consoleParameter.getValue("-td") )) {
				throw new IllegalArgumentException("Parameter -ids not found, and it's mandatory to -libraries command");
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
			
			System.out.println("Command executed successfully, libraries copied to: " + targetDirectoryParam);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return;
		}
	}
	
	private void libraries(String libraries, String targetDirectoryParam, String manageDependencies) throws IOException {
		File targetDirectory = new File(targetDirectoryParam);
		if (!targetDirectory.exists() || !targetDirectory.isDirectory()) {
			throw new IllegalStateException("The target directory don't exist or is not a directory: " + targetDirectoryParam);
		}
		
		// decide inject dependencies
		Boolean injectDependencies = GeneralsHelper.isBooleanTrue(manageDependencies);
		
		for (JarTO jar : this.javabeeService.listToMount(libraries, injectDependencies)) {
			File fileInsideLibrary = new File(JavaBeeUtils.jarAddressInsideLibrary(jar));
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
			
			System.out.println("Command executed successfully, mount completed: " + jbf.getCanonicalPath());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
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
			
			System.out.println("Command executed successfully, unmount completed: " + jbfOutFile.getCanonicalPath());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
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
				showHeader = GeneralsHelper.isBooleanTrue(showHeaderParam);
			}
			String columns = null;
			if ((columns = consoleParameter.getValue("-columns")) == null) {
				 columns = consoleParameter.getValue("-c");
			}
			boolean showDependencies = true;
			String showDependenciesParam = null;
			if ((showDependenciesParam = consoleParameter.getValue("-show_dependencies")) != null || (showDependenciesParam = consoleParameter.getValue("-sd")) != null) {
				showDependencies = GeneralsHelper.isBooleanTrue(showDependenciesParam);
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
				showSize = GeneralsHelper.isBooleanTrue(showSizeParam);
			}
			if (showSize) {
				System.out.println("\nsize: " + listJar.size());
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
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
	public void add(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -add\n\n");
			
			JavaBeeTO javabee = this.javabeeService.getCurrentState();
			
			// file param
			String targetFileParam = null;
			if (!GeneralsHelper.isStringOk( targetFileParam = consoleParameter.getValue("-file") )) {
				throw new IllegalArgumentException("Parameter -file not found, and it's mandatory to -add command");
			}
			File targetFile = new File(targetFileParam);
			if (!targetFile.exists() || !targetFile.isFile()) {
				System.out.println("File unaccessable: " + targetFileParam);
				return;
			}
			
			// name param
			String name = null;
			if (!GeneralsHelper.isStringOk( name = consoleParameter.getValue("-name") )) {
				throw new IllegalArgumentException("Parameter -name not found, and it's mandatory to -add command");
			}
			
			// version param
			String version = null;
			if (!GeneralsHelper.isStringOk( version = consoleParameter.getValue("-version") )) {
				throw new IllegalArgumentException("Parameter -version not found, and it's mandatory to -add command");
			}
			
			JarTO jar = new JarTO(name + "_" + version);
			jar.setFilename(targetFile.getName());
			jar.setName(name);
			jar.setVersion(version);
			
			// manage known dependencies
			String dependenciesParam = null;
			if (GeneralsHelper.isStringOk( dependenciesParam = consoleParameter.getValue("-dependencies") )) {
				for (String dependency : dependenciesParam.split(",")) {
					dependency = dependency.trim();
					if (!javabee.getJars().containsKey(dependency)) {
						throw new IllegalStateException("There's no library with the declared id");
					} else {
						jar.getListDependencies().add(new DependencyTO(dependency));
					}
				}
			}
			
			// update javabee's state
			javabee.getJars().put(jar.getId(), jar);
			File fileInsideLibrary = new File(JavaBeeUtils.jarAddressInsideLibrary(jar));
			FileUtils.copyFile(targetFile, fileInsideLibrary);
			this.javabeeService.updateState(javabee);
			
			System.out.println("Command executed successfully, add completed: " + jar.getId());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return;
		}
	}

	@Override
	public void update(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -update\n\n");
			
			// id param
			String idParam = null;
			if (!GeneralsHelper.isStringOk( idParam = consoleParameter.getValue("-id") )) {
				throw new IllegalArgumentException("Parameter -id not found, and it's mandatory to -update command");
			}
			
			JavaBeeTO javabee = this.javabeeService.getCurrentState();
			JarTO currentJar = javabee.getJars().get(idParam);
			if (currentJar == null) {
				throw new IllegalStateException("The referenced library id has not been found!");
			}
			
			JarTO newJarTo = new JarTO(idParam);
			boolean hasOneUpdate = false;
			String parameter = null;
			
			// name param
			if (GeneralsHelper.isStringOk( parameter = consoleParameter.getValue("-name") )) {
				newJarTo.setName(parameter.trim());
				hasOneUpdate = true;
			} else {
				newJarTo.setName(currentJar.getName());
			}
			
			// version
			if (GeneralsHelper.isStringOk( parameter = consoleParameter.getValue("-version") )) {
				newJarTo.setVersion(parameter.trim());
				hasOneUpdate = true;
			} else {
				newJarTo.setVersion(currentJar.getVersion());
			}
			
			// dependencies
			if (GeneralsHelper.isStringOk( parameter = consoleParameter.getValue("-dependencies") )) {
				String[] dependencies = parameter.split(",");
				for (String dependency : dependencies) {
					dependency = dependency.trim();
					if (!javabee.getJars().containsKey(dependency)) {
						throw new IllegalStateException("There's no library with the declared id");
					} else {
						newJarTo.getListDependencies().add(new DependencyTO(dependency));
					}
				}
				hasOneUpdate = true;
			} else {
				newJarTo.setListDependencies(currentJar.getListDependencies());
			}
			
			// get the unchanged file name
			newJarTo.setFilename(currentJar.getFilename());
			newJarTo.setId(newJarTo.getName() + "_" + newJarTo.getVersion());
			
			// check if exists some value to update
			if (!hasOneUpdate) {
				throw new IllegalArgumentException("No update value was found to do");
			}
			
			// create the new file structure, if the name or id has been changed
			if (!currentJar.getId().equals(newJarTo.getId())) {
				File currentFile = new File(JavaBeeUtils.jarAddressInsideLibrary(currentJar));
				File targetFile = new File(JavaBeeUtils.jarAddressInsideLibrary(newJarTo));
				FileUtils.copyFile(currentFile, targetFile);
				
				// delete the current
				this.deleteCurrentFileStructure(currentJar);
			}
			
			// update dependencies
			for (JarTO jarTo : javabee.getJars().values()) {
				for (DependencyTO dependency : jarTo.getListDependencies()) {
					if (dependency.getId().equals(currentJar.getId())) {
						dependency.setId(newJarTo.getId());
					}
				}
			}
			
			// remove the current id and set the new one
			javabee.getJars().remove(currentJar.getId());
			javabee.getJars().put(newJarTo.getId(), newJarTo);
			
			// update the javabee's state
			this.javabeeService.updateState(javabee);
			
			System.out.println("Command executed successfully, update completed: " + newJarTo.getId());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return;
		}
	}
	
	@Override
	public void delete(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -delete\n\n");
			
			// id param
			String idParam = null;
			if (!GeneralsHelper.isStringOk( idParam = consoleParameter.getValue("-id") )) {
				throw new IllegalArgumentException("Parameter -id not found, and it's mandatory to -delete command");
			}
			
			JavaBeeTO javabee = this.javabeeService.getCurrentState();
			JarTO jar = null;
			if ((jar = javabee.getJars().remove(idParam)) != null) {
				this.deleteCurrentFileStructure(jar);
				
				// delete dependencies
				for (JarTO jarTo : javabee.getJars().values()) {
					for (int i = 0; i < jarTo.getListDependencies().size(); i++) {
						DependencyTO dependency = jarTo.getListDependencies().get(i);
						if (dependency.getId().equals(jar.getId())) {
							jarTo.getListDependencies().remove(dependency);
						}
					}
				}
				
				// update javabee's state
				this.javabeeService.updateState(javabee);
				
				System.out.println("Jar removed successfully! Jar Id: " + idParam);
			} else {
				throw new IllegalArgumentException("Command failed! Jar Id: " + idParam + " not found.");
			}
			
			System.out.println("Command executed successfully, delete completed: " + jar.getId());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return;
		}
	}

	/**
	 * @param jar
	 */
	private void deleteCurrentFileStructure(JarTO jar) {
		File fileInsideLibrary = new File(JavaBeeUtils.jarAddressInsideLibrary(jar));
		fileInsideLibrary.delete();
		File folderVersion = fileInsideLibrary.getParentFile();
		folderVersion.delete();
		File jarNameFolder = folderVersion.getParentFile();
		if (jarNameFolder.list().length == 0) {
			jarNameFolder.delete();
		}
	}
	
	@Override
	public void export(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -export\n\n");
			
			JavaBeeTO javabee = this.javabeeService.getCurrentState();
			String parameter = null;
			
			// ids
			JavaBeeTO javabeeExporting = new JavaBeeTO(javabee.getVersion());
			if (GeneralsHelper.isStringOk(parameter = consoleParameter.getValue("-ids"))) {
				for (String id : parameter.split(",")) {
					id = id.trim();
					JarTO jar = null;
					if ((jar = javabee.getJars().get(id)) == null) {
						throw new IllegalArgumentException("Id not found: " + id);
					}
					javabeeExporting.getJars().put(jar.getId(), jar);
					for (JarTO dependency : this.javabeeService.getDependencies(jar)) {
						javabeeExporting.getJars().put(dependency.getId(), dependency);
					}
				}
			} else {
				javabeeExporting.setJars(javabee.getJars());
			}
			
			// target directory
			File targetDirectory = null;
			if (GeneralsHelper.isStringOk(parameter = consoleParameter.getValue("-target_directory"))) {
				targetDirectory = new File(parameter);
			} else {
				targetDirectory = this.getCurrentDirectory(consoleParameter);
			}
			
			// temp directory
			File tmpDir = JavaBeeUtils.createTmpDir(JavaBeeConstants.JAVABEE_TMP_DIR);
			
			// pass throughout jars map
			for (JarTO jar : javabeeExporting.getJars().values()) {
				File sourceFile = new File(JavaBeeUtils.jarAddressInsideLibrary(jar));
				File targetFile = new File(tmpDir, JavaBeeConstants.JAVABEE_LIBRARY + JavaBeeUtils.formatJarAddress(jar));
				FileUtils.copyFile(sourceFile, targetFile);
			}
			
			SSDContextManager context = this.javabeeService.getSSDFromJavaBeeTO(javabeeExporting);
			context.toFile(new File(tmpDir, JavaBeeConstants.JAVABEE_DATA_FILE));
			
			// zip the tmp dir to JavaBee State file
			File javaBeeState = new File(targetDirectory, JavaBeeConstants.JAVABEE_STATE_FILE_NAME);
			ZipHelper zipping = new ZipHelper(tmpDir, javaBeeState);
			zipping.compress();
			
			tmpDir.delete();
			
			System.out.println("Command executed successfully, export completed: " + javaBeeState.getCanonicalPath());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return;
		}
	}

	@Override
	public void importState(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -import\n\n");
			
			// file
			String jbsParam = null;
			File jbsFile = null;
			if (GeneralsHelper.isStringOk(jbsParam = consoleParameter.getValue("-file"))) {
				jbsFile = new File(jbsParam);
				if (!jbsFile.exists() || !jbsFile.isFile()) {
					throw new IllegalArgumentException("The file don't exists or isn't a file: " + jbsParam);
				}
			} else {
				throw new IllegalArgumentException("Parameter -file not found, and it's mandatory to -import command");
			}
			
			// override
			Boolean override = GeneralsHelper.isBooleanTrue(consoleParameter.getValue("-override"));
			
			File tmpDir = JavaBeeUtils.createTmpDir(JavaBeeConstants.JAVABEE_TMP_DIR);
			UnZipHelper unzipping = new UnZipHelper(jbsFile, tmpDir);
			unzipping.decompress();
			
			// check if file descriptor exists
			File javabeeData = new File(tmpDir, JavaBeeConstants.JAVABEE_DATA_FILE);
			if (!javabeeData.exists() || !javabeeData.isFile()) {
				throw new IllegalStateException("The file descriptor don't exists: " + JavaBeeConstants.JAVABEE_DATA_FILE);
			}
			
			// update current state
			JavaBeeTO currentState = this.javabeeService.getCurrentState();
			JavaBeeTO javabeeImporting = this.javabeeService.getJavaBeeTOFromSSD(javabeeData);
			for (JarTO jar : javabeeImporting.getJars().values()) {
				if (!currentState.getJars().containsKey(jar.getId()) || override) {
					File sourceFile = new File(tmpDir, JavaBeeConstants.JAVABEE_LIBRARY + JavaBeeUtils.formatJarAddress(jar));
					File fileInsideLibrary = new File(JavaBeeUtils.jarAddressInsideLibrary(jar));
					FileUtils.copyFile(sourceFile, fileInsideLibrary);
					
					currentState.addJar(jar);
				}
			}
			this.javabeeService.updateState(currentState);
			
			System.out.println("Command executed successfully, import completed: " + jbsFile.getCanonicalPath() + " imported!");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return;
		}
	}
	
	@Override
	public void appDescriptor(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -app_descriptor\n\n");
			
			File fileDescriptor = new File(this.getCurrentDirectory(consoleParameter), JavaBeeConstants.JAVABEE_FILE_DESCRIPTOR);
			JavaBeeAppDescriptorTO javaBeeDescriptor = null;
			if (fileDescriptor.exists() && fileDescriptor.isFile()) {
				javaBeeDescriptor = this.javabeeService.getDescriptorFromSSD(fileDescriptor);
			} else {
				javaBeeDescriptor = new JavaBeeAppDescriptorTO();
			}
			
			String parameter = null;

			// app_name param
			if (GeneralsHelper.isStringOk(parameter = consoleParameter.getValue("-app_name"))) {
				javaBeeDescriptor.setAppName(parameter);
			}
			
			// extract_name param
			if (GeneralsHelper.isStringOk(parameter = consoleParameter.getValue("-extract_name"))) {
				javaBeeDescriptor.setExtractName(parameter);
			}
			
			// target_directory param
			String targetDirectory = null;
			if (GeneralsHelper.isStringOk(targetDirectory = consoleParameter.getValue("-target_directory")) &&
					javaBeeDescriptor.getManageDependencies().get(targetDirectory) == null) {
				ManageDirectoryTO md = new ManageDirectoryTO();
				md.setTargetDirectory(targetDirectory);
				javaBeeDescriptor.getManageDependencies().put(md.getTargetDirectory(), md);
			}
			
			// inject_dependency param
			if (GeneralsHelper.isStringOk(parameter = consoleParameter.getValue("-inject_dependency"))) {
				if (!GeneralsHelper.isStringOk(targetDirectory)) {
					throw new IllegalArgumentException("To indicate an -inject_dependency it's mandatory indicate what -target_directory you want update!");
				}
				javaBeeDescriptor.getManageDependencies().get(targetDirectory).setInjectDependencies(GeneralsHelper.isBooleanTrue(parameter));
			}
			
			// set_id param
			if (GeneralsHelper.isStringOk(parameter = consoleParameter.getValue("-set_id"))) {
				if (!GeneralsHelper.isStringOk(targetDirectory)) {
					throw new IllegalArgumentException("To indicate an -set_id it's mandatory indicate what -target_directory you want update!");
				}
				javaBeeDescriptor.getManageDependencies().get(targetDirectory).setSetIds(JavaBeeUtils.formatSetIdString(parameter));
			}
			
			// selective_removing
			if (GeneralsHelper.isStringOk(parameter = consoleParameter.getValue("-selective_removing"))) {
				if (!GeneralsHelper.isStringOk(targetDirectory)) {
					throw new IllegalArgumentException("To indicate an -selective_removing it's mandatory indicate what -target_directory you want update!");
				}
				List<String> selectiveRemoving = Arrays.asList(parameter.split(","));
				javaBeeDescriptor.getManageDependencies().get(targetDirectory).setSelectiveRemoving(selectiveRemoving);
			}
			
			// remove_target
			if (GeneralsHelper.isStringOk(parameter = consoleParameter.getValue("-remove_target"))) {
				if (!GeneralsHelper.isStringOk(targetDirectory)) {
					throw new IllegalArgumentException("To indicate an -remove_target it's mandatory indicate what -target_directory you want remove!");
				}
				javaBeeDescriptor.getManageDependencies().remove(targetDirectory);
			}
			
			SSDContextManager ssdCtx = this.javabeeService.getSSDFromDescriptor(javaBeeDescriptor);
			
			// show param
			if (!GeneralsHelper.isStringOk(parameter = consoleParameter.getValue("-show"))) {
				ssdCtx.toFile(fileDescriptor);
			}
			
			System.out.println(ssdCtx.toString());
			
			System.out.println("Command executed successfully, application descriptor created: " + fileDescriptor.getCanonicalPath());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return;
		}
	}
	
	@Override
	public void printVersion() {
		System.out.print("command javabee -version\n\n");
		
		System.out.println("version: " + JavaBeeConstants.JAVABEE_VERSION);
		System.out.println("release: " + JavaBeeConstants.JAVABEE_RELEASE);
	}
	
	@Override
	public void printHelp() {
		StringBuffer helpMessage = new StringBuffer();
		helpMessage.append("command javabee -help\n");
		helpMessage.append(" -help[-h]                      show possible actions with its needed parameters\n");
		helpMessage.append(" -version[-v]                   show version of the current JavaBee\n");
		helpMessage.append(" -add                           add a new library to JavaBee manage\n");
		helpMessage.append("   -file                        the full file (library) address\n");
		helpMessage.append("   -name                        the name of the library in javabee's context\n");
		helpMessage.append("   -version                     the version of the library\n");
		helpMessage.append("   [-dependencies]              the list id dependencies splitted by comma(,)\n");
		helpMessage.append(" -delete[-d]                    delete a library from the current JavaBee\n");
		helpMessage.append("   -id                          the desired id library to be deleted\n");
		helpMessage.append(" -update[-u]                    update info about some library\n");
		helpMessage.append("   -id                          the desired id library to be updated\n");
		helpMessage.append("   [-name]                      the new name of the library\n");
		helpMessage.append("   [-version]                   the new version of the library\n");
		helpMessage.append("   [-dependencies]              the new list id dependencies splitted by comma(,)\n");
		helpMessage.append(" -export                        export the current JavaBee's state to jbs file\n");
		helpMessage.append("   [-ids]                       export just a desired set ids\n");
		helpMessage.append("   [-target_directory]          target directory to save the .jbs file\n");
		helpMessage.append(" -import                        import a JavaBee's state from a .jbs file\n");
		helpMessage.append("   -file                        the target .jbs file\n");
		helpMessage.append("   [-override]                  override some library if it exists"+ JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS +"\n");
		helpMessage.append(" -list[-l]                      show the current stored libraries\n");
		helpMessage.append("   [-columns][-c]               choice select columns(id,name,version,filename)\n");
		helpMessage.append("   [-show_header][-sh]          list header"+ JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS +"\n");
		helpMessage.append("   [-show_dependencies][-sd]    list dependencies"+ JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS +"\n");
		helpMessage.append("   [-sort_column][-sc]          order by column ASC(id,name,version,filename)\n");
		helpMessage.append("   [-sort_size][-sz]            show size at the end"+ JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS +"\n");
		helpMessage.append(" -libraries                     mount a directory with all desired libraries\n");
		helpMessage.append("   -ids                         a set with all desired id libraries\n");
		helpMessage.append("   -target_directory            the target diretory to copy the desired libraries\n");
		helpMessage.append("   [-manage_dependencies][-md]  inject or not dependencies"+ JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS +"\n");
		helpMessage.append(" -mount                         mount a file .jbf (JavaBee File) from a completed application file\n");
		helpMessage.append("   -file                        the current completed and compressed file address\n");
		helpMessage.append(" -unmount                       return the .jbf (JavaBee File) to application's properly state\n");
		helpMessage.append("   -file                        a .jbf valid file address\n");
		helpMessage.append("   [-to]                        the target directory to the application compressed file\n");
		helpMessage.append(" -app_descriptor                command to generate or update a " + JavaBeeConstants.JAVABEE_FILE_DESCRIPTOR + " file\n");
		helpMessage.append("   [-show]                      just show the state of the " + JavaBeeConstants.JAVABEE_FILE_DESCRIPTOR + " file\n");
		helpMessage.append("   [-app_name]                  the application name to " + JavaBeeConstants.JAVABEE_FILE_DESCRIPTOR + " file\n");
		helpMessage.append("   [-extract_name]              the desired file name when extracted from .jbf (JavaBee File)\n");
		helpMessage.append("   [-target_directory]          a managed target directory that holds libraries\n");
		helpMessage.append("   [-inject_dependency]         inject or don't dependencies to this target_directory\n");
		helpMessage.append("   [-set_id]                    the set id dependencies that will be place in this target_directoy\n");
		helpMessage.append("   [-selective_removing]        the name of the files that will be removed from this target_directory\n");
		helpMessage.append("   [-remove_target]             parameter to remove the current target_directory from the " + JavaBeeConstants.JAVABEE_FILE_DESCRIPTOR + " file\n");
		
		System.out.print(helpMessage.toString());
	}
	
	private File getCurrentDirectory(ConsoleParameters consoleParameters) {
		String currentDirectoryParam = consoleParameters.getValue(JavaBeeConstants.CURRENT_DIRECTORY_PARAM, true);
		currentDirectoryParam = currentDirectoryParam.trim();
		return new File(currentDirectoryParam);
	}

}
