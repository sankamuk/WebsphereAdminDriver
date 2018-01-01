/**
 * Datastructure representing a particular JVM and statistics for all J2C Connection Factory resource configured for it.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

import java.util.ArrayList;

public class JvmJ2CCoonection {

	private String jvmName;
	private String nodeName;
	ArrayList<J2CConnection> j2cList;
	
	public JvmJ2CCoonection(String jvm, String nd) {
		this.jvmName=jvm;
		this.nodeName=nd;
		j2cList =  new ArrayList<J2CConnection>();
	}

	/**
	 * Get JVM Name.
	 * @return String Name
	 */
	public String getJvmName() {
		return jvmName;
	}

	public void setJvmName(String jvmName) {
		this.jvmName = jvmName;
	}

	/**
	 * Gets Node Name.
	 * @return String Name
	 */
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * Gets a List of J2C Connection Factory and there Connection Pool Status.
	 * @return List of J2CConnection
	 */
	public ArrayList<J2CConnection> getJ2cList() {
		return j2cList;
	}

	public void setJ2cList(ArrayList<J2CConnection> j2cList) {
		this.j2cList = j2cList;
	}

	@Override
	public String toString() {
		return "JvmJ2CCoonection [jvmName=" + jvmName + ", nodeName="
				+ nodeName + ", j2cList=" + j2cList + "]";
	}
	
}
