/**
 * 
 */
package org.javabee.client;

import java.io.File;

import org.javabee.client.config.JavaBeeConfigs;

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
			result = result.trim().replace("\n", "");
			
			System.out.println("result: " + result);
			
			if (result.startsWith("0")) { // success
				JavaBeeLoaderClasspath.addFile(result.split(",")[1]);
				new JarClassLoader(new File(result.split(",")[1]));
			} else if (result.startsWith("1")) { // error
				throw new RuntimeException(result.split(",")[1]);
			} else throw new IllegalStateException("no valid value result was found");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
