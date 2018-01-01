/**
 * Datastructure to represent a particular Java EE Application Modules Performance statistics.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

import java.util.ArrayList;

public class ModulePerformance {

	String moduleName;
	String jvmName;
	String nodeName;
	long totHits;
	long avgResponse;
	long maxResponse;
	long minResponce;
	ArrayList<URIPerformance> uriPMIList;
	
	public ModulePerformance(String mod, String jvm, String node, long ht, long avg, long mn, long mx) {
		this.moduleName=mod;
		this.jvmName=jvm;
		this.nodeName=node;
		this.totHits=ht;
		this.avgResponse=avg;
		this.maxResponse=mx;
		this.minResponce=mn;
		uriPMIList = new ArrayList<URIPerformance>();
	}

	/**
	 * Gets the Java EE Module Name.
	 * @return String module name
	 */
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * Total number of Hits for the Module.
	 * @return Long Number
	 */
	public long getTotHits() {
		return totHits;
	}

	public void setTotHits(long totHits) {
		this.totHits = totHits;
	}

	/**
	 * Gets average response time of all the URL for the Web Module.
	 * @return Long Number
	 */
	public long getAvgResponse() {
		return avgResponse;
	}

	public void setAvgResponse(long avgResponse) {
		this.avgResponse = avgResponse;
	}

	/**
	 * Gets maximum response time of all the URL for the Web Module.
	 * @return Long Number
	 */
	public long getMaxResponse() {
		return maxResponse;
	}

	public void setMaxResponse(long maxResponse) {
		this.maxResponse = maxResponse;
	}

	/**
	 * Gets minimum response time of all the URL for the Web Module.
	 * @return Long Number
	 */
	public long getMinResponce() {
		return minResponce;
	}

	public void setMinResponce(long minResponce) {
		this.minResponce = minResponce;
	}

	/**
	 * Get list of Performance Statistics for a particular URI in the Web Module.
	 * @return List of URIPerformance
	 */
	public ArrayList<URIPerformance> getUriPMIList() {
		return uriPMIList;
	}

	public void setUriPMIList(ArrayList<URIPerformance> uriPMIList) {
		this.uriPMIList = uriPMIList;
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

	@Override
	public String toString() {
		return "ModulePerformance [moduleName=" + moduleName + ", jvmName="
				+ jvmName + ", nodeName=" + nodeName + ", totHits=" + totHits
				+ ", avgResponse=" + avgResponse + ", maxResponse="
				+ maxResponse + ", minResponce=" + minResponce
				+ ", uriPMIList=" + uriPMIList + "]";
	}


}
