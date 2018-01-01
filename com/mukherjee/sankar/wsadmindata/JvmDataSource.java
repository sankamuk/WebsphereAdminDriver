/**
 * Datastructure representing a particular JVM and statistics for all Datasource resource configured for it.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

import java.util.ArrayList;

public class JvmDataSource {

	private String jvmName;
	private String nodeName;
	private ArrayList<DataSourceData> dsDataList;
	
	public JvmDataSource(String jvm, String nd) {
		this.jvmName=jvm;
		this.nodeName=nd;
		dsDataList =  new ArrayList<DataSourceData>();
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
	 * List of Datasource and there Statistics for a Particular JVM
	 * @return List DataSourceData
	 */
	public ArrayList<DataSourceData> getDsDataList() {
		return dsDataList;
	}

	public void setDsDataList(ArrayList<DataSourceData> dsDataList) {
		this.dsDataList = dsDataList;
	}

	@Override
	public String toString() {
		return "JvmDataSource [jvmName=" + jvmName + ", nodeName=" + nodeName
				+ ", dsDataList=" + dsDataList + "]";
	}
	
	
}
