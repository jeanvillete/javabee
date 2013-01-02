package org.javabee.client.common;

public interface JavaBeeConstants {

	String				FILE_SEPARATOR = System.getProperty("file.separator");
	String				OS_SCRIPT_EXTENSION = JavaBeeUtils.isUnix() ? ".sh" : JavaBeeUtils.isWindows() ? ".cmd" : "";
	String				APP_RELATIVE_PATH = "bin" + FILE_SEPARATOR + "javabee" + OS_SCRIPT_EXTENSION;
	String 				END_OF_LINE = "\n";
	
}
