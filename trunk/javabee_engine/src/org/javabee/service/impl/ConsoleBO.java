package org.javabee.service.impl;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.com.tatu.helper.GeneralsHelper;
import org.com.tatu.helper.parameter.ConsoleParameters;
import org.javabee.commons.JavaBeeConstants;
import org.javabee.commons.JavaBeeUtils;
import org.javabee.entities.DependencyTO;
import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeTO;
import org.javabee.service.Console;
import org.javabee.service.JavaBee;

public class ConsoleBO implements Console {

	private JavaBee				javabeeService = new JavaBeeBO();
	
	@Override
	public void mount(ConsoleParameters consoleParameter) {
		boolean fromClient = GeneralsHelper.isStringOk(consoleParameter.getValue("-from_client")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-fc"));
		try {
			if (!fromClient) {
				System.out.print("command javabee -mount\n\n");
			}
			// libraries parameter
			String libraries = consoleParameter.getValue("-libraries");
			if (!GeneralsHelper.isStringOk(libraries)) {
				libraries = consoleParameter.getValue("-lib");
				if (!GeneralsHelper.isStringOk(libraries)) {
					throw new IllegalArgumentException("Parameter not -libraries or -lib not found, and it's mandatory to -mount command");
				}
			}
			// managed dependencies parameter
			String manageDependencies = consoleParameter.getValue("-manage_dependencies");
			Boolean injectDependencies=null;
			if (!GeneralsHelper.isStringOk(manageDependencies)) {
				manageDependencies = consoleParameter.getValue("-md");
				if (!GeneralsHelper.isStringOk(manageDependencies)) {
					manageDependencies = "false";
				}
				injectDependencies = manageDependencies.equals("true") || manageDependencies.equals("1") ||
						manageDependencies.equals("yes") || manageDependencies.equals("y");
			}
			// check if the libraries exist
			
			// create temporary directory
			File tmpDir = JavaBeeUtils.createTmpDir(JavaBeeConstants.JAVABEE_TMP_DIR);
			for (JarTO jar : this.javabeeService.listToMount(libraries, injectDependencies)) {
				File fileInsideLibrary = new File(JavaBeeUtils.formatJarAddress(jar));
				FileUtils.copyFileToDirectory(fileInsideLibrary, tmpDir);
			}
			if (fromClient) {
				System.out.print("0,"+tmpDir.getCanonicalPath());
			} else {
				System.out.print("0[\""+ tmpDir.getCanonicalPath() +"\"]");
			}
		} catch (Exception e) {
			if (fromClient) {
				System.out.print("1," + e.getMessage());
			} else {
				System.out.print("1[\"" + e.getMessage() + "\"]");
			}
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
		helpMessage.append(" -mount                       mount a directory with all desired libraries\n");
		helpMessage.append("   ( mandatory )\n");
		helpMessage.append("   -libraries[-lib]           a set with all desired id libraries\n");
		helpMessage.append("   ( optional )\n");
		helpMessage.append("   -manage_dependencies[-md]  inject or not dependencies"+ JavaBeeConstants.BOOLEAN_CONSOLE_OPTIONS +"\n");
		System.out.print(helpMessage.toString());
	}
	
	private static String getDialogueResponse(String mensage) {
		System.out.print(mensage + ": ");
		return new Scanner(System.in).next().trim();
	}
	
}
