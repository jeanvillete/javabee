/**
 * 
 */
package org.javabee.client.data;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Jean Villete
 * 
 * Got from: http://www.prshanmu.com/2010/01/dynamically-adding-a-jar-file-to-classpath.html
 *
 */
public class JavaBeeLoaderClasspath {

	public static void loadClass(String filePath) throws IOException {
		URLClassLoader sysLoader;
		URL u = null;
		Class sysclass;
		Class[] parameters;
		try {
		u = new URL("file://" + filePath);
		sysLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		sysclass = URLClassLoader.class;
		parameters = new Class[] { URL.class };
		Method method = sysclass.getDeclaredMethod("addURL", parameters);
		method.setAccessible(true);
		method.invoke(sysLoader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			throw new IOException("Error, could not add file " + u.toExternalForm() + " to system classloader");
		}
	}
	
}
