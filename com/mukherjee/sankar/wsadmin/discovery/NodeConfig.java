/**
* This is a Data Structure representing a Node Configuration.
* @author Sankar Mukherjee
* @version 01, 12 March, 2015
*/

package com.mukherjee.sankar.wsadmin.discovery;

public class NodeConfig {
	String nodeName;
	String profileName;
	HostConfig nodeHost;
	
	public NodeConfig(String nm) {
		this.nodeName=nm;
		profileName="UNDEFINED";
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public HostConfig getNodeHost() {
		return nodeHost;
	}

	public void setNodeHost(HostConfig nodeHost) {
		this.nodeHost = nodeHost;
	}

	@Override
	public String toString() {
		return "\nNodeConfig [nodeName=" + nodeName + ", profileName="
				+ profileName + ", nodeHost=" + nodeHost + "]";
	}
	
	
}
