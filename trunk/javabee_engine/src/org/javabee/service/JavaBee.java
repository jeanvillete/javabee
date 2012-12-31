/**
 * 
 */
package org.javabee.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeTO;

/**
 * @author Jean Villete
 *
 */
public interface JavaBee {

	List<JarTO> listJars() throws IOException;
	JavaBeeTO getCurrentState() throws IOException;
	void updateState() throws FileNotFoundException, IOException;
	List<JarTO> listToMount(String ids, Boolean injectDependencies) throws IOException;
	
}
