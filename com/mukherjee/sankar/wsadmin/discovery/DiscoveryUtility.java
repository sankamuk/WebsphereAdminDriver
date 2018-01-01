/**
 * Discovery tool for discovering a Websphere Cell. It generates a Config object representing a Websphere Cell with Application List and Node List.
 * @author Sankar Mukherjee
 * @version 01, 31 March, 2015
 */

package com.mukherjee.sankar.wsadmin.discovery;

import java.util.ArrayList;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.ibm.websphere.management.configservice.ConfigDataId;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.mukherjee.sankar.wsadmin.ApplicationOperations;
import com.mukherjee.sankar.wsadmin.ClusterOperations;
import com.mukherjee.sankar.wsadmin.JvmOperations;
import com.mukherjee.sankar.wsadmin.WsadminClient;
import com.mukherjee.sankar.wsadmindata.ApplicationModuleDetail;
import com.mukherjee.sankar.wsadmindata.ClusterTarget;
import com.mukherjee.sankar.wsadmindata.JvmTarget;

public class DiscoveryUtility {

	private static Logger logger = Logger.getLogger(DiscoveryUtility.class.getName());

	/**
	 * Discover Cell Structure and returns CellConfig object.
	 * @param client - <AdminClient> object having a connection to a running Websphere Cell Manager.
	 * @param ip - <String> representing Cell Manager IP
	 * @param port - int representing Cell Manager port
	 * @param usrnm - <String> representing Cell Manager User Id
	 * @param passwd - <String> representing Cell Manager User Password
	 * @return  <CellConfig> representing Cell Configuration
	 * @throws AdminException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 */
	public static CellConfig dicoverCell(WsadminClient client, String ip, int port, String usrnm, String passwd) throws AdminException, ConnectorException, MalformedObjectNameException {
		logger.debug("Methord dicoverCell : Parameter Client for Cell "+ client.getCellName());
		CellConfig returnResult = new CellConfig(client.getCellName());
		ApplicationOperations appOps = client.getAppV();
		ClusterOperations clusterOps = client.getClusterV();
		JvmOperations jvmOps = client.getJvmV();

		// Extraction of Application List
		ArrayList<String> appList=appOps.getApplicationList();
		for (String app : appList) {
			logger.debug("Starting to add Configuration for Application "+ app);
			ApplicationConfig appConf = new ApplicationConfig(app);
			returnResult.getAppList().add(appConf);
		}
		logger.info("Completed adding Application list.");
		
		
		// Extraction Of DMGR Configuration
		for (String nodeNm : jvmOps.returnNodes()) {
			if(jvmOps.returnJvms(nodeNm).contains("dmgr")){
				logger.info("Detected DMGR on Node "+ nodeNm +".");
				DMGRConfig dmgrProcss = new DMGRConfig(ip, port, usrnm, passwd);
				NodeConfig statNd=null;
				for (NodeConfig	nd : returnResult.getNodeList()) {
					if (nd.getNodeName().equals(nodeNm)){
						statNd=nd;
					}
				}
				if (statNd == null){
					logger.debug("Detected new Node "+ nodeNm +" adding the same to Cell NodeList and then add the DMGR Node.");
					NodeConfig nd = new NodeConfig(nodeNm);
					returnResult.getNodeList().add(nd);
					dmgrProcss.setDmgrNode(nd);
				} else {
					dmgrProcss.setDmgrNode(statNd);
				}
				returnResult.getDmgrList().add(dmgrProcss);
			} else {
				NodeConfig statNd=null;
				for (NodeConfig	nd : returnResult.getNodeList()) {
					if (nd.getNodeName().equals(nodeNm)){
						statNd=nd;
					}
				}
				if (statNd == null){
					logger.debug("Detected new Node "+ nodeNm +" adding the same to Cell NodeList and then add the DMGR Node.");
					NodeConfig nd = new NodeConfig(nodeNm);
					returnResult.getNodeList().add(nd);
				} 
			}
		}
		logger.info("Completed adding DMGR list.");

		// Extracting Host Name for Nodes
		for (NodeConfig ndConf : returnResult.getNodeList()) {
			logger.info("Detected Host "+ ndConf.getNodeName() +" in Node List.");
			ObjectName nodenm = ConfigServiceHelper.createObjectName( null, "Node", ndConf.getNodeName() );
			ObjectName nodeObj = client.getConfigService().queryConfigObjects( client.getSession(), null, nodenm, null )[0];
			String hstNm = (String) client.getConfigService().getAttribute(client.getSession(), nodeObj, "hostName");
			String washm="";
			String profileNm="";
			ObjectName vmapnm = ConfigServiceHelper.createObjectName( null, "VariableMap", null );
			ObjectName[] vmap = client.getConfigService().queryConfigObjects( client.getSession(), nodeObj, vmapnm, null );
			for (ObjectName objectName : vmap) {
				logger.debug("Variable Map :"+ objectName);
				ArrayList<AttributeList> attributeList=(ArrayList<AttributeList>) client.getConfigService().getAttribute(client.getSession(), objectName, "entries"); 
				for (AttributeList attributeNm : attributeList) {
					Attribute varAttr = (Attribute) attributeNm.get(3);
					ConfigDataId varConf = (ConfigDataId) varAttr.getValue();
					String varSymName = (String)client.getConfigService().getAttribute(client.getSession(), ConfigServiceHelper.createObjectName(varConf), "symbolicName");
					if (varSymName.equals("WAS_INSTALL_ROOT")){
						washm=(String)client.getConfigService().getAttribute(client.getSession(), ConfigServiceHelper.createObjectName(varConf), "value");
					}
					if (varSymName.equals("USER_INSTALL_ROOT")){
						profileNm=(String)client.getConfigService().getAttribute(client.getSession(), ConfigServiceHelper.createObjectName(varConf), "value");
						int pathNum=profileNm.split("\\/").length;
						profileNm=profileNm.split("\\/")[pathNum-1];
					}
				}
			}
			HostConfig hst=null;
			for (HostConfig hostC : returnResult.getHostList()) {
				if(hostC.getHostName().equals(hstNm) && hostC.getWasHome().equals(washm)){
					hst=hostC;
				}
			}
			if (hst == null){
				hst = new HostConfig(hstNm);
				if (!washm.equals("")){
					hst.setWasHome(washm);
				}
				returnResult.getHostList().add(hst);
			}
			ndConf.setNodeHost(hst);
			if (!profileNm.equals("")) {
				ndConf.setProfileName(profileNm);
			}
		}
		logger.info("Completed generating Cell Data.");

		return returnResult;
	}

}
