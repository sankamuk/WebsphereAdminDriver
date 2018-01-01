/**
 * Class to perform Java EE Application Administration.
 * @author Sankar Mukherjee
 * @version 01, 12 March, 2015
 */

package com.mukherjee.sankar.wsadmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.print.DocFlavor.STRING;

import org.apache.log4j.Logger;
import org.python.modules.struct;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementFactory;
import com.ibm.websphere.management.application.client.AppDeploymentController;
import com.ibm.websphere.management.application.client.AppDeploymentException;
import com.ibm.websphere.management.application.client.AppDeploymentTask;
import com.ibm.websphere.management.configservice.ConfigDataId;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSAverageStatistic;
import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;
import com.ibm.websphere.pmi.stat.WSCountStatistic;
import com.ibm.websphere.pmi.stat.WSStats;
import com.mukherjee.sankar.wsadmindata.AppDeploymentTaskData;
import com.mukherjee.sankar.wsadmindata.ApplicationInformation;
import com.mukherjee.sankar.wsadmindata.ApplicationModuleDetail;
import com.mukherjee.sankar.wsadmindata.ApplicationPerformance;
import com.mukherjee.sankar.wsadmindata.ClusterTarget;
import com.mukherjee.sankar.wsadmindata.JvmTarget;
import com.mukherjee.sankar.wsadmindata.ModulePerformance;
import com.mukherjee.sankar.wsadmindata.PropertySet;
import com.mukherjee.sankar.wsadmindata.URIPerformance;

public class ApplicationOperations {
	private static Logger logger = Logger.getLogger(ApplicationOperations.class.getName());
	private AdminClient client = null ;
	private Session session = null ;
	private ConfigService configService = null;
	private String cellName = null;
	private AppManagement appManager = null;
	private ClusterOperations cluster = null;

	/**
	 * Constructor not be used to intialise manually as WsadminClient should be used to intialise this object.
	 * @param admc - AdminClient Object passed which should have a valid connection to Websphere Cell
	 * @param ses - Session Object of a already created Session for a Connection to Websphere Cell
	 * @param conf - ConfigService object which is already intialised to perform  Websphere Cell configuration operations
	 * @param cell - Cell Name for a Websphere Cell to which AdminClient already connected
	 */
	public ApplicationOperations(AdminClient admc, Session ses, ConfigService conf, AppManagement appM, String cell, ClusterOperations cl) {
		logger.debug("Methord ApplicationOperations "+ admc.toString() +" Session "+ ses.toString());
		this.client = admc;
		this.session = ses;
		this.configService = conf;
		this.cellName = cell;
		this.appManager=appM;
		this.cluster=cl;
	}

	/**
	 * List the J2EE Application Installed in the Cell
	 * @return ArrayList of String for Application Name
	 * @throws AdminException 
	 */
	public ArrayList<String> getApplicationList() throws AdminException {
		logger.debug("Methord getApplicationList : Parameter .");
		ArrayList<String> appList = new ArrayList<String>();
		Hashtable preferences = new Hashtable();
		preferences.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		Vector appL = appManager.listApplications(preferences, null);
		for (int i = 0; i < appL.size(); i++) {
			appList.add((String) appL.get(i));
		}
		return appList;
	}

	/**
	 * Returns information about an Application
	 * @param appName - String Name of the Application
	 * @return
	 * @throws AdminException 
	 */
	public ApplicationInformation applicationInfo(String appName) throws AdminException {
		logger.debug("Methord getApplicationList : Parameter Application Name "+ appName +".");
		ApplicationInformation returnresult =  new ApplicationInformation();
		Hashtable preferences = new Hashtable();
		preferences.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		Vector allTask = appManager.getApplicationInfo(appName, preferences, session.toString());
		for (Object task : allTask) {
			AppDeploymentTask taskObj=(AppDeploymentTask) task;			
			if (taskObj.getTaskData() != null){
				String classNm = task.getClass().getSimpleName().toString();
				AppDeploymentTaskData temp = new AppDeploymentTaskData(classNm);
				String[] column = taskObj.getTaskData()[0];
				for (int i = 1; i < taskObj.getTaskData().length; i++) {
					String[] value = taskObj.getTaskData()[i];
					for (int j = 0; j < column.length; j++) {
						PropertySet tempProp = new PropertySet(column[j], value[j]);
						temp.getTaskProperty().add(tempProp);
					}
				}
				returnresult.getInfoList().add(temp);
			}
		}
		return returnresult;
	}

	/**
	 * Returns status of an Application
	 * @param appName - String representing Application Name 
	 * @return True in case JVM running, False otherwise 
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 */
	public String isStarted(String appName) throws MalformedObjectNameException, ConnectorException {
		logger.debug("Methord isStarted : Parameter Application Name "+ appName +".");
		String query = "WebSphere:type=Application,name=" + appName + ",*";
		ObjectName queryName = new ObjectName(query);
		Set s = client.queryNames(queryName, null);
		if (s.isEmpty()) {
			return "False";
		} else {
			return "True";
		}
	}

	/**
	 * Stop an running Application
	 * @param appName - String representing Application Name
	 * @throws MalformedObjectNameException 
	 * @throws ConnectorException 
	 * @throws AdminException 
	 */
	public void stopApplication(String appName) throws MalformedObjectNameException, ConnectorException, AdminException {
		logger.debug("Methord stopApplication : Parameter Application Name "+ appName +".");
		Hashtable preferences = new Hashtable();
		preferences.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		if (appManager.checkIfAppExists(appName, preferences, null)){
			appManager.stopApplication(appName, preferences, null);
			logger.info("Successfully stopped "+ appName +" application.");
		} else {
			logger.info("Could not stop "+ appName +" application. As it was not found deployed on this environment.");
		}
	}

	/**
	 * Starts an Application
	 * @param appName - String representing Application Name
	 * @throws MalformedObjectNameException 
	 * @throws ConnectorException 
	 * @throws AdminException 
	 */
	public void startApplication(String appName) throws MalformedObjectNameException, ConnectorException, AdminException {
		logger.debug("Methord startApplication : Parameter Application Name "+ appName +".");
		Hashtable preferences = new Hashtable();
		preferences.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		if (appManager.checkIfAppExists(appName, preferences, null)){
			appManager.startApplication(appName, preferences, null);
			logger.info("Successfully started "+ appName +" application.");
		} else {
			logger.info("Could not start "+ appName +" application. As it was not found deployed on this environment.");
		}
	}

	/**
	 * List Modules and there target for an Application
	 * @param appName - String representing Application Name
	 * @return ArrayList of ApplicationModuleDetail containing Application Module name Target Server/Cluster detail
	 * @throws AdminException
	 */
	public ArrayList<ApplicationModuleDetail> listModules(String appName) throws AdminException {
		logger.debug("Methord listModules : Parameter Application Name "+ appName +".");
		ArrayList<ApplicationModuleDetail> returnresult = new ArrayList<ApplicationModuleDetail>();
		Hashtable preferences = new Hashtable();
		preferences.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		AppDeploymentTask task = (AppDeploymentTask) appManager.listModules(appName, preferences, null);
		if (task.getTaskData() != null){
			for (int i = 1; i < task.getTaskData().length; i++) {
				String appModule = task.getTaskData()[i][1];
				String targetString = task.getTaskData()[i][2];
				logger.debug("Module Name:"+ appModule +" Target String:"+ targetString);
				ApplicationModuleDetail newAppMod = new ApplicationModuleDetail(appName, appModule);
				if (targetString.indexOf("+") == -1){
					logger.info("Single component mapping detected.");
					if (targetString.indexOf("cluster=") != -1){
						logger.info("Detected Cluster Target for Application "+ appName);
						ClusterTarget tempTar = new ClusterTarget(targetString.split("cluster=")[1]);
						newAppMod.getTargetList().add(tempTar);
					} else if (targetString.indexOf("server=") != -1) {
						logger.info("Detected Cluster Target for Application "+ appName);
						JvmTarget tempTar = new JvmTarget(targetString.split("server=")[1], targetString.split("node=")[1].split(",")[0]);
						newAppMod.getTargetList().add(tempTar);
					} else {
						logger.warn("Incorrectly formated target mapping, cannot detect its mapped to Server or CLuster.");
					}
				} else {
					logger.info("Multiple component mapping detected.");
					for (String targt : targetString.split("\\+")) {

						if (targt.indexOf("cluster=") != -1){
							logger.info("Detected Cluster Target for Application "+ appName);
							ClusterTarget tempTar = new ClusterTarget(targt.split("cluster=")[1]);
							newAppMod.getTargetList().add(tempTar);
						} else if (targt.indexOf("server=") != -1) {
							logger.info("Detected Cluster Target for Application "+ appName);
							JvmTarget tempTar = new JvmTarget(targt.split("server=")[1], targt.split("node=")[1].split(",")[0]);
							newAppMod.getTargetList().add(tempTar);
						} else {
							logger.warn("Incorrectly formated target mapping, cannot detect its mapped to Server or CLuster.");
						}
					}
				}
				returnresult.add(newAppMod);
			}
		}
		return returnresult;
	}

	/**
	 * List the Cluster list where the particular Application has been deployed.
	 * @param appName - String representing Application Name
	 * @return List of String containing Cluster Names
	 * @throws AdminException
	 * @throws ConnectorException
	 */
	public HashSet<String> listCluster(String appName) throws AdminException, ConnectorException {
		logger.debug("Methord listCluster : Parameter Application Name "+ appName +".");
		HashSet<String> listCluster=new HashSet<String>();
		ArrayList<ApplicationModuleDetail> modTarget = listModules(appName);
		for (ApplicationModuleDetail applicationModuleDetail : modTarget) {
			for (Object targetN: applicationModuleDetail.getTargetList()){
				if (targetN instanceof ClusterTarget){
					logger.info("Found Cluster Object"+ targetN.toString());
					if ( ! listCluster.contains(((ClusterTarget) targetN).getClusterName()) ){
						listCluster.add(((ClusterTarget) targetN).getClusterName());
					}
				}
			}
		}
		return listCluster;
	}

	/**
	 * List the JVM Runtime list where the particular Application has been deployed.
	 * @param appName - String representing Application Name
	 * @return HashSet of JvmTarget containing Jvm detail of the runtime
	 * @throws AdminException
	 * @throws ConnectorException
	 */
	public HashSet<JvmTarget> listRuntime(String appName) throws AdminException, ConnectorException {
		logger.debug("Methord listRuntime : Parameter Application Name "+ appName +".");
		ArrayList<ApplicationModuleDetail> modTarget = listModules(appName);
		HashSet<JvmTarget> returnresult = new HashSet<JvmTarget>();
		for (ApplicationModuleDetail applicationModuleDetail : modTarget) {
			for (Object targetN: applicationModuleDetail.getTargetList()){
				if (targetN instanceof JvmTarget){
					logger.info("Found JVM Target "+ targetN.toString());
					returnresult.add((JvmTarget) targetN);
				} else {
					logger.info("Found Cluster Object"+ targetN.toString());
					HashSet<JvmTarget> jvmL = cluster.getJVMforCluster(((ClusterTarget) targetN).getClusterName());
					for (JvmTarget jvmTarget : jvmL) {
						returnresult.add(jvmTarget);
					}
				}
			}
		}
		return returnresult;
	}


	/**
	 * Extracts the Performance statistics of a Java EE Application Web Module.
	 * @param jvmNm - String representing Jvm Name
	 * @param nodeNm - String representing Node Name
	 * @param moduleNm - String representing Application Module Name
	 * @return ModulePerformance object containing the Performance statistics for a Application Module
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public ModulePerformance getModulePMI(String jvmNm, String nodeNm, String moduleNm) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord getModulePMI : Parameter Application Module Name "+ moduleNm +" JVM "+ jvmNm +" Node Name "+ nodeNm +".");
		ModulePerformance modPMI=new ModulePerformance(moduleNm, jvmNm, nodeNm, 0, 0, 0, 0);
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
				String[] signature = new String[] { "javax.management.ObjectName", "java.lang.Boolean" };
				Object[] params = new Object[] { srvObj, Boolean.TRUE };
				WSStats wsStats = (WSStats) client.invoke(perf, "getStatsObject", params, signature);
				for (WSStats modStatus :  wsStats.getStats("webAppModule").getStats(moduleNm).getSubStats()) {
					if (modStatus != null){
						logger.debug("Recieved Module for PMI: "+ modStatus);
						modPMI.setTotHits((long) ((WSCountStatistic) modStatus.getStatistic("webAppModule.servlets.totalRequests")).getCount());
						modPMI.setAvgResponse((long) ((WSAverageStatistic) modStatus.getStatistic("webAppModule.servlets.responseTime")).getMean());
						modPMI.setMaxResponse((long) ((WSAverageStatistic) modStatus.getStatistic("webAppModule.servlets.responseTime")).getMax());
						modPMI.setMinResponce((long) ((WSAverageStatistic) modStatus.getStatistic("webAppModule.servlets.responseTime")).getMin());
						logger.debug("Module PMI: "+ modPMI +"\n=============================================");
						if (modStatus.getSubStats() != null) {
							for (WSStats servStats : modStatus.getSubStats()) {
								String uriNm = servStats.getName();
								long totH = ((WSCountStatistic) servStats.getStatistic("webAppModule.servlets.totalRequests")).getCount();
								long avg = (long) ((WSAverageStatistic) servStats.getStatistic("webAppModule.servlets.responseTime")).getMean();
								long mn = (long) ((WSAverageStatistic) servStats.getStatistic("webAppModule.servlets.responseTime")).getMin();
								long mx = (long) ((WSAverageStatistic) servStats.getStatistic("webAppModule.servlets.responseTime")).getMax();
								URIPerformance tempUri = new URIPerformance(uriNm, totH, avg, mn, mx);
								logger.debug(tempUri);
								modPMI.getUriPMIList().add(tempUri);
							}
						}
					}
				}
			} else {
				logger.warn("PMI object cannot be retrived. May be PMI disabled.");
			}
		} else {
			logger.warn("JVM Object cannot be retrieved. May be JVM down.");
		}
		return modPMI;
	}


	/**
	 * Extracts Performance of a Java EE Application.
	 * @param appName - String representing Application Name
	 * @return ApplicationPerformance object representing Application performance
	 * @throws AdminException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public ApplicationPerformance getApplicationPMI(String appName) throws AdminException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord getApplicationPMI : Parameter Application Name "+ appName +".");
		ApplicationPerformance appPMI= new ApplicationPerformance();
		ArrayList<ApplicationModuleDetail> modList = listModules(appName);
		for (ApplicationModuleDetail mod : modList) {
			HashSet<JvmTarget> runtimeList = new HashSet<JvmTarget>();
			for (Object targetN: mod.getTargetList()){
				if (targetN instanceof JvmTarget){
					logger.info("Found JVM Target "+ targetN.toString());
					runtimeList.add((JvmTarget) targetN);
				} else {
					logger.info("Found Cluster Object"+ targetN.toString());
					HashSet<JvmTarget> jvmL = cluster.getJVMforCluster(((ClusterTarget) targetN).getClusterName());
					for (JvmTarget jvmTarget : jvmL) {
						runtimeList.add(jvmTarget);
					}
				}
			}
			for (JvmTarget jvmTarget : runtimeList) {
				logger.info("Application Name:"+ mod.getApplicationName()+" Module Name:"+ mod.getModuleName().split("\\+")[0] +" JVM Name:"+ jvmTarget.toString());
				if (mod.getModuleName().indexOf("web.xml") != -1){
					ModulePerformance modPerf = getModulePMI(jvmTarget.getJvmName(), jvmTarget.getNodeName(), appName+"#"+mod.getModuleName().split("\\+")[0]);
					appPMI.getModulePMI().add(modPerf);
				} else {
					logger.warn("Module is not a Web Module. Thus Performance Statistics cannot be mesured.");
				}
			}

		}
		return appPMI;

	}

	/**
	 * Return a XML Output containing Class Loader View
	 * @param moduleName - <String> representing Module Name.
	 * @param jvmName - JVM Name <String>
	 * @param nodeName - Node Name <String>
	 * @return
	 * @throws ConnectorException 
	 * @throws ConfigServiceException 
	 * @throws MalformedObjectNameException 
	 * @throws ReflectionException 
	 * @throws MBeanException 
	 * @throws InstanceNotFoundException 
	 */
	public String dumpClassLoader(String appName, String moduleName, String jvmName, String nodeName) throws ConfigServiceException, ConnectorException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord dumpClassLoader : Parameter Application Module Name "+ moduleName +" JVM "+ jvmName +" Node Name "+ nodeName +".");
		String returnResult = null;
		ObjectName nodenm = ConfigServiceHelper.createObjectName( null, "Node", nodeName );
		ObjectName nodeObj = configService.queryConfigObjects( session, null, nodenm, null )[0];
		ObjectName servernm = ConfigServiceHelper.createObjectName( null, "Server", jvmName );
		ObjectName server = configService.queryConfigObjects( session, nodeObj, servernm, null )[0];
		ObjectName appSrvnm = ConfigServiceHelper.createObjectName( null, "ApplicationServer", null );
		ObjectName appSrv = configService.queryConfigObjects( session, server, appSrvnm, null )[0];
		String jvmclpol=(String) configService.getAttribute(session, appSrv, "applicationClassLoaderPolicy");
		String jvmclmod=(String) configService.getAttribute(session, appSrv, "applicationClassLoadingMode");
		returnResult="\nJVM Class Loading Mode: "+ jvmclmod +"\nJVM ClassLoader Policy: "+ jvmclpol;

		ObjectName appnm = ConfigServiceHelper.createObjectName( null, "Deployment", appName );
		ObjectName appObj = configService.queryConfigObjects( session, null, appnm, null )[0];
		AttributeList depObj = (AttributeList) configService.getAttribute(session, appObj, "deployedObject");
		for (Object mem : depObj) {
			Attribute memObj=(Attribute) mem;
			if (memObj.getName().equals("warClassLoaderPolicy")){
				logger.debug("Application WAR ClassLoader Policy: "+ memObj.getValue());
				returnResult=returnResult +"\nApplication WAR ClassLoader Policy: "+ memObj.getValue();
			}
			if (memObj.getName().equals("classloader")){
				AttributeList memObjProps = (AttributeList) memObj.getValue();
				for (Object props : memObjProps) {
					if (((Attribute)props).getName().equals("mode")){
						logger.debug("Application ClassLoader: "+ ((Attribute)props).getValue());
						returnResult=returnResult +"\nApplication ClassLoader Mode: "+ ((Attribute)props).getValue();
					}
				}
			}
			if (memObj.getName().equals("modules")){
				ArrayList modObjList = (ArrayList) memObj.getValue();
				logger.debug("Got Module: "+ modObjList);
				AttributeList serchObj=null;
				for (Object modl : modObjList) {
					AttributeList modProps = (AttributeList) modl;
					for (Object prop : modProps) {
						String propNm = ((Attribute)prop).getName();
						Object propVal = ((Attribute)prop).getValue();
						logger.debug("Props Name: "+ propNm +" Props Value: "+ propVal);
						if (propNm.equals("uri") && ((String)propVal).equals(moduleName)){
							serchObj = modProps;
						}
					}
				}
				logger.debug("Module Found: "+ serchObj);
				if (serchObj != null){
					for (Object prop : serchObj) {
						if (((Attribute)prop).getName().equals("classloaderMode")) {
							logger.debug("Module ClassLoader Mode: "+ ((Attribute)prop).getValue());
							returnResult=returnResult +"\nModule ClassLoader Mode: "+ ((Attribute)prop).getValue() +"\n";
						}
					}
				} else {
					logger.debug("Module ClassLoader Mode: Undefined");
					returnResult=returnResult +"\nModule ClassLoader Mode: Undefined\n";
				}
			}

		}


		ObjectName queryName = new ObjectName("WebSphere:process="+ jvmName +",node="+ nodeName +",Application="+ appName +",name="+ moduleName +",*");
		Set modObjL = client.queryNames( queryName, null );
		if (!modObjL.isEmpty()){
			ObjectName	modObj = (ObjectName) modObjL.iterator().next();
			logger.info(modObj.getClass() + " : "+ modObj.toString());
			int totDepth = (Integer) client.invoke( modObj, "getClassLoaderDepth", null, null);
			logger.info(totDepth);
			for (int i = 1; i <= totDepth; i++) {
				String signature[] = new String[] { "int", "boolean", "boolean", "boolean"};
				Object[] params = new Object[] { Integer.valueOf(i), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE };
				String loadedClass = (String) client.invoke( modObj, "getClassLoaderInfo", params, signature);
				returnResult = returnResult +"@@@\n"+ loadedClass;
			}
		}
		return returnResult;
	}

	/**
	 * Returns a String Containing NameSpace Dump 
	 * @param jvmName - JVM Name <String>
	 * @param nodeName - Node Name <String>
	 * @return - <String> representing NameSpace dump output
	 * @throws MalformedObjectNameException
	 * @throws ConnectorException 
	 * @throws ReflectionException 
	 * @throws MBeanException 
	 * @throws InstanceNotFoundException 
	 */
	public String dumpNameSpace(String jvmName, String nodeName) throws MalformedObjectNameException, ConnectorException, InstanceNotFoundException, MBeanException, ReflectionException {
		logger.debug("Methord dumpClassLoader : Parameter Application JVM "+ jvmName +" Node Name "+ nodeName +".");
		String returnResult=null;
		ObjectName queryName = new ObjectName("WebSphere:type=NameServer,process="+ jvmName +",node="+ nodeName +",*");
		Set nmSrvObjL = client.queryNames( queryName, null );
		if (!nmSrvObjL.isEmpty()){
			returnResult="";
			logger.info("Found JVM object for JVM "+ jvmName +" Node Name "+ nodeName +". Will initiate Name Space Dump.");
			ObjectName nmSrvObj = (ObjectName) nmSrvObjL.iterator().next();
			String signature[] = { "java.lang.String" };
			String params[] = { "-root server" };
			String[] dumpOp = (String[]) client.invoke( nmSrvObj, "dumpServerNameSpace", params, signature);
			logger.debug("DUMPED VALUE: "+ dumpOp);
			for (String stringL : dumpOp) {
				logger.debug("Line: "+ stringL);
				returnResult=returnResult +"\n"+ stringL;
			}
		}
		logger.debug("OutPut: "+ returnResult);
		return returnResult;
	}

	/**
	 * Redeploy an existing application
	 * @param appPath - String representing the absolute path where the new EAR archive to redeploy exists.
	 * @param appName - String representing Application Name
	 * @return - True in case deployment successful, False otherwise
	 * @throws AdminException 
	 * @throws ConnectorException 
	 * @throws MalformedObjectNameException 
	 * @throws AppDeploymentException 
	 */
	public void redeployApplication(String appName, String appPath) throws AdminException, MalformedObjectNameException, ConnectorException, AppDeploymentException{
		logger.debug("Methord redeployApplication : Parameter Application Name "+ appName +" EAR Path "+ appPath +".");
		logger.info("Redeploying the application "+ appName);
		Hashtable options = new Hashtable();
		options.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		options.put(AppConstants.APPDEPL_CELL, cellName);
		appManager.redeployApplication(appPath, appName, options, null);
		configService.save(session, true);
		logger.info("Completed redeploying the application "+ appName);

	}

	/**
	 * Return a list of Node Name where a Application is deployed
	 * @param appName - String representing Application Name
	 * @return String List of Node Name 
	 * @throws AdminException
	 * @throws ConnectorException
	 */
	public HashSet<String> getNodeList(String appName) throws AdminException, ConnectorException {
		logger.debug("Methord getNodeList : Parameter Application Name "+ appName +".");
		HashSet<String> nodeList = new HashSet<String>();
		HashSet<JvmTarget> jvmL = listRuntime(appName);
		for (JvmTarget jvmTarget : jvmL) {
			nodeList.add(jvmTarget.getNodeName());
		}
		return nodeList;
	}

}
