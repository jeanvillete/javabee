/**
 * 
 */
package org.javabee.service;

import org.com.tatu.helper.parameter.ConsoleParameters;

/**
 * @author Jean Villete
 *
 */
public interface Console {
	
	void printHelp();
	void printVersion();
	void list(ConsoleParameters consoleParameter);
	void add(ConsoleParameters consoleParameter);
	void libraries(ConsoleParameters consoleParameter);
	void mount(ConsoleParameters consoleParameter);
	void unmount(ConsoleParameters consoleParameter);
	void update(ConsoleParameters consoleParameter);
	void delete(ConsoleParameters consoleParameter);

}
