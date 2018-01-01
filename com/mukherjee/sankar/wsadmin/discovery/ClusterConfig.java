/**
* This is a Data Structure representing a Cluster Configuration.
* @author Sankar Mukherjee
* @version 01, 12 March, 2015
*/

package com.mukherjee.sankar.wsadmin.discovery;

import java.util.ArrayList;


public class ClusterConfig {

	String clusterName;
	ArrayList<NodeConfig> nodeList;
	
	public ClusterConfig(String nm) {
		this.clusterName=nm;
		this.nodeList=new ArrayList<NodeConfig>();
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public ArrayList<NodeConfig> getNodeList() {
		return nodeList;
	}

	public void setNodeList(ArrayList<NodeConfig> nodeList) {
		this.nodeList = nodeList;
	}

	@Override
	public String toString() {
		return "ClusterConfig [clusterName=" + clusterName + ", nodeList="
				+ nodeList + "]";
	}	
	
}
