/**
 * This is a datastructure for Listener Port Configuration.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

public class ListenerPortConfig {
	private String jvmName;
	private String nodeName;
	private String listenerPortName;
	private String listenerPortConfigId;
	private boolean isStarted;
	
	public ListenerPortConfig(String jvm, String node, String nm, String conf, boolean stat) {
		this.jvmName=jvm;
		this.nodeName=node;
		this.listenerPortName=nm;
		this.listenerPortConfigId=conf;
		this.isStarted=stat;
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
	 * Get Node Name.
	 * @return String Name
	 */
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * Gets the Listener Port Name.
	 * @return String Name
	 */
	public String getListenerPortName() {
		return listenerPortName;
	}

	public void setListenerPortName(String listenerPortName) {
		this.listenerPortName = listenerPortName;
	}

	/**
	 * Gets Listener Port Configuration Id.
	 * @return String Id
	 */
	public String getListenerPortConfigId() {
		return listenerPortConfigId;
	}

	public void setListenerPortConfigId(String listenerPortConfigId) {
		this.listenerPortConfigId = listenerPortConfigId;
	}

	/**
	 * Gets whether Listener Port is running. true if Running false otherwise.
	 * @return Boolean status
	 */
	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	@Override
	public String toString() {
		return "ListenerPortConfig [jvmName=" + jvmName + ", nodeName="
				+ nodeName + ", listenerPortName=" + listenerPortName
				+ ", listenerPortConfigId=" + listenerPortConfigId
				+ ", isStarted=" + isStarted + "]";
	}

	
}
