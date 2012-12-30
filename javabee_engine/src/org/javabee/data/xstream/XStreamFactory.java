/**
 * 
 */
package org.javabee.data.xstream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.javabee.constants.JavaBeeConstants;
import org.javabee.entities.DependencyTO;
import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeTO;

import com.thoughtworks.xstream.XStream;

/**
 * @author Jean Villete
 *
 */
public class XStreamFactory {
	
	private XStream 						xStream;
	private JavaBeeTO						currentState=null;
	private static XStreamFactory 			INSTANCE;
	
	private XStreamFactory() {
		this.xStream = new XStream();
		this.build();
	}
	
	private void build() {
		xStream.alias("javabee", JavaBeeTO.class);
		xStream.alias("dependency", DependencyTO.class);
		xStream.alias("jar", JarTO.class);
		
		xStream.addImplicitCollection(JarTO.class, "listDependencies");
		
		xStream.addImplicitMap(JavaBeeTO.class, "jars", JarTO.class, "id");

		xStream.useAttributeFor(JavaBeeTO.class, "version");
		xStream.useAttributeFor(DependencyTO.class, "id");
		xStream.useAttributeFor(JarTO.class, "id");
		xStream.useAttributeFor(JarTO.class, "name");
		xStream.useAttributeFor(JarTO.class, "version");
		xStream.useAttributeFor(JarTO.class, "filename");
	}
	
	public static XStreamFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new XStreamFactory();
		}
		return INSTANCE;
	}
	
	public void persistData() throws FileNotFoundException {
		OutputStream os = new FileOutputStream(new File(JavaBeeConstants.DATA_FILE_ADDRESS));
		this.xStream.toXML(this.currentState, os);
	}
	
	public JavaBeeTO getCurrentState() {
		if (this.currentState == null) {
			this.currentState = (JavaBeeTO) this.xStream.fromXML(new File(JavaBeeConstants.DATA_FILE_ADDRESS));
		}
		return this.currentState;
	}
	
}
