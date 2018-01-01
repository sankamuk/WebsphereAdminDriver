/**
* This is a Data Structure representing a Cell.
* @author Sankar Mukherjee
* @version 01, 12 March, 2015
*/

package com.mukherjee.sankar.wsadmin.discovery;

import java.util.ArrayList;

public class CellConfig {
	String cellName;
	ArrayList<DMGRConfig> dmgrList;
	ArrayList<ApplicationConfig> appList;
	ArrayList<NodeConfig> nodeList;
	ArrayList<HostConfig> hostList;

	public CellConfig(String nm) {
		this.cellName=nm;
		this.dmgrList=new ArrayList<DMGRConfig>();
		this.appList=new ArrayList<ApplicationConfig>();
		this.nodeList=new ArrayList<NodeConfig>();
		this.hostList=new ArrayList<HostConfig>();
	}

	public String getCellName() {
		return cellName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	public ArrayList<DMGRConfig> getDmgrList() {
		return dmgrList;
	}

	public void setDmgrList(ArrayList<DMGRConfig> dmgrList) {
		this.dmgrList = dmgrList;
	}

	public ArrayList<ApplicationConfig> getAppList() {
		return appList;
	}

	public void setAppList(ArrayList<ApplicationConfig> appList) {
		this.appList = appList;
	}

	public ArrayList<NodeConfig> getNodeList() {
		return nodeList;
	}

	public void setNodeList(ArrayList<NodeConfig> nodeList) {
		this.nodeList = nodeList;
	}

	public ArrayList<HostConfig> getHostList() {
		return hostList;
	}

	public void setHostList(ArrayList<HostConfig> hostList) {
		this.hostList = hostList;
	}

	@Override
	public String toString() {
		return "CellConfig [cellName=" + cellName + ", \n\n *** dmgrList=" + dmgrList
				+ ", \n\n *** appList=" + appList + ", \n\n *** nodeList=" + nodeList
				+ ", \n\n *** hostList=" + hostList + "]";
	}

	

}
