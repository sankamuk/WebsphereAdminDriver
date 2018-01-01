/**
 * This is a datastructure to represent a Application Information.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

import java.util.ArrayList;

public class ApplicationInformation {
	
	ArrayList<AppDeploymentTaskData> infoList;
	
	public ApplicationInformation() {
		infoList = new ArrayList<AppDeploymentTaskData>();
	}

	/**
	 * Returns a List for all Task and there corresponding properties.
	 * @return
	 */
	public ArrayList<AppDeploymentTaskData> getInfoList() {
		return infoList;
	}

	public void setInfoList(ArrayList<AppDeploymentTaskData> infoList) {
		this.infoList = infoList;
	}

	@Override
	public String toString() {
		String returnresult = "APPLICATION INFORMATIONS:";
		for (AppDeploymentTaskData data : infoList) {
			returnresult = returnresult +"\n"+ data;
		}
		return returnresult;
	}

	
}
