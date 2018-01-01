/*
 * Usage: This is a datastructure for JVM PMI data.
 * Author: Sankar Mukherjee
 * Date: 12 March, 2015
 */
package com.mukherjee.sankar.wsadmindata;

import java.util.ArrayList;

public class JvmPMIStat {

	String jvmName;
	String nodeName;
	boolean isUp;
	long heapUtilization;
	long cpuUtilization;
	ArrayList<ThreadPoolStat> threadPools;
	ArrayList<SessionStat> sessionStats;
	
	public JvmPMIStat(String jvm, String nd, boolean st) {
		this.jvmName=jvm;
		this.nodeName=nd;
		this.isUp=st;
		heapUtilization = 0;
		cpuUtilization = 0;
		this.threadPools = new ArrayList<ThreadPoolStat>();
		this.sessionStats = new ArrayList<SessionStat>();
	}

	/**
	 * Gets a JVM Name.
	 * @return String Name
	 */
	public String getJvmName() {
		return jvmName;
	}

	public void setJvmName(String jvmName) {
		this.jvmName = jvmName;
	}

	/**
	 * Gets a Node Name.
	 * @return String Name
	 */
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * Status of the JVM, true for Running JVM false otherwise.
	 * @return Boolean Status
	 */
	public boolean isUp() {
		return isUp;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}

	/**
	 * Heap Utilization Percentage.
	 * @return Long Number
	 */
	public long getHeapUtilization() {
		return heapUtilization;
	}

	public void setHeapUtilization(long heapUtilization) {
		this.heapUtilization = heapUtilization;
	}

	/**
	 * Percentage CPU Utilization.
	 * @return Long Number
	 */
	public long getCpuUtilization() {
		return cpuUtilization;
	}

	public void setCpuUtilization(long cpuUtilization) {
		this.cpuUtilization = cpuUtilization;
	}

	/**
	 * List of Thread Pool Statistics.
	 * @return List of ThreadPoolStat
	 */
	public ArrayList<ThreadPoolStat> getThreadPools() {
		return threadPools;
	}

	public void setThreadPools(ArrayList<ThreadPoolStat> threadPools) {
		this.threadPools = threadPools;
	}

	/**
	 * List of Web Module and Session statistics. 
	 * @return List of SessionStat
	 */
	public ArrayList<SessionStat> getSessionStats() {
		return sessionStats;
	}

	public void setSessionStats(ArrayList<SessionStat> sessionStats) {
		this.sessionStats = sessionStats;
	}

	@Override
	public String toString() {
		return "\n JvmPMIStat [\n Jvm Name=" + jvmName + ", Node Name=" + nodeName
				+ ", isUp=" + isUp + ", Heap Utilization=" + heapUtilization
				+ ", Cpu Utilization=" + cpuUtilization + ", \n\n Thread Pools="
				+ threadPools + ", \n\n Session Statistics=" + sessionStats + "]";
	}
	
	

}
