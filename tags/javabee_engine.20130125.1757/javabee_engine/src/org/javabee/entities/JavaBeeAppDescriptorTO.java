package org.javabee.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Jean Villete
 *
 */
public class JavaBeeAppDescriptorTO extends TO {

	private static final long serialVersionUID = 4870934814911296832L;
	
	private String								appName;
	private String								extractName;
	private Map<String, ManageDirectoryTO>		manageDependencies;
	
	// GETTERS AND SETTERS //
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getExtractName() {
		return extractName;
	}
	public void setExtractName(String extractName) {
		this.extractName = extractName;
	}
	public Map<String, ManageDirectoryTO> getManageDependencies() {
		if (manageDependencies == null) {
			manageDependencies = new HashMap<String, ManageDirectoryTO>();
		}
		return manageDependencies;
	}
	public void setManageDependencies(Map<String, ManageDirectoryTO> manageDependencies) {
		this.manageDependencies = manageDependencies;
	}
	
}
