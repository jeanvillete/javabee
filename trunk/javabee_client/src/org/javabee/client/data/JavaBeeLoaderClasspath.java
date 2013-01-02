/**
 * 
 */
package org.javabee.client.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Jean Villete
 * 
 * It's based from: http://www.prshanmu.com/2010/01/dynamically-adding-a-jar-file-to-classpath.html
 *
 */
public class JavaBeeLoaderClasspath {

	@SuppressWarnings("rawtypes")
	private static final Class[] parameters = new Class[]{URL.class};

	public static void addFile(String folderAddress) throws IOException {
		File f = new File(folderAddress);
		addFile(f);
	}

	public static void addFile(File folder) throws IOException {
		addURL(folder.toURI().toURL());
	}

	public static void addURL(URL urlFolder) throws IOException {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[]{urlFolder});
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	}
	
}
