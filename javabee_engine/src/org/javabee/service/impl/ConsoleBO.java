package org.javabee.service.impl;

import java.io.File;
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
			
			// check show header
			if (showHeader) {
				this.printListWithHeader(this.javabeeService.listJars(), columns, showDependencies);
			} else {
				this.printListWithoutHeader(this.javabeeService.listJars(), columns, showDependencies);
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
			String dependencyInput = getDialogueResponse("Do you want declare some known dependency for this library? (y/n)");
			while (GeneralsHelper.isStringOk(dependencyInput) && dependencyInput.equals("y")) {
				String dependencyId = getDialogueResponse("dependency id");
				if (!javabee.getJars().containsKey(dependencyId)) {
					System.out.println("There's no library with the declared id");
				} else {
					jar.getListDependencies().add(new DependencyTO(dependencyId));
				}
				dependencyInput = getDialogueResponse("Do you want declare ANOTHER known dependency for this library? (y/n)");
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
		helpMessage.append("  -help[-h]                      show the possible actions with its needed parameters\n");
		helpMessage.append("  -version[-v]                   show the version of the current JavaBee\n");
		helpMessage.append("  -list[-l]                      show all libraries actually stored\n");
		helpMessage.append("    -columns[-c]                   list with select columns (id, name, version, filename)\n");
		helpMessage.append("    -show_header[-sh]              list with or without header (true/1 or false/0)\n");
		helpMessage.append("    -show_dependencies[-sd]        list with or without dependencies (true/1 or false/0)\n");
		helpMessage.append("  -add(wizard prompt)            command used to add a new library to JavaBee manage\n");
		helpMessage.append("  -delete[-d] \"jar id\"           command to delete a library from the current JavaBee\n");
		helpMessage.append("  -update[-u] \"jar id\"           command to update info of some library and/or its jar file\n");
		helpMessage.append("  -export \"target file address\"  command used to export the current JavaBee's state\n");
		helpMessage.append("  -import \"source file address\"  command used to import a JavaBee's state\n");
		helpMessage.append("  -mount                         command used build the directory with desired libraries\n");
		helpMessage.append("    -libraries[-lib]           (mandatory)set with all id libraries desired\n");
		helpMessage.append("    -manage_dependencies[-md]  (optional, default=false)inject or not dependencies\n");
		System.out.print(helpMessage.toString());
	}
	
	private static String getDialogueResponse(String mensage) {
		System.out.print(mensage + ": ");
		return new Scanner(System.in).next().trim();
	}
	
}
