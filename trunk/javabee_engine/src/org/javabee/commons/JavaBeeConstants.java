package org.javabee.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

public interface JavaBeeConstants {

	String JAVABEE_VERSION = "1.0";
	String JAVABEE_DATA_FILE = "javabee.data.ssd";
	String DATA_FILE_ADDRESS = "data" + System.getProperty("file.separator") + JAVABEE_DATA_FILE;
	String JAVABEE_LIBRARY = "library";
	String LIBRARY_ROOT_ADDRESS = ".." + System.getProperty("file.separator") + JAVABEE_LIBRARY;
	String JAVABEE_TMP_DIR = "JavaBeeTmpDir";
	String BOOLEAN_CONSOLE_OPTIONS = "(true/1/yes/y or false/0/no/n)";
	
	String CURRENT_DIRECTORY_PARAM = "-current_directory";
	String JAVABEE_FILE_DESCRIPTOR = "javabee.desc.ssd";
	String JAVABEE_FILE_EXTENSION = ".jbf";
	String JAVABEE_STATE_FILE_NAME = "javabee_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jbs";
}
