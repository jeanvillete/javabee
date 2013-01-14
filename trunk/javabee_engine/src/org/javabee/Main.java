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
		// version
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-version")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-v"))) {
			ServiceFactory.getInstance().getConsole().printVersion();
			return;
		}
		// list
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-list")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-l"))) {
			ServiceFactory.getInstance().getConsole().list(consoleParameter);
			return;
		}
		// add
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-add"))) {
			ServiceFactory.getInstance().getConsole().add();
			return;
		}
		// delete
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-delete")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-d"))) {
			String idJar = null;
			if ((idJar = consoleParameter.getValue("-delete")) != null) {
				ServiceFactory.getInstance().getConsole().delete(idJar);
			} else if ((idJar = consoleParameter.getValue("-d")) != null) {
				ServiceFactory.getInstance().getConsole().delete(idJar);
			}
			return;
		}
		// update
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-update")) || GeneralsHelper.isStringOk(consoleParameter.getValue("-u"))) {
			return;
		}
		// export
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-export"))) {
			return;
		}
		// import
		if (GeneralsHelper.isStringOk(consoleParameter.getValue("-import"))) {
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
		ServiceFactory.getInstance().getConsole().printHelp();
	}
}
