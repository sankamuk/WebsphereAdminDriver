/**
 * This is a datastructure to represent a Property Tuple(Name,Value pair).
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

public class PropertySet {

	String propName;
	String propValue;
	
	public PropertySet(String nm, String val) {
		this.propName=nm;
		this.propValue=val;
	}

	/**
	 * Get the Name of the property.
	 * @return String Name
	 */
	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	/**
	 * Get property value.
	 * @return String value
	 */
	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}

	@Override
	public String toString() {
		return propName +" = "+ propValue;
	}
	
	
}
