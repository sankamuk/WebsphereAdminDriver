/**
 * This is a datastructure for Session Statistics.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

public class SessionStat {

	String moduleName;
	long sesnCount;
	
	public SessionStat(String mod, long cn) {
		this.moduleName=mod;
		this.sesnCount=cn;
	}

	/**
	 * Get the Name of the Web Module Name for which Session count is gathered.
	 * @return String Name.
	 */
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * Get the current number of active sessions for the Web Module.
	 * @return Long Number
	 */
	public long getSesnCount() {
		return sesnCount;
	}

	public void setSesnCount(long sesnCount) {
		this.sesnCount = sesnCount;
	}

	@Override
	public String toString() {
		return "SessionStat [Module Name=" + moduleName + ", Session Count="
				+ sesnCount + "]";
	}
	
	
}
