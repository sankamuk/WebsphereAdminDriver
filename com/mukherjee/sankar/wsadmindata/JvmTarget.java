/**
 * Data structure representing Java EE Application Module's JVM target.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

public class JvmTarget {

	String jvmName;
	String nodeName;
	
	public JvmTarget(String nm, String nd) {
		this.jvmName=nm;
		this.nodeName=nd;
	}

	/**
	 * Get a JVM Name.
	 * @return String Name
	 */
	public String getJvmName() {
		return jvmName;
	}

	public void setJvmName(String jvmName) {
		this.jvmName = jvmName;
	}

	/**
	 * Get Node Name.
	 * @return String Name
	 */
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	@Override
	public String toString() {
		return "JvmTarget [jvmName=" + jvmName + ", nodeName=" + nodeName + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (!(obj instanceof JvmTarget)) {
			return false;
		}
		JvmTarget objJvm = (JvmTarget) obj;	
		if ((jvmName.equals(objJvm.getJvmName())) && (nodeName.equals(objJvm.getNodeName()))){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return jvmName.length()+nodeName.length();
	}
}
