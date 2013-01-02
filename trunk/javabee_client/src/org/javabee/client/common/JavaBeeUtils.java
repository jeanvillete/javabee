/**
 * 
 */
package org.javabee.client.common;

/**
 * @author Jean Villete
 *
 */
public class JavaBeeUtils {

	private static String 				OS = null;
	private static final String			LINUX = "linux";
	private static final String			WINDOWS = "windows";
	
	private static String getOsName() {
		if(OS == null) {
			OS = System.getProperty("os.name"); 
		}
		return OS;
	}
	
	public static boolean isWindows() {
		return getOsName().toLowerCase().startsWith(WINDOWS);
	}

	public static boolean isUnix() {
		return getOsName().toLowerCase().startsWith(LINUX);
	}
	
}
