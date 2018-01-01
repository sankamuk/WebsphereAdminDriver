/**
 * Class to creates a wasadmin client to be used for Websphere Application Server Administration.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmin;

import java.util.Properties;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class WsadminClient {
	private static Logger logger = Logger.getLogger(WsadminClient.class.getName());
	private AdminClient client = null;
	private Session session = null;
	private ConfigService configService = null;
	private AppManagement appManager = null;
	private JvmOperations jvmV = null;
	private ApplicationOperations appV = null;
	private ClusterOperations clusterV = null;
	private String cellName = null;

	/**
	 * Method used to initialize a client for the management interface of the Websphere Cell to be administered.  
	 * @param host - Host name of DMGR
	 * @param port - Soap Connector port of DMGR
	 * @param user - User Id for the DMGR
	 * @param password - Password for the above mentioned user
	 * @return In case connection was successful method will return true
	 * @throws Exception 
	 */
	public boolean initialize(String host, String port, String user, String password) throws Exception {
		logger.debug("Methord getAdminConnection, Host "+ host +" Port "+ port +" User "+ user +" Password "+ password);
		Properties connectProps = new Properties();
		connectProps.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		connectProps.setProperty(AdminClient.CONNECTOR_HOST, host);
		connectProps.setProperty(AdminClient.CONNECTOR_PORT, port);
		connectProps.setProperty(AdminClient.USERNAME, user);
		connectProps.setProperty(AdminClient.PASSWORD, password);
		client = AdminClientFactory.createAdminClient(connectProps);
		session = new Session();
		configService = new ConfigServiceProxy(client);
		appManager = AppManagementProxy.getJMXProxyForClient(client);
		logger.info("Successfully created connection to Host "+ host + " port "+ port +" user "+ user);

		ObjectName cellObject= configService.resolve(session, "Cell")[0];
		cellName = (String) configService.getAttribute(session, cellObject, "name");
		logger.info("Cell retrived "+ cellName);

		jvmV = new JvmOperations(client, session, configService, cellName);
		logger.info("Initiated JvmOperations object successfully.");

		clusterV = new ClusterOperations(client, session, configService, cellName, jvmV);
		logger.info("Initiated ClusterOperations object successfully.");

		appV = new ApplicationOperations(client, session, configService, appManager, cellName, clusterV);
		logger.info("Initiated ApplicationOperations object successfully.");

		return true;

	}

	/** Getter for property cellName
	 * @return 
	 */
	public String getCellName() {
		return cellName;
	}


	/**
	 * Getter for configService property
	 * @return
	 */
	public ConfigService getConfigService() {
		return configService;
	}
	
	
	/**
	 * Getter for session property
	 * @return
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Method to be used to return the Cluster Operation Object. This object to be used for all operation in Cluster Scope.
	 * @return
	 */
	public ClusterOperations getClusterV() {
		return clusterV;
	}

	/**
	 * Method to be used to return the Application Operation Object. This object to be used for all operation in Application Scope.
	 * @return ApplicationOperations object
	 */
	public ApplicationOperations getAppV() {
		return appV;
	}

	/**
	 * Method to be used to return the JVM Operation Object. This object to be used for all operation in JVM Scope.
	 * @return JvmOperations object
	 */
	public JvmOperations getJvmV() {
		return jvmV;
	}

}
