/**
 * This is a datastructure for Datasource Pool Configuration.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */
package com.mukherjee.sankar.wsadmindata;

public class DatasourcePool {

	String dsName;
	int minSize;
	int maxSize;
	
	public DatasourcePool(String n, int mn, int mx) {
		dsName = n;
		minSize = mn;
		maxSize = mx;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public String toString() {
		return "DatasourcePool [dsName=" + dsName + ", minSize=" + minSize
				+ ", maxSize=" + maxSize + "]";
	}
	
}
