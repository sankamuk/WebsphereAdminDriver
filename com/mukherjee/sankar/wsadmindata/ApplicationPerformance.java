/**
 * Datastructure to represent a particular Java EE Application Performance statistics.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

import java.util.ArrayList;

public class ApplicationPerformance {

	ArrayList<ModulePerformance> modulePMI;
	
	public ApplicationPerformance() {
		modulePMI = new ArrayList<ModulePerformance>();
	}

	/**
	 * Gets a List of Java EE Applications Module PMI Data.
	 * @return ArrayList of ModulePerformance
	 */
	public ArrayList<ModulePerformance> getModulePMI() {
		return modulePMI;
	}

	public void setModulePMI(ArrayList<ModulePerformance> modulePMI) {
		this.modulePMI = modulePMI;
	}

	@Override
	public String toString() {
		return "ApplicationPerformance [modulePMI=" + modulePMI + "]";
	}

}
