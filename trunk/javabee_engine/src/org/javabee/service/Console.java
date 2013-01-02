/**
 * 
 */
package org.javabee.service;

import org.com.tatu.helper.parameter.ConsoleParameters;

/**
 * @author villjea
 *
 */
public interface Console {
	
	void printHelp();
	void printVersion();
	void list(ConsoleParameters consoleParameter);
	void add();
	void delete(String idJar);
	void mount(ConsoleParameters consoleParameter);

}
