/**
 * 
 */
package org.javabee.service.factory;

import org.javabee.service.Console;
import org.javabee.service.JavaBee;
import org.javabee.service.impl.ConsoleBO;
import org.javabee.service.impl.JavaBeeBO;

/**
 * @author Jean Villete
 *
 */
public class ServiceFactory {

	private static ServiceFactory INSTANCE;
	private ServiceFactory() {
	}
	
	public static ServiceFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ServiceFactory();
		}
		return INSTANCE;
	}
	
	public Console getConsole() {
		return new ConsoleBO();
	}
	
	public JavaBee getJavaBee() {
		return new JavaBeeBO();
	}
}
