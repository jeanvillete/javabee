/**
 * 
 */
package org.javabee.client.config;

import org.javabee.client.common.JavaBeeConstants;

/**
 * @author Jean Villete
 *
 */
public abstract class JavaBeeConfigs {

	private String				javabeeTargetScript;
	private String				desiredLibraries;
	private Boolean				manageDependencies;
	
	void setSettings(String javabeeHome, String desiredLibraries, Boolean manageDependencies) {
		if (javabeeHome == null || javabeeHome.isEmpty()) {
			throw new IllegalArgumentException("parameter executableAddress is null or empty");
		}
		if (desiredLibraries == null || desiredLibraries.isEmpty()) {
			throw new IllegalArgumentException("parameter desiredLibraries is null or empty");
		}
		this.javabeeTargetScript = javabeeHome;
		if (!this.javabeeTargetScript.toString().endsWith("\\") && !this.javabeeTargetScript.toString().endsWith("/")) {
			this.javabeeTargetScript += JavaBeeConstants.FILE_SEPARATOR;
		}
		this.javabeeTargetScript += JavaBeeConstants.APP_RELATIVE_PATH;
		this.desiredLibraries = desiredLibraries;
		this.manageDependencies = manageDependencies;
	}
	
	// GETTERS AND SETTERS //
	public Boolean getManageDependencies() {
		return manageDependencies;
	}
	public String getDesiredLibraries() {
		return desiredLibraries;
	}
	public String getJavabeeTargetScript() {
		return javabeeTargetScript;
	}
}
