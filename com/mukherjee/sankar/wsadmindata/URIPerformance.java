/**
 * Datastructure to represent a particular Java EE Application individual Performance statistics.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

public class URIPerformance {

	String uriName;
	long hits;
	long maxResponce;
	long minResponce;
	long avgResponce;
	
	public URIPerformance(String u, long h, long a, long mn, long mx) {
		this.uriName=u;
		this.hits=h;
		this.minResponce=mn;
		this.maxResponce=mx;
		this.avgResponce=a;
	}

	/**
	 * URI Name for the Web Module. 
	 * @return String Name
	 */
	public String getUriName() {
		return uriName;
	}

	public void setUriName(String uriName) {
		this.uriName = uriName;
	}

	/**
	 * Total Number Hits to the URI.
	 * @return Long Number
	 */
	public long getHits() {
		return hits;
	}

	public void setHits(long hits) {
		this.hits = hits;
	}

	/**
	 * Get Maximum response time recorded for the URI.
	 * @return Long Number
	 */
	public long getMaxResponce() {
		return maxResponce;
	}

	public void setMaxResponce(long maxResponce) {
		this.maxResponce = maxResponce;
	}

	/**
	 * Get minimum time response time recorded for the URI. 
	 * @return Long Number
	 */
	public long getMinResponce() {
		return minResponce;
	}

	public void setMinResponce(long minResponce) {
		this.minResponce = minResponce;
	}

	/**
	 * Average response time for the URI.
	 * @return Long Number
	 */
	public long getAvgResponce() {
		return avgResponce;
	}

	public void setAvgResponce(long avgResponce) {
		this.avgResponce = avgResponce;
	}

	@Override
	public String toString() {
		return "URIPerformance [uriName=" + uriName + ", hits=" + hits
				+ ", maxResponce=" + maxResponce + ", minResponce="
				+ minResponce + ", avgResponce=" + avgResponce + "]";
	}
	
}
