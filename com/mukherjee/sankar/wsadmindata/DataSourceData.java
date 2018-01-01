/*
 * Usage: This is a datastructure for Datasource.
 * Author: Sankar Mukherjee
 * Date: 12 March, 2015
 */
package com.mukherjee.sankar.wsadmindata;



public class DataSourceData {
	private String jvmName;
	private String nodeName;
	private String dsName;
	private long poolSize;
	private long freePool;
	
	public DataSourceData(String jvm, String node, String ds, long pl, long fr) {
		this.jvmName=jvm;
		this.nodeName=node;
		this.dsName=ds;
		this.poolSize=pl;
		this.freePool=fr;
	}

	/**
	 * Get the Datasource Name.
	 * @return String Name
	 */
	public String getDsName() {
		return dsName;
	}

	/**
	 * Get the Size of the Connection Pool.
	 * @return Long Number
	 */
	public long getPoolSize() {
		return poolSize;
	}

	/**
	 * Get the Number of Free Connection in the Connection Pool.
	 * @return Long Number
	 */
	public long getFreePool() {
		return freePool;
	}

	/**
	 * Get Name of the JVM.
	 * @return String Name
	 */
	public String getJvmName() {
		return jvmName;
	}

	/**
	 * Get Name of the Node.
	 * @return String Name
	 */
	public String getNodeName() {
		return nodeName;
	}

	@Override
	public String toString() {
		return "DataSourceData [jvmName=" + jvmName + ", nodeName=" + nodeName
				+ ", dsName=" + dsName + ", poolSize=" + poolSize
				+ ", freePool=" + freePool + "]";
	}


	
}
