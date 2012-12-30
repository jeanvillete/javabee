/**
 * 
 */
package org.javabee.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.javabee.data.xstream.XStreamFactory;
import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeTO;
import org.javabee.service.JavaBee;

/**
 * @author Jean Villete
 *
 */
public class JavaBeeBO implements JavaBee {

	@Override
	public List<JarTO> listJars() throws IOException {
		return new ArrayList<JarTO>(XStreamFactory.getInstance().getCurrentState().getJars().values());
	}

	@Override
	public JavaBeeTO getCurrentState() throws IOException {
		return XStreamFactory.getInstance().getCurrentState();
	}

	@Override
	public void updateState() throws IOException {
		XStreamFactory.getInstance().persistData();
	}
	
}
