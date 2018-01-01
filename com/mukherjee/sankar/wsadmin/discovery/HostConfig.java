/**
* This is a Data Structure representing a Host Configuration.
* @author Sankar Mukherjee
* @version 01, 12 March, 2015
*/

package com.mukherjee.sankar.wsadmin.discovery;

public class HostConfig {
	String hostName;
	String hostIP;
	String hostUsr;
	String hostPasswd;
	String wasHome;
	
	public HostConfig(String nm) {
		this.hostName=nm;
		this.hostIP="UNDEFINED";
		this.hostUsr="UNDEFINED";
		this.hostPasswd="UNDEFINED";
		this.wasHome="UNDEFINED";
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	public String getHostUsr() {
		return hostUsr;
	}

	public void setHostUsr(String hostUsr) {
		this.hostUsr = hostUsr;
	}

	public String getHostPasswd() {
		return hostPasswd;
	}

	public void setHostPasswd(String hostPasswd) {
		this.hostPasswd = hostPasswd;
	}

	public String getWasHome() {
		return wasHome;
	}

	public void setWasHome(String wasHome) {
		this.wasHome = wasHome;
	}

	@Override
	public String toString() {
		return "HostConfig [hostName=" + hostName + ", hostIP=" + hostIP
				+ ", hostUsr=" + hostUsr + ", hostPasswd=" + hostPasswd
				+ ", wasHome=" + wasHome + "]";
	}
		
}
