/**
 * 
 */
package org.javabee.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeTO;
import org.simplestructruedata.data.SSDContextManager;

/**
 * @author Jean Villete
 *
 */
public interface JavaBee {

	List<JarTO> listJars() throws IOException;
	JavaBeeTO getCurrentState() throws IOException;
	void updateState(JavaBeeTO javabeeTo) throws FileNotFoundException, IOException;
	List<JarTO> listToMount(String ids, Boolean injectDependencies) throws IOException;
	public List<JarTO> getDependencies(JarTO jar) throws IOException;
	public SSDContextManager getSSDFromJavaBeeTO(JavaBeeTO javabeeTo);
	JavaBeeTO getJavaBeeTOFromSSD(File sourceSSD);
	
}
