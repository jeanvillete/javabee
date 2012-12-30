/**
 * 
 */
package org.javabee.service;

import java.io.FileNotFoundException;
import java.util.List;

import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeTO;

/**
 * @author Jean Villete
 *
 */
public interface JavaBee {

	List<JarTO> listJars();
	JavaBeeTO getCurrentState();
	void updateState() throws FileNotFoundException;
	
}
