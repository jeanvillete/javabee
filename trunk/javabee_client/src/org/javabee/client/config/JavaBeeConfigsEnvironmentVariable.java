/**
 * 
 */
package org.javabee.client.config;

/**
 * @author villjea
 *
 */
public class JavaBeeConfigsEnvironmentVariable extends JavaBeeConfigs {

	public JavaBeeConfigsEnvironmentVariable(String keyEnvironmentVariable, String desiredLibraries) {
		this(keyEnvironmentVariable, desiredLibraries, null);
	}
	
	public JavaBeeConfigsEnvironmentVariable(String keyEnvironmentVariable, String desiredLibraries, Boolean manageDependencies) {
		if (keyEnvironmentVariable == null || keyEnvironmentVariable.isEmpty()) {
			throw new IllegalArgumentException("Some problem with keyEnvironmentVariable passed as parameter! It's is null or empty.");
		}
		String javabeeHome = System.getenv(keyEnvironmentVariable);
		if (javabeeHome != null && !javabeeHome.isEmpty()) {
			this.setSettings(javabeeHome, desiredLibraries, manageDependencies);
		} else {
			throw new RuntimeException("There's no valid value for \"Environment Variable\" " + keyEnvironmentVariable + " and right now it is required.");
		}
	}

}
