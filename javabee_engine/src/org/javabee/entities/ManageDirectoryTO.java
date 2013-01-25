package org.javabee.entities;

import java.util.ArrayList;
import java.util.List;

import org.com.tatu.helper.GeneralsHelper;

/**
 * 
 * @author Jean Villete
 *
 */
public class ManageDirectoryTO extends TO {

	private static final long serialVersionUID = 8992937601173387959L;
	
	private String					targetDirectory;
	private Boolean					injectDependencies;
	private List<String>			selectiveRemoving;
	private List<String>			setIds;
	
	// GETTERS AND SETTERS //
	public String getTargetDirectory() {
		return targetDirectory;
	}
	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}
	public Boolean getInjectDependencies() {
		return GeneralsHelper.isBooleanTrue(injectDependencies);
	}
	public void setInjectDependencies(Boolean injectDependencies) {
		this.injectDependencies = GeneralsHelper.isBooleanTrue(injectDependencies);
	}
	public List<String> getSelectiveRemoving() {
		if (selectiveRemoving == null) {
			selectiveRemoving = new ArrayList<String>();
		}
		return selectiveRemoving;
	}
	public void setSelectiveRemoving(List<String> selectiveRemoving) {
		this.selectiveRemoving = selectiveRemoving;
	}
	public List<String> getSetIds() {
		if (setIds == null) {
			setIds = new ArrayList<String>();
		}
		return setIds;
	}
	public void setSetIds(List<String> setIds) {
		this.setIds = setIds;
	}
	
}
