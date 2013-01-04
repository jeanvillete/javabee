package org.javabee.client.common;

public interface JavaBeeConstants {

	String				FILE_SEPARATOR = System.getProperty("file.separator");
	String				OS_SCRIPT_EXTENSION = JavaBeeUtils.isUnix() ? ".sh" : JavaBeeUtils.isWindows() ? ".cmd" : "";
	String				APP_RELATIVE_PATH = "bin" + FILE_SEPARATOR + "javabee" + OS_SCRIPT_EXTENSION;
	String 				END_OF_LINE = "\n";
	
	// KNOWED EXTENSIONS
	String 				JAR_EXTENSION = ".jar";
	String 				CLASS_EXTENSION = ".class";
	
	// WEB LISTENER PARAMETERS
	String				WEB_PARAM_FULL_PATH = "org.javabee.client.web.JAVABEE_FULL_PATH";
	String				WEB_PARAM_ENVIRONMENT_VARIABLE = "org.javabee.client.web.JAVABEE_ENVIRONMENT_VARIABLE";
	String				WEB_PARAM_LIBRARIES = "org.javabee.client.web.JAVABEE_LIBRARIES";
	String				WEB_PARAM_MANAGE_DEPENDENCIES = "org.javabee.client.web.JAVABEE_MANAGE_DEPENDENCIES";
	
}
