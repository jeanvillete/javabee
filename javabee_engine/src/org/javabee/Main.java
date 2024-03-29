/**
 * 
 */
package org.javabee;

import org.com.tatu.helper.GeneralsHelper;
import org.com.tatu.helper.parameter.ConsoleParameters;
import org.javabee.service.factory.ServiceFactory;

/**
 * @author Jean Villete
 *
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConsoleParameters consoleParameter = ConsoleParameters.getInstance(args);
		System.out.println("JavaBee Org - Library Manager - Engine");
		
		// help
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-help")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-h"))) {
			ServiceFactory.getInstance().getConsole().printHelp();
			return;
		}
		// list
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-list")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-l"))) {
			ServiceFactory.getInstance().getConsole().list(consoleParameter);
			return;
		}
		// add
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-add"))) {
			ServiceFactory.getInstance().getConsole().add(consoleParameter);
			return;
		}
		// delete
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-delete")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-d"))) {
			ServiceFactory.getInstance().getConsole().delete(consoleParameter);
			return;
		}
		// update
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-update")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-u"))) {
			ServiceFactory.getInstance().getConsole().update(consoleParameter);
			return;
		}
		// export
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-export"))) {
			ServiceFactory.getInstance().getConsole().export(consoleParameter);
			return;
		}
		// import
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-import"))) {
			ServiceFactory.getInstance().getConsole().importState(consoleParameter);
			return;
		}
		// libraries
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-libraries"))) {
			ServiceFactory.getInstance().getConsole().libraries(consoleParameter);
			return;
		}
		// mount
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-mount"))) {
			ServiceFactory.getInstance().getConsole().mount(consoleParameter);
			return;
		}
		// unmount
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-unmount"))) {
			ServiceFactory.getInstance().getConsole().unmount(consoleParameter);
			return;
		}
		// app_descriptor
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-app_descriptor"))) {
			ServiceFactory.getInstance().getConsole().appDescriptor(consoleParameter);
			return;
		}
		// version
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-version")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-v"))) {
			ServiceFactory.getInstance().getConsole().printVersion();
			return;
		}
		ServiceFactory.getInstance().getConsole().printHelp();
	}
}
