/**
 * 
 */
package org.javabee.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.com.tatu.helper.GeneralsHelper;
import org.javabee.commons.JavaBeeConstants;
import org.javabee.commons.JavaBeeUtils;
import org.javabee.entities.DependencyTO;
import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeTO;
import org.simplestructruedata.data.SSDContextManager;
import org.simplestructruedata.entities.SSDObject;
import org.simplestructruedata.entities.SSDObjectArray;
import org.simplestructruedata.entities.SSDObjectLeaf;
import org.simplestructruedata.entities.SSDObjectNode;

/**
 * @author Jean Villete
 *
 */
public class JavaBeePO {

	public List<JarTO> listJars() throws IOException {
		return new ArrayList<JarTO>(this.getCurrentState().getJars().values());
	}
	
	public JavaBeeTO getCurrentState() throws IOException {
		SSDContextManager context = SSDContextManager.build(this.getFileData());
		SSDObjectNode javabee = context.getRootObject().getNode("javabee");
		
		JavaBeeTO javabeeTo = new JavaBeeTO(javabee.getLeaf("version").getValue());
		
		SSDObjectArray libraries = javabee.getArray("libraries");
		for (int i = 0; i < libraries.getSize(); i ++) {
			SSDObjectNode library = libraries.getNode(i);
			JarTO jarTo = new JarTO(library.getLeaf("id").getValue());
			jarTo.setName(library.getLeaf("name").getValue());
			jarTo.setVersion(library.getLeaf("version").getValue());
			jarTo.setFilename(library.getLeaf("filename").getValue());
			
			SSDObject dependencies = library.get("dependencies");
			if (dependencies != null) {
				SSDObjectArray dependenciesArray = (SSDObjectArray) dependencies;
				for (int j = 0; j < dependenciesArray.getSize(); j++) {
					jarTo.addDependency(new DependencyTO(dependenciesArray.getLeaf(j).getValue()));
				}
			}
			javabeeTo.addJar(jarTo);
		}
		
		return javabeeTo;
	}
	
	public void updateState(JavaBeeTO javabeeTo) throws IOException {
		SSDObjectNode javabee = new SSDObjectNode("javabee");
		javabee.addAttribute(new SSDObjectLeaf("version", javabeeTo.getVersion()));
		
		SSDObjectArray libraries = new SSDObjectArray("libraries");
		for (JarTO jarTo : javabeeTo.getJars().values()) {
			SSDObjectNode library = new SSDObjectNode(jarTo.getId());
			library.addAttribute(new SSDObjectLeaf("id", jarTo.getId()));
			library.addAttribute(new SSDObjectLeaf("name", jarTo.getName()));
			library.addAttribute(new SSDObjectLeaf("version", jarTo.getVersion()));
			library.addAttribute(new SSDObjectLeaf("filename", jarTo.getFilename()));
			
			if (GeneralsHelper.isCollectionOk(jarTo.getListDependencies())) {
				SSDObjectArray dependencies = new SSDObjectArray("dependencies");
				for (DependencyTO dependency : jarTo.getListDependencies()) {
					dependencies.addElement(new SSDObjectLeaf(dependency.getId(), dependency.getId()));
				}
				library.addAttribute(dependencies);
			}
			
			libraries.addElement(library);
		}
		javabee.addAttribute(libraries);
		
		SSDContextManager context = SSDContextManager.build();
		context.getRootObject().addAttribute(javabee);
		context.toFile(this.getFileData());
	}
	
	private File getFileData() throws IOException {
		return new File(JavaBeeUtils.getBaseDir().getCanonicalPath() +
				System.getProperty("file.separator") +
				JavaBeeConstants.DATA_FILE_ADDRESS);
	}
	
}
