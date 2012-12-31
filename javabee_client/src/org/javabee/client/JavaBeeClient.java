/**
 * 
 */
package org.javabee.client;

import org.javabee.config.JavaBeeConfigs;

/**
 * @author Jean Villete
 *
 */
public class JavaBeeClient {
	
	private JavaBeeConfigs 				configs;
	
	public JavaBeeClient(JavaBeeConfigs configs) {
		if (configs == null) {
			throw new IllegalArgumentException("Parameter configs cann't be null");
		}
		this.configs = configs;
	}
	
	public void loadClasspath() {
		
	}

}
