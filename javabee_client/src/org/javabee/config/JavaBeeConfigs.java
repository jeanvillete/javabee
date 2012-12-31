/**
 * 
 */
package org.javabee.config;

/**
 * @author Jean Villete
 *
 */
public class JavaBeeConfigs {

	private String				executableAddress;
	private String				desiredLibraries;
	private Boolean				manageDependencies;
	
	public JavaBeeConfigs(String executableAddress, String desiredLibraries, Boolean manageDependencies) {
		if (executableAddress == null || executableAddress.isEmpty()) {
			throw new IllegalArgumentException("parameter executableAddress is null or empty");
		}
		if (desiredLibraries == null || desiredLibraries.isEmpty()) {
			throw new IllegalArgumentException("parameter desiredLibraries is null or empty");
		}
		if (manageDependencies == null) {
			throw new IllegalArgumentException("parameter manageDependencies is null");
		}
		this.executableAddress = executableAddress;
		this.desiredLibraries = desiredLibraries;
		this.manageDependencies = manageDependencies;
	}
	// GETTERS AND SETTERS //
	public Boolean getManageDependencies() {
		return manageDependencies;
	}
	public String getExecutableAddress() {
		return executableAddress;
	}
	public String getDesiredLibraries() {
		return desiredLibraries;
	}
}
