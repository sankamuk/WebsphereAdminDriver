/**
 * This is a datastructure for J2C Connection Factory Configuration.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

public class J2CConnection {
	private String jvmName;
	private String nodeName;
	private String cfName;
	private String cfConfigId;
	private int cfPoolMaxSz;
	private int cfPoolMinSz;

	public J2CConnection(String jvm, String node, String nm, String conf, int mx, int act) {
		this.jvmName=jvm;
		this.nodeName=node;
		this.cfName=nm;
		this.cfConfigId=conf;
		this.cfPoolMaxSz=mx;
		this.cfPoolMinSz=act;
	}
	
	/**
	 * Gets JVM Name.
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
	 * Get Connection Pool Minimum Size.
	 * @return Integer Number
	 */
	public int getCfPoolMinSz() {
		return cfPoolMinSz;
	}

	public void setCfPoolMinSz(int cfPoolMinSz) {
		this.cfPoolMinSz = cfPoolMinSz;
	}

	/**
	 * Gets J2C Connection Factory Name.
	 * @return String Name
	 */
	public String getCfName() {
		return cfName;
	}

	public void setCfName(String cfName) {
		this.cfName = cfName;
	}

	/**
	 * Get Connection factory Configuration Id. 
	 * @return String
	 */
	public String getCfConfigId() {
		return cfConfigId;
	}

	public void setCfConfigId(String cfConfigId) {
		this.cfConfigId = cfConfigId;
	}

	/**
	 * Get Connection Pool Maximum Size.
	 * @return Integer Number
	 */
	public int getCfPoolMaxSz() {
		return cfPoolMaxSz;
	}

	public void setCfPoolMaxSz(int cfPoolMaxSz) {
		this.cfPoolMaxSz = cfPoolMaxSz;
	}

	@Override
	public String toString() {
		return "J2CConnection [jvmName=" + jvmName + ", nodeName=" + nodeName
				+ ", cfName=" + cfName + ", cfConfigId=" + cfConfigId
				+ ", cfPoolMaxSz=" + cfPoolMaxSz + ", cfPoolMinSx="
				+ cfPoolMinSz + "]";
	}

}
