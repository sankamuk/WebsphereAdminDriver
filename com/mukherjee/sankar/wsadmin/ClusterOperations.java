/**
 * Class to perform Cluster Administration.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.log4j.Logger;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigDataId;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;
import com.ibm.websphere.pmi.stat.WSStats;
import com.mukherjee.sankar.wsadmindata.ClusterState;
import com.mukherjee.sankar.wsadmindata.ClusterTarget;
import com.mukherjee.sankar.wsadmindata.DataSourceData;
import com.mukherjee.sankar.wsadmindata.DatasourcePool;
import com.mukherjee.sankar.wsadmindata.J2CConnection;
import com.mukherjee.sankar.wsadmindata.J2CPool;
import com.mukherjee.sankar.wsadmindata.JvmDataSource;
import com.mukherjee.sankar.wsadmindata.JvmJ2CCoonection;
import com.mukherjee.sankar.wsadmindata.JvmListenerPort;
import com.mukherjee.sankar.wsadmindata.JvmPMIStat;
import com.mukherjee.sankar.wsadmindata.JvmTarget;
import com.mukherjee.sankar.wsadmindata.ListenerPortConfig;

public class ClusterOperations {
	private static Logger logger = Logger.getLogger(ClusterOperations.class.getName());
	private AdminClient client = null ;
	private Session session = null ;
	private ConfigService configService = null;
	private String cellName = null;
	private JvmOperations jvmOperations =null;

	/**
	 * Constructor not be used to initialize manually as WsadminClient should be used to intialise this object.
	 * @param admc - AdminClient Object passed which should have a valid connection to Websphere Cell
	 * @param ses - Session Object of a already created Session for a Connection to Websphere Cell
	 * @param conf - ConfigService object which is already initialized to perform  Websphere Cell configuration operations
	 * @param cell - Cell Name for a Websphere Cell to which AdminClient already connected
	 */
	public ClusterOperations(AdminClient admc, Session ses, ConfigService conf, String cell, JvmOperations jvmV){
		logger.debug("Methord JvmOperations "+ admc.toString() +" Session "+ ses.toString());
		this.client = admc;
		this.session = ses;
		this.configService = conf;
		this.cellName = cell;
		this.jvmOperations = jvmV;
	}

	/**
	 * Get list of Dynamic Cluster.
	 * @return ArrayList of String for Dynamic Cluster Names
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public ArrayList<String> getClusterDynamic() throws ConfigServiceException, ConnectorException {
		logger.debug("Methord getClusterDynamic : Parameter.");
		ArrayList<String> clusterL = new ArrayList<String>();
		ObjectName[] clusters= configService.resolve(session, "DynamicCluster");
		for (ObjectName clusterObj : clusters)  {
			clusterL.add(clusterObj.getKeyProperty("_Websphere_Config_Data_Display_Name"));
		}
		logger.debug("Cluster List Status "+ clusterL);
		return clusterL;
	}

	/**
	 * Get list of Static Cluster.
	 * @return ArrayList of String for Static Cluster Names
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public ArrayList<String> getClusterStatic() throws ConfigServiceException, ConnectorException {
		logger.debug("Methord getClusterStatic : Parameter.");
		ArrayList<String> dynmicClusters = getClusterDynamic();
		logger.debug("Dynamic Cluster: "+ dynmicClusters);
		ArrayList<String> clusterL = new ArrayList<String>();
		ObjectName[] clusters= configService.resolve(session, "ServerCluster");
		for (ObjectName clusterObj : clusters)  {
			String clusterNm = clusterObj.getKeyProperty("_Websphere_Config_Data_Display_Name");
			if ( ! dynmicClusters.contains(clusterNm)){
				clusterL.add(clusterNm);
			}
		}
		logger.debug("Cluster List Status "+ clusterL.toString());
		return clusterL;
	}

	/**
	 * Provides JVM List for a Cluster, later to be moved under ClusterOperations.
	 * @param cluster - ClusterTarget representing Cluster 
	 * @return HashSet of JvmTarget object
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public HashSet<JvmTarget> getJVMforCluster(String clusterNm) throws ConfigServiceException, ConnectorException {
		logger.debug("Methord getJVMforCluster : Parameter Cluster Name: "+clusterNm+".");
		HashSet<JvmTarget> returnresult = new HashSet<JvmTarget>();
		ObjectName clusterObjNm = ConfigServiceHelper.createObjectName( null, "ServerCluster", clusterNm );
		ObjectName clusterObj = configService.queryConfigObjects( session, null, clusterObjNm, null )[0];
		ArrayList memList = (ArrayList) configService.getAttribute(session, clusterObj, "members");
		for (Object mem : memList) {
			AttributeList memObj=(AttributeList) mem;
			Attribute memAttr = (Attribute) memObj.get(5);
			ConfigDataId memConf = (ConfigDataId) memAttr.getValue();
			String memName = (String)configService.getAttribute(session, ConfigServiceHelper.createObjectName(memConf), "memberName");
			String memNode = (String)configService.getAttribute(session, ConfigServiceHelper.createObjectName(memConf), "nodeName");
			returnresult.add(new JvmTarget(memName, memNode));
		}
		return returnresult;
	}

	/**
	 * Return a ClusterState data structure having the state of Websphere Cluster state.
	 * @param clusterNm - String Name of the Cluster
	 * @param clusterType - String Type of the Cluster Dynamic for Dynamic Cluster and Static for Static Cluster
	 * @return ClusterState object containing the State of Cluster and Operation Mode(In case of Dynamic Cluster)
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws ConfigServiceException
	 */
	public ClusterState getClusterState(String clusterNm, String clusterType) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException, ConfigServiceException {
		logger.debug("Methord getClusterState : Parameter Cluster Name: "+clusterNm+" Type:"+ clusterType +".");
		ClusterState clusterData = new ClusterState(clusterNm, clusterType);
		String query = "WebSphere:type=Cluster,name=" + clusterNm + ",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (!s.isEmpty()){
			ObjectName clusterObj = (ObjectName)s.iterator().next();
			String status = (String) client.invoke(clusterObj, "getState", null, null);
			if (status.equals("websphere.cluster.stopped")) {
				clusterData.setClusterState("STOPPED");
			} else {
				clusterData.setClusterState("RUNNING");
			}
			if (clusterType.equalsIgnoreCase("Dynamic")){
				ObjectName clusterObjNm = ConfigServiceHelper.createObjectName( null, "DynamicCluster", clusterNm );
				ObjectName clusterObjStg = configService.queryConfigObjects( session, null, clusterObjNm, null )[0];
				String opMode = (String) configService.getAttribute(session, clusterObjStg, "operationalMode");
				clusterData.setOperationMode(opMode);
			}
		} else {
			logger.warn("Cluster "+ clusterNm +" is probably down.");
		}
		return clusterData;
	}

	/**
	 * Returns the Performance Statistics for a Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @return ArrayList of JvmPMIStat representing PMI statistics of individual JVM
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MalformedObjectNameException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public ArrayList<JvmPMIStat> getClusterPMI(String clusterNm) throws ConfigServiceException, ConnectorException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException {
		logger.debug("Methord getClusterPMI : Parameter Cluster Name: "+clusterNm+".");
		ArrayList<JvmPMIStat> clusterPMI= new ArrayList<JvmPMIStat>();
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			logger.info("Starting to Collect PMI Statistics for JVM "+ jvm.getJvmName() +" on Node "+ jvm.getNodeName());
			JvmPMIStat jvmStats = jvmOperations.getJVMPMI(jvm.getNodeName(), jvm.getJvmName());
			clusterPMI.add(jvmStats);
		}
		return clusterPMI;
	}

	/**
	 * Returns the detail information about the Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @return String containing detail Cluster State Information
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public String clusterInfo(String clusterNm) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord clusterInfo : Parameter Cluster Name: "+clusterNm+".");
		String returnresult = null;
		String query = "WebSphere:type=Cluster,name=" + clusterNm + ",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (!s.isEmpty()){
			ObjectName clusterObj = (ObjectName)s.iterator().next();
			returnresult = (String) client.invoke(clusterObj, "dumpClusterInfo", null, null);
		} else {
			logger.warn("Cluster Object cannot be found probably it might be down.");
		}
		return returnresult;
	}

	/**
	 * Sets Dynamic Cluster operation mode.
	 * @param clusterNm - String Name of the Cluster
	 * @param opMode - String Operation Mode, MANUAL for manual mode and rest for automatic mode
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public void changeOperationMode(String clusterNm, String opMode) throws ConfigServiceException, ConnectorException {
		logger.debug("Methord clusterInfo : Parameter Cluster Name: "+clusterNm+" Operation Mode: "+ opMode +".");
		AttributeList attributes = new AttributeList();
		if (opMode.equals("MANUAL")){
			attributes.add( new Attribute( "operationalMode", "manual" ) );
		} else {
			attributes.add( new Attribute( "operationalMode", "automatic" ) );
		}
		ObjectName clusterObjNm = ConfigServiceHelper.createObjectName( null, "DynamicCluster", clusterNm );
		ObjectName clusterObjStg = configService.queryConfigObjects( session, null, clusterObjNm, null )[0];
		configService.setAttributes( session, clusterObjStg, attributes );
		configService.save( session, true );
	}

	/**
	 * Initiates Heap Dump for all Active JVM in Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void heapdump(String clusterNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord heapdump : Parameter Cluster Name: "+clusterNm+".");
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			jvmOperations.heapdump(jvm.getNodeName(), jvm.getJvmName());
		}
	}

	/**
	 * Initiates JavaCore for all Active JVM in Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void javacoredump(String clusterNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord javacoredump : Parameter Cluster Name: "+clusterNm+".");
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			jvmOperations.javacoredump(jvm.getNodeName(), jvm.getJvmName());
		}
	}

	/**
	 * Initiates MustGather for all Active JVM in Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws InterruptedException 
	 */
	public void mustgather(String clusterNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException, InterruptedException {
		logger.debug("Methord mustgather : Parameter Cluster Name: "+clusterNm+".");
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			jvmOperations.javacoredump(jvm.getNodeName(), jvm.getJvmName());
			jvmOperations.heapdump(jvm.getNodeName(), jvm.getJvmName());
		}
		logger.info("First set of Dump Gather Completed.");
		Thread.sleep(120000);
		for (JvmTarget jvm : jvmList) {
			jvmOperations.javacoredump(jvm.getNodeName(), jvm.getJvmName());
			jvmOperations.heapdump(jvm.getNodeName(), jvm.getJvmName());
		}
		logger.info("Second set of Dump Gather Completed.");
		Thread.sleep(120000);
		for (JvmTarget jvm : jvmList) {
			jvmOperations.javacoredump(jvm.getNodeName(), jvm.getJvmName());
			jvmOperations.heapdump(jvm.getNodeName(), jvm.getJvmName());
		}
		logger.info("Completed all set of Dump Gather Completed.");
	}

	/**
	 * Stop a Cluster (In case the cluster is Dynamic, it will change the Operation Mode to Manual).
	 * @param clusterNm - String Name of the Cluster
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void stopCluster(String clusterNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord stopCluster : Parameter Cluster Name: "+clusterNm+".");
		ObjectName clusterObjNm = ConfigServiceHelper.createObjectName( null, "DynamicCluster", clusterNm );
		ObjectName[] clusterObjStg = configService.queryConfigObjects( session, null, clusterObjNm, null );
		if (clusterObjStg.length == 1){
			AttributeList attributes = new AttributeList();
			attributes.add( new Attribute( "operationalMode", "manual" ) );
			configService.setAttributes( session, clusterObjStg[0], attributes );
			configService.save( session, true );
		}
		String query = "WebSphere:type=Cluster,name=" + clusterNm + ",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (!s.isEmpty()){
			ObjectName clusterObj = (ObjectName)s.iterator().next();
			client.invoke(clusterObj, "stop", null, null);
		} else {
			logger.warn("Cluster Object cannot be found probably it does not exist.");
		}
	}

	/**
	 * Start a Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void startCluster(String clusterNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord startCluster : Parameter Cluster Name: "+clusterNm+".");
		String query = "WebSphere:type=Cluster,name=" + clusterNm + ",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (!s.isEmpty()){
			ObjectName clusterObj = (ObjectName)s.iterator().next();
			client.invoke(clusterObj, "start", null, null);
		} else {
			logger.warn("Cluster Object cannot be found probably it does not exist.");
		}
	}	

	/**
	 * Retries the Datasources configured in Cluster Scope and there Performance Statistics.
	 * @param clusterNm - String Name of the Cluster
	 * @return ArrayList of JvmDataSource containing list of Datasource configured per JVM
	 * @throws ConnectorException 
	 * @throws ConfigServiceException 
	 * @throws MalformedObjectNameException 
	 * @throws ReflectionException 
	 * @throws MBeanException 
	 * @throws InstanceNotFoundException 
	 */
	public ArrayList<JvmDataSource> getDatasourceData(String clusterNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord getDatasourceData : Parameter Cluster Name: "+clusterNm+".");
		ArrayList<JvmDataSource> dsData = new ArrayList<JvmDataSource>();
		ObjectName clusternm = ConfigServiceHelper.createObjectName( null, "ServerCluster", clusterNm );
		ObjectName clusterObj = configService.queryConfigObjects( session, null, clusternm, null )[0];
		ObjectName dsnm = ConfigServiceHelper.createObjectName( null, "DataSource", null );
		ObjectName[] dsList = configService.queryConfigObjects( session, clusterObj, dsnm, null );
		ObjectName poolNm = ConfigServiceHelper.createObjectName( null, "ConnectionPool", null);
		ArrayList<DatasourcePool> dsNameL=new ArrayList<DatasourcePool>();
		for (ObjectName ds : dsList) {
			logger.info(ds);
			String dsNm = ds.toString().split("_Websphere_Config_Data_Display_Name=")[1].split("\\,")[0];
			ObjectName poolId = configService.queryConfigObjects( session, ds, poolNm, null )[0];
			int mn=(Integer) configService.getAttribute(session, poolId, "minConnections");
			int mx = (Integer) configService.getAttribute(session, poolId, "maxConnections");
			dsNameL.add(new DatasourcePool(dsNm, mn, mx));
		}
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			JvmDataSource tempJvmDs = new JvmDataSource(jvm.getJvmName(), jvm.getNodeName());
			if (jvmOperations.isJVMStarted(jvm.getNodeName(), jvm.getJvmName())){
				for (DatasourcePool pl : dsNameL) {
					tempJvmDs.getDsDataList().add(new DataSourceData(jvm.getJvmName(),jvm.getNodeName(),pl.getDsName(),pl.getMaxSize(),pl.getMinSize()));
				}
			}
			dsData.add(tempJvmDs);
		}
		return dsData;
	}

	/**
	 * Test Connection for a particular Datasource in all JVM in Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @param dsName - String JNDI Name of the Datasource
	 * @return String containing the Test Connection Status per JVM
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public String testConnection(String clusterNm, String dsName) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord testConnection : Parameter Cluster Name: "+clusterNm+" Datasource Name "+ dsName +".");
		String returnresult="";
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			returnresult = returnresult +"\n"+ jvmOperations.testDatasourceConnection(jvm.getNodeName(), jvm.getJvmName(), dsName); 
		}
		return returnresult;
	}

	/**
	 * Purges a Datasource Pool of a particular Datasource for all JVM in a Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @param dsName - String JNDI Name of the Datasource
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void purgeDsPoolCluster(String clusterNm, String dsName) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord purgeDsPoolCluster : Parameter Cluster Name: "+clusterNm+" Datasource Name "+ dsName +".");
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			jvmOperations.purgeDatasourcePool(jvm.getNodeName(), jvm.getJvmName(), dsName); 
		}
	}

	/**
	 * Extracts a list of Listener Ports configured for all JVM in Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @return ArrayList of JvmListenerPort containing list of Listener Ports configured per JVM
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public ArrayList<JvmListenerPort> getListenerPortCluster(String clusterNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord getListenerPortCluster : Parameter Cluster Name: "+clusterNm+".");
		ArrayList<JvmListenerPort> lpList = new ArrayList<JvmListenerPort>();
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			JvmListenerPort tempLpList=new JvmListenerPort(jvm.getJvmName(), jvm.getNodeName());
			for (ListenerPortConfig lpData : jvmOperations.getListenerPort(jvm.getNodeName(), jvm.getJvmName())) {
				tempLpList.getLpList().add(lpData);
			}
			lpList.add(tempLpList);
		}
		return lpList;
	}

	/**
	 * Starts a Listener Port for all JVM in a Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @param lpName - String Name of the Listener Port
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void startListenerPortCluster(String clusterNm, String lpName) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord startListenerPortCluster : Parameter Cluster Name: "+clusterNm+" Listener Port "+ lpName +".");
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			logger.info("Starting Listener Port "+ lpName +" on Node "+ jvm.getNodeName() +" JVM "+ jvm.getJvmName());
			jvmOperations.listenerPortAdmin(jvm.getNodeName(), jvm.getJvmName(), lpName, "start");
		}
	}

	/**
	 * Stops a Listener Port deployed in all JVM in Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @param lpName - String Name of the Listener Port
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void stoptListenerPortCluster(String clusterNm, String lpName) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord stoptListenerPortCluster : Parameter Cluster Name: "+clusterNm+" Listener Port "+ lpName +".");
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			logger.info("Stopping Listener Port "+ lpName +" on Node "+ jvm.getNodeName() +" JVM "+ jvm.getJvmName());
			jvmOperations.listenerPortAdmin(jvm.getNodeName(), jvm.getJvmName(), lpName, "stop");
		}
	}

	/**
	 * Extracts a List of J2C Connection Factory and there Pool status for all JVM in Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @return ArrayList of JvmJ2CCoonection containing list of J2C Connection Factory configured per JVM
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws ConnectorException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws ConfigServiceException
	 */
	public ArrayList<JvmJ2CCoonection> getJ2CConnectionPoolCluster(String clusterNm) throws MalformedObjectNameException, InstanceNotFoundException, ConnectorException, MBeanException, ReflectionException, ConfigServiceException {
        logger.debug("Methord getJ2CConnectionPoolCluster : Parameter Cluster Name: "+clusterNm+".");
        ArrayList<JvmJ2CCoonection> poolList= new ArrayList<JvmJ2CCoonection>();
        ArrayList<J2CPool> poolData = new ArrayList<J2CPool>();
        String query = "ServerCluster="+ clusterNm;
		ObjectName clusterId = configService.resolve(session, query)[0];
		ObjectName cfName = ConfigServiceHelper.createObjectName( null, "J2CConnectionFactory", null );
		ObjectName[] j2cList = configService.queryConfigObjects( session, clusterId, cfName, null );
		cfName = ConfigServiceHelper.createObjectName( null, "MQQueueConnectionFactory", null );
		ObjectName[] mqList = configService.queryConfigObjects( session, clusterId, cfName, null );
		for (ObjectName j2C : j2cList) {
			String j2cNm=j2C.toString().split("_Websphere_Config_Data_Display_Name=")[1].split("\\,")[0];
			ObjectName poolNm = ConfigServiceHelper.createObjectName( null, "ConnectionPool", null);
			ObjectName[] poolIdL = configService.queryConfigObjects( session, j2C, poolNm, null );
			if (poolIdL != null && poolIdL.length > 0){
				ObjectName poolId=poolIdL[0];
				Integer mxSz = (Integer) configService.getAttribute(session, poolId, "maxConnections");
				Integer mnSz = (Integer) configService.getAttribute(session, poolId, "minConnections");
				logger.debug("Connection Factory Name: "+ j2cNm +", Min Pool Size:"+ mnSz +", Max Pool Size:"+ mxSz);
				poolData.add(new J2CPool(j2cNm, mnSz, mxSz));
			}
		}
		for (ObjectName j2C : mqList) {
			String j2cNm=j2C.toString().split("_Websphere_Config_Data_Display_Name=")[1].split("\\,")[0];
			ObjectName poolNm = ConfigServiceHelper.createObjectName( null, "ConnectionPool", null);
			ObjectName[] poolIdL = configService.queryConfigObjects( session, j2C, poolNm, null );
			if (poolIdL != null && poolIdL.length > 0){
				ObjectName poolId=poolIdL[0];
				Integer mxSz = (Integer) configService.getAttribute(session, poolId, "maxConnections");
				Integer mnSz = (Integer) configService.getAttribute(session, poolId, "minConnections");
				logger.debug("Connection Factory Name: "+ j2cNm +", Min Pool Size:"+ mnSz +", Max Pool Size:"+ mxSz);
				poolData.add(new J2CPool(j2cNm, mnSz, mxSz));
			}
		}
        HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
        for (JvmTarget jvm : jvmList) {
                JvmJ2CCoonection tempJvmJ2c = new JvmJ2CCoonection(jvm.getJvmName(), jvm.getNodeName());
                if(jvmOperations.isJVMStarted(jvm.getNodeName(), jvm.getJvmName())){
                	for (J2CPool pl : poolData) {
						tempJvmJ2c.getJ2cList().add(new J2CConnection(jvm.getJvmName(), jvm.getNodeName(), pl.getJ2cNm(), null, pl.getMaxSize(), pl.getMinSize()));
					}
                }
                poolList.add(tempJvmJ2c);
        }
        return poolList;
	}

	/**
	 * Purges all J2C Connection Pool for a particular factory for all JVM's in Cluster.
	 * @param clusterNm - String Name of the Cluster
	 * @param j2c - String Name of the Connection Factory Name
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void purgeAllJ2CConnectionPoolCluster(String clusterNm, String j2c) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord startListenerPortCluster : Parameter Cluster Name: "+clusterNm+" J2CCoonection Factory "+ j2c +".");
		ArrayList<JvmJ2CCoonection> poolList= new ArrayList<JvmJ2CCoonection>();
		HashSet<JvmTarget> jvmList=getJVMforCluster(clusterNm);
		for (JvmTarget jvm : jvmList) {
			logger.debug("For JVM "+ jvm.getJvmName() +" on Node "+ jvm.getNodeName() +" purging the J2C Connection Pool for "+ j2c);
			jvmOperations.purgeJ2CPool(jvm.getNodeName(), jvm.getJvmName(), j2c);

		}
	}
}
