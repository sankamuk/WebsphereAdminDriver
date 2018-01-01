/**
 * Class to perform JVM Administration.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.python.modules.struct;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;
import com.ibm.websphere.pmi.stat.WSCountStatistic;
import com.ibm.websphere.pmi.stat.WSRangeStatistic;
import com.ibm.websphere.pmi.stat.WSStats;
import com.mukherjee.sankar.wsadmindata.DataSourceData;
import com.mukherjee.sankar.wsadmindata.DatasourceConfig;
import com.mukherjee.sankar.wsadmindata.J2CConnection;
import com.mukherjee.sankar.wsadmindata.JvmPMIStat;
import com.mukherjee.sankar.wsadmindata.ListenerPortConfig;
import com.mukherjee.sankar.wsadmindata.SessionStat;
import com.mukherjee.sankar.wsadmindata.ThreadPoolStat;

public class JvmOperations {
	private static Logger logger = Logger.getLogger(JvmOperations.class.getName());
	private AdminClient client = null ;
	private Session session = null ;
	private ConfigService configService = null;
	private String cellName = null;

	/**
	 * Constructor not be used to intialise manually as WsadminClient should be used to intialise this object.
	 * @param admc - <AdminClient> Object passed which should have a valid connection to Websphere Cell
	 * @param ses - <Session> Object of a already created Session for a Connection to Websphere Cell
	 * @param conf - <ConfigService> object which is already intialised to perform  Websphere Cell configuration operations
	 * @param cell - <String> Cell Name for a Websphere Cell to which AdminClient already connected
	 */
	public JvmOperations(AdminClient admc, Session ses, ConfigService conf, String cell){
		logger.debug("Methord JvmOperations "+ admc.toString() +" Session "+ ses.toString());
		this.client = admc;
		this.session = ses;
		this.configService = conf;
		this.cellName = cell;
	}

	/**
	 * Return a list of Nodes in the Websphere Cell.
	 * @return ArrayList of String of Node name
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 */
	public ArrayList<String> returnNodes() throws ConfigServiceException, ConnectorException, MalformedObjectNameException {
		logger.debug("Methord returnNodes");
		ArrayList<String> returnresult = new ArrayList<String>();
		ObjectName[] nodes= configService.resolve(session, "Node");
		for (ObjectName nodeObj : nodes)  {
			returnresult.add(nodeObj.getKeyProperty("_Websphere_Config_Data_Display_Name"));
		}
		logger.debug("Node List Status "+ returnresult.toString());
		return returnresult;
	}

	/**
	 * Return a list of JVM for a perticular Node in the Websphere Cell.
	 * @param node - Node Name <String>
	 * @return ArrayList of String of JVM name
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public ArrayList<String> returnJvms(String node) throws ConfigServiceException, ConnectorException {
		logger.debug("Methord returnNodes : Parameter Node "+ node);
		ArrayList<String> returnresult = new ArrayList<String>();
		ObjectName nodenm = ConfigServiceHelper.createObjectName( null, "Node", node );
		ObjectName nodeObj = configService.queryConfigObjects( session, null, nodenm, null )[0];
		ObjectName servernm = ConfigServiceHelper.createObjectName( null, "Server", null );
		ObjectName[] servers = configService.queryConfigObjects( session, nodeObj, servernm, null );
		for (ObjectName srvObj : servers) {
			returnresult.add(srvObj.getKeyProperty("_Websphere_Config_Data_Display_Name"));
		}
		logger.debug("Jvm List Status "+ returnresult.toString());
		return returnresult;
	}

	/**
	 * Checks the Cluster for a JVM
	 * @param jvm - JVM Name <String>
	 * @param node - Node Name <String>
	 * @return String representing Cluster Name or NULL in case the JVM not part of Cluster
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public String returnClusterforJVM(String node, String jvm) throws ConfigServiceException, ConnectorException {
		logger.debug("Methord returnClusterforJVM : Parameter JVM "+ jvm +"Node "+ node);
		ObjectName nodenm = ConfigServiceHelper.createObjectName( null, "Node", node );
		ObjectName nodeObj = configService.queryConfigObjects( session, null, nodenm, null )[0];
		ObjectName servernm = ConfigServiceHelper.createObjectName( null, "Server", jvm );
		ObjectName srvObj = configService.queryConfigObjects( session, nodeObj, servernm, null )[0];
		if (srvObj != null){
			Object clusterNm=configService.getAttribute(session, srvObj, "clusterName");
			return (String) clusterNm;
		} else {
			logger.warn("The JVM "+ jvm +" on Node "+ node +" not part of Cluster.");
			return null;
		}
	}

	/**
	 * Starts up a JVM Process.
	 * @param nodeNm - Node Name <String>
	 * @param jvmNm - JVM Name <String>
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws ReflectionException
	 * @throws MBeanException
	 */
	public void startJVM(String nodeNm, String jvmNm) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, ReflectionException, MBeanException {
		logger.debug("Methord startJVM : Parameter Node "+ nodeNm +" Server "+ jvmNm +" will be started.");
		String query = "WebSphere:type=NodeAgent,node=" + nodeNm + ",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (s.isEmpty()) {
			logger.error("Node agent not detected for the Node.");
		} else {
			ObjectName nodeAgent = (ObjectName)s.iterator().next();
			String opName = "launchProcess";
			String signature[] = { "java.lang.String" };
			String params[] = { jvmNm };
			Boolean b = (Boolean)client.invoke(nodeAgent, opName, params, signature);
			if (b){
				logger.info("JVM "+ jvmNm +" on Node "+ nodeNm +" was started.");
			} else {
				logger.warn("JVM "+ jvmNm +" on Node "+ nodeNm +" was not started.");
			}
		}

	}

	/**
	 * Stops a JVM Process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void stopJVM(String nodeNm, String jvmNm) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord stopJVM : Parameter Node "+ nodeNm +" Server "+ jvmNm +" will be stopped.");
		String query = "WebSphere:type=Server,node=" + nodeNm + ",name="+ jvmNm +",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (!s.isEmpty()){
			ObjectName srvObj = (ObjectName)s.iterator().next();
			String params[] = null;
			String signature[] = null;
			client.invoke(srvObj, "stop",  params, signature) ;
			logger.info("JVM "+ jvmNm +" on Node "+ nodeNm +" was stopped.");
		}
	}

	/**
	 * Check if a JVM Process is running, returns a <boolean> true in case the JVM is running.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @return True in case JVM running, False otherwise 
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 */
	public boolean isJVMStarted(String nodeNm, String jvmNm) throws MalformedObjectNameException, ConnectorException {
		logger.debug("Methord isJVMStarted Node "+ nodeNm +" Server "+ jvmNm +" will be checked for running status.");
		String query = "WebSphere:type=Server,node=" + nodeNm + ",name="+ jvmNm +",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (s.isEmpty()){
			logger.info("Cannot find JVM Object, it seems to be stopped.");
			return false;
		} else {
			logger.info("JVM is running.");
			return true;
		}
	}

	/**
	 * Initiates a Heap Dump in a JVM process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void heapdump(String nodeNm, String jvmNm) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord heapdump : Parameter Node "+ nodeNm +" Server "+ jvmNm +" will be taken.");
		String query = "WebSphere:type=JVM,node=" + nodeNm + ",process="+ jvmNm +",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (s.isEmpty()){
			logger.info("Cannot find JVM Object, it seems to be stopped.");
		} else {
			ObjectName srvObj = (ObjectName)s.iterator().next();
			String params[] = null;
			String signature[] = null;
			client.invoke(srvObj, "generateHeapDump",  params, signature) ;
			logger.info("Taken Heapdump on JVM "+ jvmNm +" on Node "+ nodeNm +".");
		}
	}

	/**
	 * Initiates a JavaCore Dump in a JVM process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void javacoredump(String nodeNm, String jvmNm) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord javacoredump : Parameter Node "+ nodeNm +" Server "+ jvmNm +" will be taken.");
		String query = "WebSphere:type=JVM,node=" + nodeNm + ",process="+ jvmNm +",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (s.isEmpty()){
			logger.info("Cannot find JVM Object, it seems to be stopped.");
		} else {
			ObjectName srvObj = (ObjectName)s.iterator().next();
			String params[] = null;
			String signature[] = null;
			client.invoke(srvObj, "dumpThreads",  params, signature) ;
			logger.info("Taken Java Core on JVM "+ jvmNm +" on Node "+ nodeNm +".");
		}
	}

	/**
	 * Initiates a MustGather(Used for PMR, 3 Heap Dump and Java core set at 2 minute interval) Dump in a JVM process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws InterruptedException
	 */
	public void mustgather(String nodeNm, String jvmNm) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException, InterruptedException {
		logger.debug("Methord mustgather : Parameter Node "+ nodeNm +" Server "+ jvmNm +" will be taken.");
		String query = "WebSphere:type=JVM,node=" + nodeNm + ",process="+ jvmNm +",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (s.isEmpty()){
			logger.info("Cannot find JVM Object, it seems to be stopped.");
		} else {
			ObjectName srvObj = (ObjectName)s.iterator().next();
			String params[] = null;
			String signature[] = null;
			int count=0;
			while (count < 3){
				client.invoke(srvObj, "dumpThreads",  params, signature) ;
				client.invoke(srvObj, "generateHeapDump",  params, signature) ;
				logger.info("[ SET "+ count + " ] Dump on JVM "+ jvmNm +" on Node "+ nodeNm +".");
				Thread.sleep(120000);
				count++;
			}

		}
	}

	/**
	 * Return a List of Datasource deployed to a perticular JVM process along with its connection pools performance statistics.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @return ArrayList<DataSourceData> of Datasource performance statistics object
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws ConfigServiceException
	 */
	public ArrayList<DataSourceData> getDataSourceData(String nodeNm, String jvmNm) throws MalformedObjectNameException, ConnectorException, ConfigServiceException{
		logger.debug("Methord getDataSourceDataList : Parameter Node "+ nodeNm +" Server "+ jvmNm +".");
		ArrayList<DataSourceData> returnresult = new ArrayList<DataSourceData>();
		ObjectName queryName = new ObjectName("WebSphere:type=DataSource,node="+ nodeNm +",process="+ jvmNm +",*");
		Set dsObjLst = client.queryNames( queryName, null );
		Iterator cursor=dsObjLst.iterator();
		while (cursor.hasNext()) {
			ObjectName	dsObj = (ObjectName) cursor.next();
			String dsNm=dsObj.toString().split("name=")[1].split("\\,")[0];
			String target=dsObj.toString().split("mbeanIdentifier=")[1].split("\\,")[0];
			if (target.contains("servers")){
				String query = "Node="+ nodeNm + ":Server=" + jvmNm;
				ObjectName serverId = configService.resolve(session, query)[0];
				ObjectName dsName = ConfigServiceHelper.createObjectName( null, "DataSource", dsNm );
				ObjectName objId = configService.queryConfigObjects( session, serverId, dsName, null )[0];
				if (objId != null){
					logger.debug("Scoped to Server: "+ objId);
					ObjectName poolNm = ConfigServiceHelper.createObjectName( null, "ConnectionPool", null);
					ObjectName poolId = configService.queryConfigObjects( session, objId, poolNm, null )[0];
					if (poolId != null){
						Integer mxSz = (Integer) configService.getAttribute(session, poolId, "maxConnections");
						Integer mnSz = (Integer) configService.getAttribute(session, poolId, "minConnections");
						logger.debug("Server: "+ jvmNm +", Node Name: "+ nodeNm +", DataSource: "+ dsNm +", Min Pool: "+ mnSz +", Max Pool: "+ mxSz);
						returnresult.add(new DataSourceData(jvmNm, nodeNm, dsNm, mxSz, mnSz));
					}
				}
			} else {
				String clusterStr = target.split("clusters/")[1].split("/")[0];
				String query = "ServerCluster="+ clusterStr;
				ObjectName clusterId = configService.resolve(session, query)[0];
				ObjectName dsName = ConfigServiceHelper.createObjectName( null, "DataSource", dsNm );
				ObjectName objId = configService.queryConfigObjects( session, clusterId, dsName, null )[0];
				if (objId != null){
					logger.debug("Scoped to Cluster: "+ objId);
					ObjectName poolNm = ConfigServiceHelper.createObjectName( null, "ConnectionPool", null);
					ObjectName poolId = configService.queryConfigObjects( session, objId, poolNm, null )[0];
					if (poolId != null){
						Integer mxSz = (Integer) configService.getAttribute(session, poolId, "maxConnections");
						Integer mnSz = (Integer) configService.getAttribute(session, poolId, "minConnections");
						logger.debug("Cluster: "+ clusterStr +", DataSource: "+ dsNm +", Min Pool: "+ mnSz +", Max Pool: "+ mxSz);
						returnresult.add(new DataSourceData(jvmNm, nodeNm, dsNm, mxSz, mnSz));
					}
				}
			}
		}
		return returnresult;
	}

	/**
	 * Get the performance statistics of the JVM extracted from PMI data.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @return JvmPMIStat object, containing complete performance statistics for Heap Usage, CPU usage, Thread Pool Usage, Session Usage
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 */
	public JvmPMIStat getJVMPMI(String nodeNm, String jvmNm) throws InstanceNotFoundException, MBeanException, ReflectionException, ConnectorException, MalformedObjectNameException {
		logger.debug("Methord getJVMPMI : Parameter Node "+ nodeNm +" Server "+ jvmNm +".");
		JvmPMIStat jvmStats = new JvmPMIStat(jvmNm, nodeNm, false);
		String query = "WebSphere:type=Server,node=" + nodeNm + ",name="+ jvmNm +",*";
		ObjectName queryName = new ObjectName(query);
		Set sers = client.queryNames(queryName, null);
		if (!sers.isEmpty()){
			ObjectName srvObj = (ObjectName)sers.iterator().next();
			logger.debug("Server Object: "+ srvObj.toString());
			query = "WebSphere:type=Perf,node=" + nodeNm + ",process="+ jvmNm +",*";
			queryName = new ObjectName(query);
			Set perfs = client.queryNames(queryName, null);
			if (!perfs.isEmpty()){
				ObjectName perf = (ObjectName)perfs.iterator().next();
				logger.debug("PMI Object: "+ perf.toString());
				String[] signature = new String[] { "javax.management.ObjectName", "java.lang.Boolean" };
				Object[] params = new Object[] { srvObj, Boolean.TRUE };
				WSStats wsStats = (WSStats) client.invoke(perf, "getStatsObject", params, signature);
				WSStats runtimeStats = wsStats.getStats("jvmRuntimeModule");
				long maxHeap = ((WSBoundedRangeStatistic) runtimeStats.getStatistic("HeapSize")).getUpperBound();
				long minHeap = ((WSCountStatistic) runtimeStats.getStatistic("UsedMemory")).getCount();
				double ratioUsage = ((float) minHeap/(float) maxHeap);
				long percentUsage = (long) (ratioUsage*100);
				long cpuUsage = ((WSCountStatistic) runtimeStats.getStatistic("ProcessCpuUsage")).getCount();
				jvmStats.setHeapUtilization(percentUsage);
				jvmStats.setCpuUtilization(cpuUsage);
				logger.debug("Min Heap "+ minHeap +" Max Heap "+ maxHeap +" CPU Usage "+ cpuUsage);
				for (WSStats thpl : wsStats.getStats("threadPoolModule").getSubStats()) {
					String pl = thpl.getName();
					long sz = ((WSBoundedRangeStatistic)thpl.getStatistic("PoolSize")).getUpperBound();
					long at = ((WSRangeStatistic)thpl.getStatistic("PoolSize")).getCurrent();
					ThreadPoolStat tempPl = new ThreadPoolStat(pl, sz, at);
					logger.debug("Thread Pool "+ pl +" Pool Size "+ sz +" Active Count "+ at);
					jvmStats.getThreadPools().add(tempPl);
				}
				if (wsStats.getStats("servletSessionsModule") != null){
					for (WSStats ses : wsStats.getStats("servletSessionsModule").getSubStats()) {
						String mod = ses.getName();
						long lv = ((WSRangeStatistic)ses.getStatistic("LiveCount")).getCurrent();
						logger.debug("Session Module "+ mod +" Live Session Count"+ lv);
						SessionStat tempSesn = new SessionStat(mod, lv);
						jvmStats.getSessionStats().add(tempSesn);
					}
				}
				jvmStats.setUp(true);
			} else {
				logger.warn("Cannot get PMI Object for JVM, cause may be PMI not enabled.");
				jvmStats.setUp(false);
			}
		} else {
			logger.warn("Cannot get Server Object for JVM, cause may be JVM down.");
			jvmStats.setUp(false);
		}
		return jvmStats;
	}

	/**
	 * Find the Configuration detail for a Datasource deployed to a JVM.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @param dsNm - JNDI Name for Datasource String
	 * @return DatasourceConfig object containing Datasource Name, Configuration Object and Jndi name of the same
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException 
	 */
	public DatasourceConfig findDatasource(String nodeNm, String jvmNm, String dsNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException {
		logger.debug("Methord findDatasource : Parameter Node "+ nodeNm +" Server "+ jvmNm +" Datasource "+ dsNm +".");
		DatasourceConfig dsConfig=null;
		String query = "Node="+ nodeNm + ":Server=" + jvmNm;
		ObjectName server = configService.resolve(session, query)[0];
		ObjectName dsName = ConfigServiceHelper.createObjectName( null, "DataSource", dsNm );
		ObjectName queryName = new ObjectName("WebSphere:type=DataSource,name="+ dsNm +",node="+ nodeNm +",process="+ jvmNm +",*");
		Set s = client.queryNames(queryName, null);
		if (!s.isEmpty()){
			ObjectName dsObj = (ObjectName)s.iterator().next();
			String target=dsObj.toString().split("mbeanIdentifier=")[1].split("\\,")[0];
			if (target.contains("servers")){
				String dsQuery = "Node="+ nodeNm + ":Server=" + jvmNm;
				ObjectName serverId = configService.resolve(session, dsQuery)[0];
				ObjectName dsConfName = ConfigServiceHelper.createObjectName( null, "DataSource", dsNm );
				ObjectName objId = configService.queryConfigObjects( session, serverId, dsConfName, null )[0];
				String config_id=objId.getKeyProperty("_Websphere_Config_Data_Id");
				dsConfig = new DatasourceConfig(dsNm, config_id, null);
			} else {
				String clusterStr = target.split("clusters/")[1].split("/")[0];
				String dsQuery = "ServerCluster="+ clusterStr;
				ObjectName clusterId = configService.resolve(session, dsQuery)[0];
				ObjectName dsConfName = ConfigServiceHelper.createObjectName( null, "DataSource", dsNm );
				ObjectName objId = configService.queryConfigObjects( session, clusterId, dsConfName, null )[0];
				String config_id=objId.getKeyProperty("_Websphere_Config_Data_Id");
				dsConfig = new DatasourceConfig(dsNm, config_id, null);
			}
		} else {
			logger.info("No datasource found in Server scope matching given name "+ dsNm +".");
		}
		return dsConfig;
	}

	/**
	 * Dumps the Connection pool statistics of a Datasource deployed on a JVM process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @param dsNm - JNDI Name for Datasource String
	 * @return String containing the Connection pool status
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public String dumpDatasource(String nodeNm, String jvmNm, String dsNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		ObjectName dsObjNm = new ObjectName( "WebSphere:*,type=DataSource,name="+ dsNm +",node="+ nodeNm +",Server="+ jvmNm);
		Set dsObjLst = client.queryNames( dsObjNm, null );
		if (! dsObjLst.isEmpty()){
			ObjectName dsObj = (ObjectName) dsObjLst.iterator().next();
			return (String) client.invoke( dsObj, "showPoolContents", null, null);
		} else {
			logger.warn("Datasource object "+ dsNm +" cannot be found on JVM "+ jvmNm +" on Node "+ nodeNm);
			return "No data extracted!!!";
		}
	}

	/**
	 * Purge the connections in a Connection pool statistics of a Datasource deployed on a JVM process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @param dsNm - JNDI Name for Datasource String
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void purgeDatasourcePool(String nodeNm, String jvmNm, String dsNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		ObjectName dsObjNm = new ObjectName( "WebSphere:*,type=DataSource,name="+ dsNm +",node="+ nodeNm +",Server="+ jvmNm);
		Set dsObjLst = client.queryNames( dsObjNm, null );
		if (! dsObjLst.isEmpty()){
			ObjectName dsObj = (ObjectName) dsObjLst.iterator().next();
			client.invoke( dsObj, "purgePoolContents", null, null);
			logger.info("Successfully purged connection pool content for Datasource with JNDI "+ dsNm +" for Node "+ nodeNm +" JVM "+ jvmNm);
		} else {
			logger.warn("Datasource object "+ dsNm +" cannot be found on JVM "+ jvmNm +" on Node "+ nodeNm);
		}
	}

	/**
	 * Performs a Test Connection on the Datasource object deployed on a JVM process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @param dsNm - JNDI Name for Datasource String
	 * @return Success if the Test Connection operation was successful else Failed
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public String testDatasourceConnection(String nodeNm, String jvmNm, String dsNm) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord testDatasourceConnection : Parameter Node "+ nodeNm +" Server "+ jvmNm +" Datasource "+ dsNm +".");
		DatasourceConfig ds=this.findDatasource(nodeNm,jvmNm,dsNm);
		if (ds == null){
			logger.warn("Cannot find Datasource with JNDI "+ dsNm +" for Node "+ nodeNm +" JVM "+ jvmNm);
			return null;
		} else {
			logger.info("Found Datasource "+ ds.toString());
			ObjectName queryName = new ObjectName("WebSphere:type=DataSourceCfgHelper,node="+ nodeNm +",process="+ jvmNm +",*");
			Set dsObjLst = client.queryNames( queryName, null );
			if (!dsObjLst.isEmpty()){
				ObjectName dsObj = (ObjectName) dsObjLst.iterator().next();
				if (dsObj == null){
					logger.warn("Failed to find DataSourceCfgHelper to preform Testconnection.");
					return null;
				} else {
					String[] signature = { "java.lang.String" };
					Object[] params = { ds.getDatasourecConfigId() };
					Object result = client.invoke(dsObj, "testConnection", params, signature);
					if (result.toString().equals("0")) {
						logger.info("Test Connection on Node "+ nodeNm +" Server "+ jvmNm +" Datasource "+ dsNm +" was successful!");
						return "JVM:"+ jvmNm +", Node:"+ nodeNm +", Test Connection: Success.";
					} else {
						logger.info("Test Connection on Node "+ nodeNm +" Server "+ jvmNm +" Datasource "+ dsNm +" was unsuccessful!");
						return "JVM:"+ jvmNm +", Node:"+ nodeNm +", Test Connection: Failed.";
					}
				}
			} else {
				logger.warn("Cannot find DataSourceCfgHelper for JVM "+ jvmNm +" on Node "+ nodeNm +" probably the JVM might be down.");
				return "JVM:"+ jvmNm +", Node:"+ nodeNm +", Test Connection: JVM Down.";
			}
		}
	}

	/**
	 * List the Listener ports resources configured in a perticular JVM process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @return ArrayList of ListenerPortConfig objects containing Listener Port name, Configuration Id and its current status
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public ArrayList<ListenerPortConfig> getListenerPort(String nodeNm, String jvmNm) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord getListenerPort : Parameter Node "+ nodeNm +" Server "+ jvmNm +".");
		ArrayList<ListenerPortConfig> returnresult=new ArrayList<ListenerPortConfig>();
		ObjectName queryName = new ObjectName("WebSphere:type=ListenerPort,node="+ nodeNm +",process="+ jvmNm +",*");
		Set lsObjLst = client.queryNames( queryName, null );
		Iterator cursor=lsObjLst.iterator();
		while (cursor.hasNext()) {
			ObjectName	lpObj = (ObjectName) cursor.next();
			String lpNm = lpObj.toString().split(":name=")[1].split(",process=")[0];
			Object stat = client.invoke( lpObj, "isStarted", null, null);
			boolean status = false;
			if (stat.toString().equals("true")){
				status = true;
			} 
			ListenerPortConfig newLP = new ListenerPortConfig(jvmNm, nodeNm, lpNm, lpObj.toString(), status);
			logger.debug(newLP);
			returnresult.add(newLP);
		}
		return returnresult;
	}

	/**
	 * Method to be used for Listener Port Start/Stop.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @param lpNm - Listener Port Name String
	 * @param op - String containing the operation type. "Stop" to Stop Listener Port and "Start" to Start it
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void listenerPortAdmin(String nodeNm, String jvmNm, String lpNm, String op) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord getListenerPort : Parameter Node "+ nodeNm +" Server "+ jvmNm +" Listener Port "+ lpNm +" Operation "+ op +".");
		ObjectName queryName = new ObjectName("WebSphere:type=ListenerPort,name="+ lpNm +",node="+ nodeNm +",process="+ jvmNm +",*");
		Set lsObjLst = client.queryNames( queryName, null );
		if (!lsObjLst.isEmpty()) {
			ObjectName lp = (ObjectName)lsObjLst.iterator().next();
			if (op.equalsIgnoreCase("stop")){
				client.invoke(lp, "stop", null, null);
			} else {
				client.invoke(lp, "start", null, null);
			}
		} else {
			logger.warn("Cannot find Listener Port Object with name "+ lpNm +" on Node "+ nodeNm +" Server "+ jvmNm);
		}

	}

	/**
	 * List the J2C Connection Factory resource configured for the JVM Process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @return ArrayList of J2CConnection object containing Connection Factory Name, Configuration Id, Max Size of Connection Pool and Current Active Connection
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws ConfigServiceException 
	 */
	public ArrayList<J2CConnection> getJ2CConnectionFactoryList(String nodeNm, String jvmNm) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException, ConfigServiceException {
		logger.debug("Methord getJ2CConnectionFactoryList : Parameter Node "+ nodeNm +" Server "+ jvmNm +".");
		ArrayList<J2CConnection> returnresult=new ArrayList<J2CConnection>();
		ObjectName queryName = new ObjectName("WebSphere:type=J2CConnectionFactory,node="+ nodeNm +",process="+ jvmNm +",*");
		Set cfObjLst = client.queryNames( queryName, null );
		Iterator cursor=cfObjLst.iterator();
		while (cursor.hasNext()) {
			ObjectName	cfObj = (ObjectName) cursor.next();
			String cfNm = cfObj.toString().split("name=")[1].split("\\,")[0];
			String target=cfObj.toString().split("mbeanIdentifier=")[1].split("\\,")[0];
			if (target.contains("servers")){
				String query = "Node="+ nodeNm + ":Server=" + jvmNm;
				ObjectName serverId = configService.resolve(session, query)[0];
				ObjectName cfName = ConfigServiceHelper.createObjectName( null, "J2CConnectionFactory", cfNm );
				ObjectName objId = configService.queryConfigObjects( session, serverId, cfName, null )[0];
				if (objId != null){
					logger.debug("Scoped to Server: "+ objId);
					ObjectName poolNm = ConfigServiceHelper.createObjectName( null, "ConnectionPool", null);
					ObjectName poolId = configService.queryConfigObjects( session, objId, poolNm, null )[0];
					if (poolId != null){
						Integer mxSz = (Integer) configService.getAttribute(session, poolId, "maxConnections");
						Integer mnSz = (Integer) configService.getAttribute(session, poolId, "minConnections");
						logger.debug("Server: "+ jvmNm +", Node Name: "+ nodeNm +", J2CConnectionFactory: "+ cfNm +", Min Pool: "+ mnSz +", Max Pool: "+ mxSz);
						returnresult.add(new J2CConnection(jvmNm, nodeNm, cfNm, objId.toString(), mxSz, mnSz));
					}
				}
			} else {
				String clusterStr = target.split("clusters/")[1].split("/")[0];
				String query = "ServerCluster="+ clusterStr;
				logger.debug("Cluster Query String: "+ query);
				ObjectName[] clusterIdList = configService.resolve(session, query);
				if (clusterIdList.length > 0 ){ 
					ObjectName clusterId = clusterIdList[0];
					ObjectName cfName = ConfigServiceHelper.createObjectName( null, "J2CConnectionFactory", cfNm );
					ObjectName[] objIdList = configService.queryConfigObjects( session, clusterId, cfName, null ) ;
					if (objIdList.length > 0 ){
						ObjectName objId = objIdList[0];
						if (objId != null){
							logger.debug("Scoped to Cluster: "+ objId);
							ObjectName poolNm = ConfigServiceHelper.createObjectName( null, "ConnectionPool", null);
							ObjectName poolId = configService.queryConfigObjects( session, objId, poolNm, null )[0];
							if (poolId != null){
								Integer mxSz = (Integer) configService.getAttribute(session, poolId, "maxConnections");
								Integer mnSz = (Integer) configService.getAttribute(session, poolId, "minConnections");
								logger.debug("Cluster: "+ clusterStr +", J2CConnectionFactory: "+ cfNm +", Min Pool: "+ mnSz +", Max Pool: "+ mxSz);
								returnresult.add(new J2CConnection(jvmNm, nodeNm, cfNm, objId.toString(), mxSz, mnSz));
							}
						}
					}
				}
			}
		}
		return returnresult;
	}

	/**
	 * Purge a Connection Pool for a J2C Connection Factory resource configured for the JVM Process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @param j2cf - J2C Connection Factory Name String
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws ConnectorException
	 */
	public void purgeJ2CPool(String nodeNm, String jvmNm, String j2cf) throws MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException, ConnectorException {
		logger.debug("Methord purgeJ2CPool : Parameter Node "+ nodeNm +" Server "+ jvmNm +" J2CConnection Factory "+ j2cf +".");
		ObjectName queryName = new ObjectName("WebSphere:type=J2CConnectionFactory,name="+ j2cf +",node="+ nodeNm +",process="+ jvmNm +",*");
		Set j2CObjLst = client.queryNames( queryName, null );
		if (!j2CObjLst.isEmpty()) {
			ObjectName j2c = (ObjectName)j2CObjLst.iterator().next();
			client.invoke(j2c, "purgePoolContents", null, null);
			logger.info("Purged J2CConnectionFactory Connection Pool with name "+ j2cf +" on Node "+ nodeNm +" Server "+ jvmNm);
		} else {
			logger.warn("Cannot find J2CConnectionFactory Object with name "+ j2cf +" on Node "+ nodeNm +" Server "+ jvmNm);
		}

	}

	/**
	 * Dumps the connections in a Connection pool statistics of a Datasource deployed on a JVM process.
	 * @param nodeNm - Node Name String
	 * @param jvmNm - JVM Name String
	 * @param j2cf - J2C Connection Factory Name String
	 * @return String containing the Connection pool status
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws ConnectorException
	 */
	public String dumpJ2CPool(String nodeNm, String jvmNm, String j2cf) throws MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException, ConnectorException {
		logger.debug("Methord dumpJ2CPool : Parameter Node "+ nodeNm +" Server "+ jvmNm +" J2CConnection Factory "+ j2cf +".");
		ObjectName queryName = new ObjectName("WebSphere:type=J2CConnectionFactory,name="+ j2cf +",node="+ nodeNm +",process="+ jvmNm +",*");
		Set j2CObjLst = client.queryNames( queryName, null );
		if (!j2CObjLst.isEmpty()) {
			ObjectName j2c = (ObjectName)j2CObjLst.iterator().next();
			return (String)client.invoke(j2c, "showPoolContents", null, null);
		} else {
			logger.warn("Cannot find J2CConnectionFactory Object with name "+ j2cf +" on Node "+ nodeNm +" Server "+ jvmNm);
			return null;
		}

	}	
}
