/*
 * Usage: This is a datastructure for Threadpool.
 * Author: Sankar Mukherjee
 * Date: 12 March, 2015
 */
package com.mukherjee.sankar.wsadmindata;

public class ThreadPoolStat {

	String poolName;
	long poolSize;
	long activeSize;
	
	public ThreadPoolStat(String pl, long sz, long at) {
		this.poolName=pl;
		this.poolSize=sz;
		this.activeSize=at;
	}

	/**
	 * Get Name of the Thread Pool Considered.
	 * @return String Name
	 */
	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	/**
	 * Thread Pool Size(Current).
	 * @return Long Number
	 */
	public long getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(long poolSize) {
		this.poolSize = poolSize;
	}

	/**
	 * Get Size of the Pool currently actively used.
	 * @return Long Number
	 */
	public long getActiveSize() {
		return activeSize;
	}

	public void setActiveSize(long activeSize) {
		this.activeSize = activeSize;
	}

	@Override
	public String toString() {
		return "ThreadPoolStat [Pool Name=" + poolName + ", Pool Size="
				+ poolSize + ", Active Size=" + activeSize + "]";
	}
	
	
}
