package org.javabee.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Jean Villete
 *
 */
public class JavaBeeTO extends TO {

	private static final long serialVersionUID = 203443483812738762L;
	
	public JavaBeeTO() { }
	
	public JavaBeeTO(String version) {
		this.version = version;
	}
	
	private String 						version;
	private Map<String, JarTO>			jars; // key=jarto.id, value=jarto

	// GETTERS AND SETTERS //
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, JarTO> getJars() {
		if (jars == null) {
			jars = new HashMap<String, JarTO>();
		}
		return jars;
	}

	public void setJars(Map<String, JarTO> jars) {
		this.jars = jars;
	}
	
}
