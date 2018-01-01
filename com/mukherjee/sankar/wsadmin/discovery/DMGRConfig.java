/**
* This is a Data Structure representing a DMGR Process Configuration.
* @author Sankar Mukherjee
* @version 01, 12 March, 2015
*/

package com.mukherjee.sankar.wsadmin.discovery;

public class DMGRConfig {
	int dmgrmgmtPort;
	String dmgrmgmtIP;
	String dmgrmgmtUsr;
	String dmgrmgmtPasswd;
	NodeConfig dmgrNode;
	
	public DMGRConfig(String ip, int prt, String usr, String pswd) {
		this.dmgrmgmtIP=ip;
		this.dmgrmgmtPort=prt;
		this.dmgrmgmtUsr=usr;
		this.dmgrmgmtPasswd=pswd;
		this.dmgrNode=null;
	}

	public int getDmgrmgmtPort() {
		return dmgrmgmtPort;
	}

	public void setDmgrmgmtPort(int dmgrmgmtPort) {
		this.dmgrmgmtPort = dmgrmgmtPort;
	}

	public String getDmgrmgmtIP() {
		return dmgrmgmtIP;
	}

	public void setDmgrmgmtIP(String dmgrmgmtIP) {
		this.dmgrmgmtIP = dmgrmgmtIP;
	}

	public String getDmgrmgmtUsr() {
		return dmgrmgmtUsr;
	}

	public void setDmgrmgmtUsr(String dmgrmgmtUsr) {
		this.dmgrmgmtUsr = dmgrmgmtUsr;
	}

	public String getDmgrmgmtPasswd() {
		return dmgrmgmtPasswd;
	}

	public void setDmgrmgmtPasswd(String dmgrmgmtPasswd) {
		this.dmgrmgmtPasswd = dmgrmgmtPasswd;
	}

	public NodeConfig getDmgrNode() {
		return dmgrNode;
	}

	public void setDmgrNode(NodeConfig dmgrNode) {
		this.dmgrNode = dmgrNode;
	}

	@Override
	public String toString() {
		return "\nDMGRConfig [dmgrmgmtPort=" + dmgrmgmtPort + ", dmgrmgmtIP="
				+ dmgrmgmtIP + ", dmgrmgmtUsr=" + dmgrmgmtUsr
				+ ", dmgrmgmtPasswd=" + dmgrmgmtPasswd + ", dmgrNode="
				+ dmgrNode + "]";
	}
	
	
}
