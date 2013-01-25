/**
 * 
 */
package org.javabee.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeAppDescriptorTO;
import org.javabee.entities.JavaBeeTO;
import org.javabee.persistence.JavaBeePO;
import org.javabee.service.JavaBee;
import org.simplestructruedata.data.SSDContextManager;

/**
 * @author Jean Villete
 *
 */
public class JavaBeeBO implements JavaBee {

	@Override
	public List<JarTO> listJars() throws IOException {
		return new JavaBeePO().listJars();
	}

	@Override
	public JavaBeeTO getCurrentState() throws IOException {
		return new JavaBeePO().getCurrentState();
	}

	@Override
	public void updateState(JavaBeeTO javabeeTo) throws IOException {
		new JavaBeePO().updateState(javabeeTo);
	}

	@Override
	public List<JarTO> listToMount(String ids, Boolean injectDependencies) throws IOException {
		return new JavaBeePO().listToMount(ids, injectDependencies);
	}
	
	@Override
	public JavaBeeTO getJavaBeeTOFromSSD(File sourceSSD) {
		return new JavaBeePO().getJavaBeeTOFromSSD(sourceSSD);
	}
	
	@Override
	public List<JarTO> getDependencies(JarTO jar) throws IOException {
		return new JavaBeePO().getDependencies(jar);
	}

	@Override
	public SSDContextManager getSSDFromJavaBeeTO(JavaBeeTO javabeeTo) {
		return new JavaBeePO().getSSDFromJavaBeeTO(javabeeTo);
	}
	
	@Override
	public JavaBeeAppDescriptorTO getDescriptorFromSSD(File sourceSSD) throws IOException {
		return new JavaBeePO().getDescriptorFromSSD(sourceSSD);
	}
	
	@Override
	public SSDContextManager getSSDFromDescriptor(JavaBeeAppDescriptorTO javaBeeDescriptor) {
		return new JavaBeePO().getSSDFromDescriptor(javaBeeDescriptor);
	}
}
