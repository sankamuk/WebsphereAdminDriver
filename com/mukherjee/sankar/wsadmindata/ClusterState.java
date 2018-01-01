/**
 * Data structure for holding Cluster State.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

public class ClusterState {

	String clusterNm;
	String clusterType;
	String clusterState;
	String operationMode;
	
	public ClusterState(String clusNm, String clustype) {
		this.clusterNm=clusNm;
		this.clusterType=clustype;
		this.clusterState="Undefined";
		this.operationMode="Undefined";
	}

	/**
	 * Get the Cluster Name for the Cluster.
	 * @return String Name
	 */
	public String getClusterNm() {
		return clusterNm;
	}

	public void setClusterNm(String clusterNm) {
		this.clusterNm = clusterNm;
	}

	/**
	 * Get the type of Cluster.
	 * @return String (Dynamic,Static)
	 */
	public String getClusterType() {
		return clusterType;
	}

	public void setClusterType(String clusterType) {
		this.clusterType = clusterType;
	}

	/**
	 * Get the State of Cluster.
	 * @return String (STOPPED,RUNNING)
	 */
	public String getClusterState() {
		return clusterState;
	}

	public void setClusterState(String clusterState) {
		this.clusterState = clusterState;
	}

	/**
	 * Operation Mode for the Cluster. Only for Cluster type Dynamic.
	 * @return String (manual,automatic)
	 */
	public String getOperationMode() {
		return operationMode;
	}

	public void setOperationMode(String operationMode) {
		this.operationMode = operationMode;
	}

	@Override
	public String toString() {
		return "ClusterState [clusterNm=" + clusterNm + ", clusterType="
				+ clusterType + ", clusterState=" + clusterState
				+ ", operationMode=" + operationMode + "]";
	}
	
	
}
