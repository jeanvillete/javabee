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
	void list();
	void add();
	void mount(ConsoleParameters consoleParameter);

}
