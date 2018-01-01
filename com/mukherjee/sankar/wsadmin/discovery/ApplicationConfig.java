/**
* This is a Data Structure representing a Application Configuration.
* @author Sankar Mukherjee
* @version 01, 12 March, 2015
*/

package com.mukherjee.sankar.wsadmin.discovery;

import java.util.ArrayList;

public class ApplicationConfig {
	String applicationName;
	ArrayList<ClusterConfig> clusterTarget;
	
	public ApplicationConfig(String nm) {
		this.applicationName=nm;
		clusterTarget=new ArrayList<ClusterConfig>();
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public ArrayList<ClusterConfig> getClusterTarget() {
		return clusterTarget;
	}

	public void setClusterTarget(ArrayList<ClusterConfig> clusterTarget) {
		this.clusterTarget = clusterTarget;
	}

	@Override
	public String toString() {
		return "\nApplicationConfig [applicationName=" + applicationName
				+ ", clusterTarget=" + clusterTarget + "]";
	}
	
	

}
