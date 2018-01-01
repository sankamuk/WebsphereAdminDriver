/**
 * Data structure representing Java EE Application Module's Cluster target.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

public class ClusterTarget {

	String clusterName;
	
	public ClusterTarget(String nm) {
		this.clusterName=nm;
	}

	/**
	 * Name of the Cluster.
	 * @return String Name
	 */
	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	@Override
	public String toString() {
		return "ClusterTarget [clusterName=" + clusterName + "]";
	}
	
	
}
