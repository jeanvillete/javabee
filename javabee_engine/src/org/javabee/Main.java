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
		if (!GeneralsHelper.isStringOk(consoleParameter.getValue("-from_client")) && !GeneralsHelper.isStringOk(consoleParameter.getValue("-fc"))) {
			System.out.println("JavaBee Org Library Manager Engine");
		}
		// help
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-help")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-h"))) {
			ServiceFactory.getInstance().getConsole().printHelp();
			return;
		}
		// version
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-version")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-v"))) {
			ServiceFactory.getInstance().getConsole().printVersion();
			return;
		}
		// list
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-list")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-l"))) {
			ServiceFactory.getInstance().getConsole().list();
			return;
		}
		// add
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-add"))) {
			ServiceFactory.getInstance().getConsole().add();
			return;
		}
		// delete
		// update
		// export
		// import
		// mount
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-mount"))) {
			ServiceFactory.getInstance().getConsole().mount(consoleParameter);
			return;
		}
		ServiceFactory.getInstance().getConsole().printHelp();
	}
}
