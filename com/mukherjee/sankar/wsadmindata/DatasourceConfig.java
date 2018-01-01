/**
 * This is a datastructure for Datasource Configuration.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmindata;

public class DatasourceConfig {
	String datasourceName;
	String datasourecConfigId;
	String datasourceJndi;
	
	public DatasourceConfig(String dsnm, String dscon, String jndi) {
		this.datasourceName=dsnm;
		this.datasourecConfigId=dscon;
		this.datasourceJndi=jndi;
	}

	/**
	 * Get Name of the Datasource.
	 * @return String Name
	 */
	public String getDatasourceName() {
		return datasourceName;
	}

	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}

	/**
	 * Get Datasource Configuration Id.
	 * @return String 
	 */
	public String getDatasourecConfigId() {
		return datasourecConfigId;
	}

	public void setDatasourecConfigId(String datasourecConfigId) {
		this.datasourecConfigId = datasourecConfigId;
	}

	/**
	 * Get Datasource JNDI Name.
	 * @return String Name
	 */
	public String getDatasourceJndi() {
		return datasourceJndi;
	}

	public void setDatasourceJndi(String datasourceJndi) {
		this.datasourceJndi = datasourceJndi;
	}

	@Override
	public String toString() {
		return "DatasourceConfig [datasourceName=" + datasourceName
				+ ", datasourecConfigId=" + datasourecConfigId
				+ ", datasourceJndi=" + datasourceJndi + "]";
	}

	
}
