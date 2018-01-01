/**
 * This is a datastructure to represent a Applications Deployment Task Property.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

import java.util.ArrayList;

public class AppDeploymentTaskData {

	String taskName;
	ArrayList<PropertySet> taskProperty;
	
	public AppDeploymentTaskData(String nm) {
		this.taskName=nm;
		taskProperty = new ArrayList<PropertySet>();
	}

	/**
	 * Returns the type of Task.
	 * @return
	 */
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * Returns a List of Property(Name,Value) for the considered Task.
	 * @return ArrayList of PropertySet
	 */
	public ArrayList<PropertySet> getTaskProperty() {
		return taskProperty;
	}

	public void setTaskProperty(ArrayList<PropertySet> taskProperty) {
		this.taskProperty = taskProperty;
	}

	@Override
	public String toString() {
		String returnresult = "\n"+ taskName +"\n------------------------------------------------------------------------\n" ;
		for (PropertySet propertySet : taskProperty) {
			returnresult = returnresult + propertySet.toString() +", ";
		}
		return returnresult;
	}
	
	
}
