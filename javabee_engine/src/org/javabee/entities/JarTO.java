/**
 * 
 */
package org.javabee.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jean Villete
 *
 */
public class JarTO extends LibraryTO {

	private static final long serialVersionUID = 2614992136788900602L;
	
	public JarTO() { }

	public JarTO(String id) {
		this.setId(id);
	}
	
	private String 						name;
	private String 						version;
	private String 						filename;
	private List<DependencyTO>			listDependencies;
	
	// GETTERS AND SETTERS //
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public List<DependencyTO> getListDependencies() {
		if (listDependencies == null) {
			listDependencies = new ArrayList<DependencyTO>();
		}
		return listDependencies;
	}
	public void setListDependencies(List<DependencyTO> listDependencies) {
		this.listDependencies = listDependencies;
	}
	
}
