/**
 * This is a J2C Pool Configuration.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */
package com.mukherjee.sankar.wsadmindata;

public class J2CPool {

	String j2cNm;
	int minSize;
	int maxSize;
	
	public J2CPool(String n, int mn, int mx) {
		j2cNm=n;
		minSize=mn;
		maxSize=mx;
	}
	
	public String getJ2cNm() {
		return j2cNm;
	}
	public void setJ2cNm(String j2cNm) {
		this.j2cNm = j2cNm;
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
		return "J2CPool [j2cNm=" + j2cNm + ", minSize=" + minSize
				+ ", maxSize=" + maxSize + "]";
	}
	
	
}
