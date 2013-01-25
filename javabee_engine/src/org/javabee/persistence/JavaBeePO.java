/**
 * 
 */
package org.javabee.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.com.tatu.helper.GeneralsHelper;
import org.javabee.commons.JavaBeeConstants;
import org.javabee.commons.JavaBeeUtils;
import org.javabee.entities.DependencyTO;
import org.javabee.entities.JarTO;
import org.javabee.entities.JavaBeeAppDescriptorTO;
import org.javabee.entities.JavaBeeTO;
import org.javabee.entities.ManageDirectoryTO;
import org.simplestructruedata.data.SSDContextManager;
import org.simplestructruedata.data.SSDContextManager.SSDRootObject;
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
		return this.getJavaBeeTOFromSSD(this.getFileData());
	}
	
	public JavaBeeTO getJavaBeeTOFromSSD(File sourceSSD) {
		SSDContextManager context = SSDContextManager.build(sourceSSD);
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
	
	public SSDContextManager getSSDFromJavaBeeTO(JavaBeeTO javabeeTo) {
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
		return context;
	}
	
	public void updateState(JavaBeeTO javabeeTo) throws IOException {
		this.getSSDFromJavaBeeTO(javabeeTo).toFile(this.getFileData());
	}
	
	public JavaBeeAppDescriptorTO getDescriptorFromSSD(File sourceSSD) throws IOException {
		SSDContextManager context = SSDContextManager.build(sourceSSD);
		SSDRootObject root = context.getRootObject();
		JavaBeeAppDescriptorTO javaBeeDescriptor = new JavaBeeAppDescriptorTO();
		javaBeeDescriptor.setAppName(root.getLeaf("app-name").getValue());
		javaBeeDescriptor.setExtractName(root.getLeaf("extract-name").getValue());
		
		for (SSDObject ssdManageDirectory : root.getArray("manage-libraries").getElements()) {
			SSDObjectNode nodeManageDirectory = (SSDObjectNode) ssdManageDirectory;
			ManageDirectoryTO manageDirectory = new ManageDirectoryTO();
			manageDirectory.setTargetDirectory(nodeManageDirectory.getLeaf("target-directory").getValue());
			manageDirectory.setInjectDependencies(GeneralsHelper.isBooleanTrue(nodeManageDirectory.getLeaf("inject-dependencies").getValue()));
			
			// getting array set-id
			SSDObjectArray arrayIds = nodeManageDirectory.getArray("set-id");
			if (GeneralsHelper.isCollectionOk(arrayIds.getElements())) {
				for (int i = 0; i < arrayIds.getSize(); i++) {
					SSDObjectLeaf leafIds = arrayIds.getLeaf(i);
					manageDirectory.getSetIds().add(leafIds.getValue());
				}
			}
			
			// getting array selective-removing
			for (SSDObject ssdSelectiveRemoving : nodeManageDirectory.getArray("selective-removing").getElements()) {
				manageDirectory.getSelectiveRemoving().add(((SSDObjectLeaf) ssdSelectiveRemoving).getValue());
			}
			
			javaBeeDescriptor.getManageDependencies().put(manageDirectory.getTargetDirectory(), manageDirectory);
		}
		
		return javaBeeDescriptor;
	}
	
	public SSDContextManager getSSDFromDescriptor(JavaBeeAppDescriptorTO javaBeeDescriptor) {
		SSDContextManager ssdCtx = SSDContextManager.build();
		SSDRootObject root = ssdCtx.getRootObject();
		root.addAttribute(new SSDObjectLeaf("app-name", javaBeeDescriptor.getAppName()));
		root.addAttribute(new SSDObjectLeaf("extract-name", javaBeeDescriptor.getExtractName()));
		
		SSDObjectArray arrayManageLibrary = new SSDObjectArray("manage-libraries");
		for (ManageDirectoryTO manageDirectory : javaBeeDescriptor.getManageDependencies().values()) {
			SSDObjectNode nodeManageDirectory = new SSDObjectNode("node");
			
			nodeManageDirectory.addAttribute(new SSDObjectLeaf("target-directory", manageDirectory.getTargetDirectory()));
			nodeManageDirectory.addAttribute(new SSDObjectLeaf("inject-dependencies", manageDirectory.getInjectDependencies().toString()));
			
			// array set-id
			SSDObjectArray arraySetIds = new SSDObjectArray("set-id");
			for (String id : manageDirectory.getSetIds()) {
				arraySetIds.addElement(new SSDObjectLeaf(id, id));
			}
			nodeManageDirectory.addAttribute(arraySetIds);
			
			// array selective-removing
			SSDObjectArray arraySelectiveRemoving = new SSDObjectArray("selective-removing");
			for (String selectiveRemoving : manageDirectory.getSelectiveRemoving()) {
				arraySelectiveRemoving.addElement(new SSDObjectLeaf(selectiveRemoving, selectiveRemoving));
			}
			nodeManageDirectory.addAttribute(arraySelectiveRemoving);
			
			arrayManageLibrary.addElement(nodeManageDirectory);
		}
		root.addAttribute(arrayManageLibrary);
		
		return ssdCtx;
	}
	
	public List<JarTO> listToMount(String ids, Boolean injectDependencies) throws IOException {
		List<JarTO> listLibraries = new ArrayList<JarTO>();
		for (String library : JavaBeeUtils.formatSetIdString(ids)) {
			library = library.trim();
			JarTO jar = this.getCurrentState().getJars().get(library);
			if (jar == null) {
				throw new IllegalStateException("There's no library for the id: " + library);
			}
			listLibraries.add(jar);
			if (injectDependencies) {
				for (JarTO jarDependency : this.getDependencies(jar, null)) {
					listLibraries.add(jarDependency);
				}
			}
		}
		
		return listLibraries;
	}
	
	public List<JarTO> getDependencies(JarTO jar) throws IOException {
		return this.getDependencies(jar, null);
	}
	
	private List<JarTO> getDependencies(JarTO jar, Map<String, JarTO> baseList) throws IOException {
		if (baseList == null) {
			baseList = new HashMap<String, JarTO>();
		}
		JavaBeeTO javabee = this.getCurrentState();
		for (DependencyTO d : jar.getListDependencies()) {
			JarTO jarDependency = javabee.getJars().get(d.getId());
			baseList.put(jarDependency.getId(), jarDependency);
			for (DependencyTO dependency : jarDependency.getListDependencies()) {
				getDependencies(javabee.getJars().get(dependency.getId()), baseList);
			}
		}
		return new ArrayList<JarTO>(baseList.values());
	}
	
	private File getFileData() throws IOException {
		return new File(JavaBeeUtils.getBaseDir().getCanonicalPath() +
				System.getProperty("file.separator") +
				JavaBeeConstants.DATA_FILE_ADDRESS);
	}
	
}
