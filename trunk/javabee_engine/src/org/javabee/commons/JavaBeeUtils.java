package org.javabee.commons;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.javabee.entities.JarTO;

public class JavaBeeUtils {
	
	/**
	* Method to create a Temporary Directory.
	* @return
	*/
	public static final File createTmpDir(String identifierTmpDir) {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"), identifierTmpDir + Long.toString(System.nanoTime()));
		tmpDir.mkdir();
		tmpDir.deleteOnExit();
		return tmpDir;
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
	
	public static String jarAddressInsideLibrary(JarTO jar) {
		return  getBaseDir() +
				System.getProperty("file.separator") +
				JavaBeeConstants.LIBRARY_ROOT_ADDRESS +
				formatJarAddress(jar);
	}
	
	public static String formatJarAddress(JarTO jar) {
		return System.getProperty("file.separator") + jar.getName() + 
				System.getProperty("file.separator") + jar.getVersion() +
				System.getProperty("file.separator") + jar.getFilename();
	}
	
	public static List<String> formatSetIdString(String setId) {
		String[] splitedLibrary = setId.split(",");
		List<String> returningList = new ArrayList<String>();
		for (String id : splitedLibrary) {
			id = id.trim();
			returningList.add(id);
		}
		return returningList;
	}
	
}
