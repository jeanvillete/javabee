package org.javabee.commons;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.javabee.entities.JarTO;

public class JavaBeeUtils {
	
	/**
	* Method to create a Temporary Directory to decompress the OCR Data Training.
	* @return
	*/
	public static final File createTmpDir(String identifierTmpDir) {
		String tmpDirPath = System.getProperty("java.io.tmpdir");
		File ocrTmpDir = new File(tmpDirPath + identifierTmpDir + Long.toString(System.nanoTime()));
		ocrTmpDir.mkdir();
		return ocrTmpDir;
	}

	/**
	* @return the directory of the running jar
	*/
	public static File getBaseDir() {
		URL dir = JavaBeeUtils.class.getResource("/" + JavaBeeUtils.class.getName().replaceAll("\\.", "/") + ".class");
		File dbDir = new File(System.getProperty("user.dir"));
	
		try {
			if (dir.toString().startsWith("jar:")) {
			dir = new URL(dir.toString().replaceFirst("^jar:", "").replaceFirst("/[^/]+.jar!.*$", ""));
			dbDir = new File(dir.toURI());
			}
		} catch (MalformedURLException mue) {
		mue.printStackTrace();
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
		return dbDir;
	}
	
	public static String formatJarAddress(JarTO jar) {
		return  getBaseDir() +
				System.getProperty("file.separator") +
				JavaBeeConstants.LIBRARY_ROOT_ADDRESS +
				System.getProperty("file.separator") + jar.getName() + 
				System.getProperty("file.separator") + jar.getVersion() +
				System.getProperty("file.separator") + jar.getFilename();
	}
	
}
