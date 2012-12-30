/**
 * 
 */
package org.javabee;

import java.io.File;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.com.tatu.helper.GeneralsHelper;
import org.com.tatu.helper.parameter.ConsoleParameters;
import org.javabee.commons.JavaBeeConstants;
import org.javabee.commons.JavaBeeUtils;
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
		System.out.println("JavaBee Org Library Manager Engine");
		ConsoleParameters consoleParameter = ConsoleParameters.getInstance(args);
		// help
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-help")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-h"))) {
			new Main().printHelp();
			return;
		}
		// version
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-version")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-v"))) {
			new Main().printVersion();
			return;
		}
		// list
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-list")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-l"))) {
			new Main().list();
			return;
		}
		// add
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-add"))) {
			new Main().add();
			return;
		}
		// delete
		// update
		// export
		// import
		// mount
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-mount"))) {
			new Main().mount(consoleParameter);
			return;
		}
		
		new Main().printHelp();
	}

	private void mount(ConsoleParameters consoleParameter) {
		try {
			System.out.print("command javabee -mount\n\n");
			JavaBee service = new JavaBeeBO();
			JavaBeeTO javabee = service.getCurrentState();
			// libraries parameter
			String libraries = consoleParameter.getValue("-libraries");
			if (!GeneralsHelper.isStringOk(libraries)) {
				libraries = consoleParameter.getValue("-lib");
				if (!GeneralsHelper.isStringOk(libraries)) {
					System.out.println("Parameter not -libraries or -lib not found, and it's mandatory to -mount command");
					return;
				}
			}
			// managed dependencies parameter
			String manageDependencies = consoleParameter.getValue("-manage_dependencies");
			if (!GeneralsHelper.isStringOk(manageDependencies)) {
				manageDependencies = consoleParameter.getValue("-md");
				if (!GeneralsHelper.isStringOk(manageDependencies)) {
					manageDependencies = "false";
				}
			}
			// check if the libraries exist
			String[] splitedLibrary = libraries.split(","); 
			for (String library : splitedLibrary) {
				library = library.trim();
				if (!javabee.getJars().containsKey(library)) {
					System.out.println("There's no library for the id: " + library);
					return;
				}
			}
			// create temporary directory
			File tmpDir = JavaBeeUtils.createTmpDir(JavaBeeConstants.JAVABEE_TMP_DIR);
			for (JarTO jar : javabee.getJars().values()) {
				File fileInsideLibrary = new File(JavaBeeUtils.formatJarAddress(jar));
				FileUtils.copyFileToDirectory(fileInsideLibrary, tmpDir);
			}
			System.out.println("0["+ tmpDir.getCanonicalPath() +"]");
		} catch (Exception e) {
			System.out.println("1[" + e.getMessage() + "]");
			return;
		}
	}

	private void list() {
		try {
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
		} catch (Exception e) {
			System.out.println("1[" + e.getMessage() + "]");
			return;
		}
	}
	
	private void add() {
		try {
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
			javabee.getJars().put(jar.getId(), jar);
			File fileInsideLibrary = new File(JavaBeeUtils.formatJarAddress(jar));
			FileUtils.copyFile(targetFile, fileInsideLibrary);
			service.updateState();
			System.out.println("Library added successfully! " + jar.getId());
		} catch (Exception e) {
			System.out.println("1[" + e.getMessage() + "]");
			return;
		}
	}
	
	private void printVersion() {
		System.out.println("command javabee -version: " + JavaBeeConstants.JAVA_BEE_VERSION);
	}
	
	private void printHelp() {
		StringBuffer helpMessage = new StringBuffer();
		helpMessage.append("command javabee -help\n");
		helpMessage.append("  -help[-h]                      show the possible actions with its needed parameters\n");
		helpMessage.append("  -version[-v]                   show the version of the current JavaBee\n");
		helpMessage.append("  -list[-l]                      show all libraries actually stored\n");
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
		return new Scanner(System.in).next();
	}
	
}
