/**
 * 
 */
package org.javabee.client;

import org.javabee.client.config.JavaBeeConfigs;
import org.javabee.client.data.JavaBeeLoaderClasspath;

/**
 * @author Jean Villete
 *
 */
public class JavaBeeClient {
	
	private JavaBeeExecutable			executable;
	
	public JavaBeeClient(JavaBeeConfigs configs) {
		if (configs == null) {
			throw new IllegalArgumentException("Parameter configs cann't be null");
		}
		this.executable = new JavaBeeExecutable(configs);
	}
	
	public void loadClasspath() {
		try {
			String result = this.executable.execute();
			if (result.startsWith("0")) { // success
				JavaBeeLoaderClasspath.loadClass(result.split(",")[1]);
			} else if (result.startsWith("1")) { // error
				throw new RuntimeException(result.split(",")[1]);
			} else throw new IllegalStateException("no valid value result was found");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
