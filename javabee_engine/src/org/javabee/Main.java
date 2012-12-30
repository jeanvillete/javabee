/**
 * 
 */
package org.javabee;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.com.tatu.helper.GeneralsHelper;
import org.com.tatu.helper.parameter.ConsoleParameters;
import org.javabee.constants.JavaBeeConstants;
import org.javabee.entities.DependencyTO;
import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeTO;
import org.javabee.service.JavaBee;
import org.javabee.service.impl.JavaBeeBO;

/**
 * @author Jean Villete
 *
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("JavaBee Org Library Manager");
		ConsoleParameters consoleParameter = ConsoleParameters.getInstance(args);
		// help
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-help")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-h"))) {
			printHelp();
			return;
		}
		// version
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-version")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-v"))) {
			printVersion();
			return;
		}
		// list
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-list")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-l"))) {
			JavaBee service = new JavaBeeBO();
			System.out.print("command javabee -list\n\n");
			for (JarTO jar : service.listJars()) {
				System.out.println("id:\t\t" + jar.getId());
				System.out.println("name:\t\t" + jar.getName());
				System.out.println("version:\t" + jar.getVersion());
				System.out.println("filename:\t" + jar.getFilename());
				if (GeneralsHelper.isCollectionOk(jar.getListDependencies()))
					for (DependencyTO dependency : jar.getListDependencies())
						System.out.println("+ dependency id: " + dependency.getId());
				System.out.println();
			}
			return;
		}
		// add
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-add"))) {
			JavaBee service = new JavaBeeBO();
			JavaBeeTO javabee = service.getCurrentState();
			String currentFileAddress = getDialogueResponse("jar File (full) Address");
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
			try {
				javabee.getJars().put(jar.getId(), jar);
				File fileInLibrary = new File(JavaBeeConstants.LIBRARY_ROOT_ADDRESS +
						System.getProperty("file.separator") + jar.getName() + 
						System.getProperty("file.separator") + jar.getVersion() +
						System.getProperty("file.separator") + jar.getFilename());
				FileUtils.copyFile(targetFile, fileInLibrary);
				service.updateState();
				System.out.println("Library added successfully! " + jar.getId());
			} catch (IOException e) {
				System.out.println("Problems while getting file: " + e.getMessage());
			}
			return;
		}
		// delete
		// update
		// export
		// import
		
		printHelp();
	}
	
	private static void printVersion() {
		System.out.println("command javabee -version: " + JavaBeeConstants.JAVA_BEE_VERSION);
	}
	
	private static void printHelp() {
		StringBuffer helpMessage = new StringBuffer();
		helpMessage.append("command javabee -help\n");
		helpMessage.append("\t-help[-h]\t\t\tshow the possible actions with its needed parameters\n");
		helpMessage.append("\t-version[-v]\t\t\tshow the version of the current JavaBee\n");
		helpMessage.append("\t-list[-l]\t\t\tshow all libraries actually stored\n");
		helpMessage.append("\t-add(wizard prompt)\t\tcommand used to add a new library to JavaBee manage\n");
		helpMessage.append("\t-delete[-d]\"jar id\"\t\tcommand to delete a library from the current JavaBee\n");
		helpMessage.append("\t-update[-u] \"jar id\"\t\tcommand to update info about some library and/or its jar file\n");
		helpMessage.append("\t-export \"target file address\"\tcommand used to export the current JavaBee's state\n");
		helpMessage.append("\t-import \"source file address\"\tcommand used to import a JavaBee's state");
		System.out.println(helpMessage.toString());
	}
	
	private static String getDialogueResponse(String mensage) {
		System.out.print(mensage + ": ");
		return new Scanner(System.in).next();
	}
	
}
