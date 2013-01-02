/**
 * 
 */
package org.javabee.client.config;

/**
 * @author villjea
 *
 */
public class JavaBeeConfigsFullPath extends JavaBeeConfigs {

	public JavaBeeConfigsFullPath(String javabeeHome, String desiredLibraries) {
		this(javabeeHome, desiredLibraries, null);
	}
	
	public JavaBeeConfigsFullPath(String javabeeHome, String desiredLibraries, Boolean manageDependencies) {
		this.setSettings(javabeeHome, desiredLibraries, manageDependencies);
	}

}
