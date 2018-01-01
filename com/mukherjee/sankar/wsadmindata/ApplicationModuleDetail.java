/**
 * Data structure representing Java EE Application Module and the deployed target.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

import java.util.ArrayList;

public class ApplicationModuleDetail {

	String applicationName;
	String moduleName;
	ArrayList targetList;
	
	public ApplicationModuleDetail(String nm, String md) {
		this.applicationName=nm;
		this.moduleName=md;
		targetList = new ArrayList();
	}

	/**
	 * Get the Name of the Java EE Application.
	 * @return String Name
	 */
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * Get the Name of the Module Corresponding to the Java EE Application.
	 * @return String Name
	 */
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * Get the List of Target where the Module has been deployed.
	 * @return ArrayList(Generic) of JVM Target
	 */
	public ArrayList getTargetList() {
		return targetList;
	}

	public void setTargetList(ArrayList targetList) {
		this.targetList = targetList;
	}

	@Override
	public String toString() {
		return "ApplicationModuleDetail [applicationName=" + applicationName
				+ ", moduleName=" + moduleName + ", targetList=" + targetList
				+ "]";
	}
	
	
}
