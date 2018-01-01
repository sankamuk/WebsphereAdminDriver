/**
 * This is a utility provided to test the library.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmin.testing;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.mukherjee.sankar.wsadmin.ApplicationOperations;
import com.mukherjee.sankar.wsadmin.ClusterOperations;
import com.mukherjee.sankar.wsadmin.JvmOperations;
import com.mukherjee.sankar.wsadmin.WsadminClient;
import com.mukherjee.sankar.wsadmin.discovery.DiscoveryUtility;
import com.mukherjee.sankar.wsadmindata.JvmDataSource;
import com.mukherjee.sankar.wsadmindata.JvmPMIStat;


public class TestClient {
	
	private static Logger logger = Logger.getLogger(TestClient.class.getName());

	/**
	 * This is a Testing Client. It doesnot provides any functionality to the library but can be used to test the library. 
	 * @param args - DMGR Host[String] Soap Port[String] User[String] Password[String] 
	 */
	public static void main(String[] args) {
		String host = args[0];
		String port = args[1];
		String user = args[2];
		String passwd = args[3];
		
		WsadminClient client = new WsadminClient();
		try {
			if (client.initialize(host, port, user, passwd)) {
				JvmOperations jvmClient = client.getJvmV();
				ApplicationOperations appClient = client.getAppV();
				ClusterOperations clusterClient = client.getClusterV();
/*				String jvmNm = "TEST01_APP01";
				String ndNm = "POC_KIOSK_ENV01_UKLVADWEB01B_APPNODE_1";*/

				
/*				logger.info("Node/JVM List Testing .....");
				ArrayList<String> nodeList = jvmClient.returnNodes();
				for (String node : nodeList) {
					logger.info("Node "+ node);
					logger.info("Retriving JVM for Node :");
					ArrayList<String> jvmList = jvmClient.returnJvms(node);
					for (String jvm : jvmList) {
						logger.info(jvm);

					}
					logger.info("Done for Node ......");
				}
				logger.info("End of Node/JVM List Testing .....");

				logger.info("Testing the JVM Cluster Membership");
				String clusterNm = jvmClient.returnClusterforJVM(ndNm, jvmNm);
				if (clusterNm != null){
					logger.info(clusterNm);
				} else {
					logger.info("NULL");
				}
				logger.info("Done testing JVM Cluster Membership");

				logger.info("Start/Stop Testing .....");

				if (jvmClient.isJVMStarted(ndNm, jvmNm)) {
					logger.info("JVM "+ jvmNm +" is Running!!!");
				} else {
					logger.info("JVM "+ jvmNm +" is NOT Running!!!");
				}

				logger.info("Stopping the JVM");
				jvmClient.stopJVM(ndNm, jvmNm);
				logger.info("Done stopping JVM");

				if (jvmClient.isJVMStarted(ndNm, jvmNm)) {
					logger.info("JVM "+ jvmNm +" is Running!!!");
				} else {
					logger.info("JVM "+ jvmNm +" is NOT Running!!!");
				}

				logger.info("Starting JVM");
				jvmClient.startJVM(ndNm, jvmNm);
				logger.info("Done starting JVM");

				if (jvmClient.isJVMStarted(ndNm, jvmNm)) {
					logger.info("JVM "+ jvmNm +" is Running!!!");
				} else {
					logger.info("JVM "+ jvmNm +" is NOT Running!!!");
				}

				logger.info("End of Start/Stop Testing ......");*/


				/*				logger.info("Dump Testing .....");
				jvmClient.javacoredump(ndNm, jvmNm);
				logger.info("End of Dump Testing .....");*/

				/*				logger.info("Datasource Testing .....");
				ArrayList<DataSourceData> dsList = jvmClient.getDataSourceData(ndNm, jvmNm);
				for (DataSourceData ds : dsList) {
					logger.info(ds);
				}
				logger.info("End of Datasource Testing .....");		*/		

/*				logger.info("JVM PMI Testing .....");
				JvmPMIStat jvmS = jvmClient.getJVMPMI(ndNm, jvmNm);
				logger.info(jvmS.toString());
				logger.info("End of JVM PMI Testing .....");*/
				
/*				logger.info("Datasource Id Retrival Testing .....");
				jvmClient.findDatasource(ndNm, jvmNm, "jdbc/HRListerDs");
				logger.info("End of Datasource Id Retrival Testing .....");*/
				
/*				logger.info("Datasource Pool Dump Testing .....");
				jvmClient.purgeDatasourcePool(ndNm, jvmNm, "jdbc/HRListerDs");
				logger.info("End of Datasource Pool Dump Testing .....");				
				
				logger.info("Datasource Pool Dump Testing .....");
				logger.info(jvmClient.dumpDatasource(ndNm, jvmNm, "jdbc/HRListerDs"));
				logger.info("End of Datasource Pool Dump Testing .....");*/
				
/*				logger.info("Datasource TestConnection Testing .....");
				logger.info(jvmClient.testDatasourceConnection(ndNm, jvmNm, "estore"));
				logger.info("End of Datasource TestConnection Testing .....");	*/		
				
/*				logger.info("Listener Port List Testing .....");
				logger.info(jvmClient.getListenerPort(ndNm, jvmNm));
				logger.info("End of Listener Port List Testing .....");	*/	
				
/*				logger.info("Listener Port Stop Testing .....");
				jvmClient.listenerPortAdmin(ndNm, jvmNm, "KIOSK_LP_01", "stop");
				logger.info("End of Listener Port Stop Testing .....");		
				logger.info("Listener Port List Testing .....");
				logger.info(jvmClient.getListenerPort(ndNm, jvmNm));
				logger.info("End of Listener Port List Testing .....");*/
				
/*				logger.info("J2CConnectionFactory List Testing .....");
				logger.info(jvmClient.getJ2CConnectionFactoryList(ndNm, jvmNm));
				logger.info("End of J2CConnectionFactory List Testing .....");		
				*/
				
/*				logger.info("J2CConnectionFactory Dump Testing .....");
				logger.info(jvmClient.dumpJ2CPool(ndNm, jvmNm, "KIOSK_QCF_01"));
				logger.info("End of J2CConnectionFactory Dump Testing .....");	*/
				
/*				logger.info("Application List Testing .....");
				logger.info(appClient.getApplicationList());
				logger.info("End of Application List Testing .....");*/
				
/*				logger.info("Application Info Testing .....");
				logger.info(appClient.applicationInfo("EStoreApplication"));
				logger.info("End of Application Info Testing .....");*/
				
/*				logger.info("Application Info Testing .....");
				logger.info(appClient.listModules("WSN Service Point Application"));
				logger.info("End of Application Info Testing .....");*/
				
/*				logger.info("Cluster to JVM List Testing .....");
				ClusterTarget temp=new ClusterTarget("KIOSK_APP_CLUSTER_01");
				HashSet<JvmTarget> result= appClient.getJVMforCluster(temp);
				for (JvmTarget jvmTarget : result) {
					logger.info(jvmTarget);
				}
				logger.info("End of Cluster to JVM List Testing .....");*/
				
/*				logger.info("Application Runtime List Testing .....");
				HashSet<JvmTarget> result= appClient.listRuntime("EStoreApplication");
				for (JvmTarget jvmTarget : result) {
					logger.info(jvmTarget);
				}
				logger.info("End of Application Runtime List Testing .....");*/

/*				logger.info("Application performance Testing .....");
				logger.info(appClient.getApplicationPMI("EStoreApplication"));
				logger.info("End of Application performance Testing .....");*/
				
/*				logger.info("Cluster List Testing .....");
				logger.info("Dynamic Cluster"+ clusterClient.getClusterDynamic());
				logger.info("Static Cluster"+ clusterClient.getClusterStatic());
				logger.info("End of Cluster List Testing .....");*/
				
/*				String clusterNmS = "KIOSK_SIB_CLUSTER";
				String clusterNmD = "KIOSK_APP_CLUSTER_01";*/
				
/*				logger.info("Cluster JVM Testing .....");
				logger.info("Dynamic Cluster"+ clusterClient.getJVMforCluster(clusterNmS));
				logger.info("End of Cluster JVM Testing .....");*/
				
/*				logger.info("Cluster State Testing .....");
				logger.info("Dynamic Cluster"+ clusterClient.getClusterState(clusterNmD, "Dynamic"));
				logger.info("End of Cluster State Testing .....");*/
				
/*				logger.info("Cluster PMI Testing .....");
				for (JvmPMIStat pmi : clusterClient.getClusterPMI(clusterNmS)) {
					logger.info(pmi);
				}
				logger.info("End of Cluster PMI Testing .....");*/
				
/*				logger.info("Cluster Info Testing .....");
				logger.info("Dynamic Cluster"+ clusterClient.clusterInfo(clusterNmD));
				logger.info("End of Cluster Info Testing .....");*/
				
/*				logger.info("Cluster OpMode Change Testing .....");
				clusterClient.changeOperationMode(clusterNmD, "MANUAL");
				logger.info(clusterClient.getClusterState(clusterNmD, "Dynamic"));
				logger.info("End of Cluster OpMode Change Testing .....");*/
				
/*				logger.info("Cluster javacoredump Testing .....");
				clusterClient.javacoredump(clusterNmD);
				logger.info("End of Cluster javacoredump Testing .....");*/
				
/*				logger.info("Cluster Stop Testing .....");
				clusterClient.stopCluster(clusterNmD);
				logger.info("End of Stop Cluster Testing .....");*/
				
/*				logger.info("Cluster Datasource Statistics Testing .....");
				for (JvmDataSource ds : clusterClient.getDatasourceData(clusterNmD)) {
					logger.info(ds);
				}
				logger.info("End of Datasource Statistics Testing .....");*/
				
/*				logger.info("Cluster Datasource Test Connection Testing .....");
				logger.info(clusterClient.testConnection(clusterNmD, "jdbc/HRListerDs"));
				logger.info("End of Cluster Datasource Test Connection Testing .....");*/
				
/*				logger.info("Cluster Listener port Data Testing .....");
				logger.info(clusterClient.getListenerPortCluster(clusterNmD));
				logger.info("End of Cluster Listener port Data Testing .....");*/
				
/*				logger.info("Cluster Listener port stop Testing .....");
				clusterClient.stoptListenerPortCluster(clusterNmD, "KIOSK_LP_01");
				logger.info("End of Cluster Listener port stop Testing .....");*/
				
/*				logger.info("Cluster J2C Connection Factory Data Testing .....");
				logger.info(clusterClient.getJ2CConnectionPoolCluster(clusterNmD));
				logger.info("End of J2C Connection Factory Data Testing .....");*/
				
/*				logger.info("Cluster J2C Connection Factory Pool Purge Testing .....");
				clusterClient.purgeAllJ2CConnectionPoolCluster(clusterNmD, "KIOSK_QCF_01");
				logger.info("End of Cluster J2C Connection Factory Pool Purge Testing .....");*/
				
/*				logger.info("Discovery Tool Testing .....");
				logger.info(DiscoveryUtility.dicoverCell(client, host, Integer.parseInt(port), user, passwd));
				logger.info("End of Discovery Tool Testing .....");*/
				
/*				logger.info("Application DumpClassLoader Testing .....");
				logger.info(appClient.dumpClassLoader("EStoreApplication", "EStore-war.war", "KIOSK_APP_CLUSTER_02_POC_KIOSK_ENV01_UKLVADWEB01B_APPNODE_1", "POC_KIOSK_ENV01_UKLVADWEB01B_APPNODE_1"));
				logger.info("End of Application DumpClassLoader Testing .....");*/
				
/*				logger.info("Application Namespace Testing .....");
				logger.info(appClient.dumpNameSpace("KIOSK_APP_CLUSTER_02_POC_KIOSK_ENV01_UKLVADWEB01B_APPNODE_1", "POC_KIOSK_ENV01_UKLVADWEB01B_APPNODE_1"));
				logger.info("End of Application Namespace Testing .....");*/
				
/*				logger.info("Application Status Testing .....");
				logger.info("Application Status for EStoreApplication is "+ appClient.isStarted("EStoreApplication"));
				logger.info("End of Application Status Testing .....");*/
				
/*				logger.info("Application Cluster List Testing .....");
				logger.info("Application Cluster List for EStoreApplication is "+ appClient.listCluster("EStoreApplication"));
				logger.info("End of Application Cluster List Testing .....");*/
				
/*				logger.info("Check J2C Testing .....");
				//logger.info("J2C Pool :   "+ jvmClient.getJ2CConnectionFactoryList("POC_KIOSK_ENV01_UKLVADWEB01B_APPNODE_1", "KIOSK_APP_CLUSTER_01_POC_KIOSK_ENV01_UKLVADWEB01B_APPNODE_1"));
				logger.info("J2C Pool: "+ clusterClient.getJ2CConnectionPoolCluster("KIOSK_APP_CLUSTER_01"));
				logger.info("End of Check J2C Testing .....");*/
				
/*				logger.info("Check Extract Cell Config Testing .....");
				logger.info(DiscoveryUtility.dicoverCell(client, host, Integer.parseInt(port), user, passwd));
				logger.info("End of Extract Cell Config Testing .....");*/
				
				logger.info("Application Deployment Testing .....");
				String appName = args[4];
				String appPath = args[5];
				appClient.redeployApplication(appName, appPath);
				logger.info("End of Application Deployment Testing .....");
				
/*				logger.info("Application Deployment Testing .....");
				logger.info("Node List: "+ appClient.getNodeList("EStoreApplication"));
				logger.info("End of Application Deployment Testing .....");*/
				
/*				logger.info("Application Details Testing .....");
				appClient.getApplicationDetails("EStoreApplication");
				logger.info("End of Application Details Testing .....");*/
			}
		} catch (Exception e) {
			logger.info("ERROR!!!");
			e.printStackTrace();
		}


	}
}
