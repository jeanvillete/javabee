/**
 * 
 */
package org.javabee.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javabee.data.xstream.XStreamFactory;
import org.javabee.entities.DependencyTO;
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

	@Override
	public List<JarTO> listToMount(String ids, Boolean injectDependencies) throws IOException {
		String[] splitedLibrary = ids.split(","); 
		List<JarTO> listLibraries = new ArrayList<JarTO>();
		for (String library : splitedLibrary) {
			library = library.trim();
			JarTO jar = this.getCurrentState().getJars().get(library);
			if (jar == null) {
				throw new IllegalStateException("There's no library for the id: " + library);
			}
			listLibraries.add(jar);
			if (injectDependencies) {
				for (JarTO jarDependency : getDependencies(jar, null)) {
					listLibraries.add(jarDependency);
				}
			}
		}
		
		return listLibraries;
	}
	
	private List<JarTO> getDependencies(JarTO jar, Map<String, JarTO> baseList) throws IOException {
		if (baseList == null) {
			baseList = new HashMap<String, JarTO>();
		}
		JavaBeeTO javabee = new JavaBeeBO().getCurrentState();
		for (DependencyTO d : jar.getListDependencies()) {
			JarTO jarDependency = javabee.getJars().get(d.getId());
			baseList.put(jarDependency.getId(), jarDependency);
			for (DependencyTO dependency : jarDependency.getListDependencies()) {
				getDependencies(javabee.getJars().get(dependency.getId()), baseList);
			}
		}
		return new ArrayList<JarTO>(baseList.values());
	}
	
}
