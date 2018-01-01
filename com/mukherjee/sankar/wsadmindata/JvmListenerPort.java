package com.mukherjee.sankar.wsadmindata;

import java.util.ArrayList;

public class JvmListenerPort {
	
	private String jvmName;
	private String nodeName;
	ArrayList<ListenerPortConfig> lpList;
	
	public JvmListenerPort(String jvm, String nd) {
		this.jvmName=jvm;
		this.nodeName=nd;
		lpList =  new ArrayList<ListenerPortConfig>();
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
	 * Get a Node Name.
	 * @return String Name
	 */
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * Get a List of Listener Port Configured for a JVM and there status.
	 * @return List of ListenerPortConfig
	 */
	public ArrayList<ListenerPortConfig> getLpList() {
		return lpList;
	}

	public void setLpList(ArrayList<ListenerPortConfig> lpList) {
		this.lpList = lpList;
	}

	@Override
	public String toString() {
		return "JvmListenerPort [jvmName=" + jvmName + ", nodeName=" + nodeName
				+ ", lpList=" + lpList + "]";
	}

	
}
