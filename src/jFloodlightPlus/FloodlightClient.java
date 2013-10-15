package jFloodlightPlus;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FloodlightClient {
	private String controllerIp;
	
	//	constructor
	FloodlightClient(String ip){
		this.controllerIp = ip;
	}
	
	//	---------------------------
	//		API implementation
	//	---------------------------
	
	//	/wm/core/switch/all/<statType>/json
	public JSONObject getAggregateSwitchesStats(String statType) throws MalformedURLException, IOException, RuntimeException, JSONException{
		return new JSONObject(RestUtils.doGet("http://" + controllerIp + ":8080/wm/core/switch/all/" + statType + "/json"));
	}
	
	//	/wm/core/switch/<switchId>/<statType>/json
	public JSONObject getSwitchStats(String switchId, String statType) throws MalformedURLException, JSONException, IOException, RuntimeException{
		return new JSONObject(RestUtils.doGet("http://" + controllerIp + ":8080/wm/core/switch/" + switchId + "/" + statType + "/json"));
	}
	
	//	/wm/core/controller/switches/json
	//	not only DPIDs as written in document, but many informations of switches
	//	"dpid" in a JSONObject information may be the most important
	public JSONArray getSwitchesInformations() throws MalformedURLException, JSONException, IOException, RuntimeException{
		return new JSONArray(RestUtils.doGet("http://" + controllerIp + ":8080/wm/core/controller/switches/json"));
	}
	
	public ArrayList<String> getSwitchesDPIDs() throws MalformedURLException, JSONException, IOException, RuntimeException{
		ArrayList<String> result;
		JSONArray allSwitchInformations;
		
		result = new ArrayList<String>(); 
		allSwitchInformations = getSwitchesInformations();
		
		for(int i=0; i<allSwitchInformations.length(); i++){
			result.add(allSwitchInformations.getJSONObject(i).getString("dpid"));
		}
		
		return result;
	}
	
	//	/wm/core/counter/<counterTitle>/json
	public JSONObject getGlobalTrafficCounters(String counterTitle) throws MalformedURLException, JSONException, IOException, RuntimeException{
		return new JSONObject(RestUtils.doGet("http://" + controllerIp + ":8080/wm/core/counter/" + counterTitle + "/json"));
	}
	
	//	/wm/core/counter/<switchId>/<counterName>/json
	public JSONObject getSwitchTrafficCounters(String switchId, String counterName) throws MalformedURLException, IOException, RuntimeException, JSONException{
		return new JSONObject(RestUtils.doGet("http://" + controllerIp + ":8080/wm/core/counter/" + switchId + "/" + counterName + "/json"));
	}
	
	//	/wm/core/memory/json
	public JSONObject getControllerMemoryUsage() throws MalformedURLException, JSONException, IOException, RuntimeException{
		return new JSONObject(RestUtils.doGet("http://" + controllerIp + ":8080/wm/core/memory/json"));
	}
	
	//	/wm/topology/links/json
	public JSONArray getInterSwitchLinks() throws MalformedURLException, IOException, RuntimeException, JSONException{
		return new JSONArray(RestUtils.doGet("http://" + controllerIp + ":8080/wm/topology/links/json")); 
	}
	
	//	/wm/topology/switchclusters/json
	public JSONObject getSwitchClusters() throws MalformedURLException, IOException, RuntimeException, JSONException{
		return new JSONObject(RestUtils.doGet("http://" + controllerIp + ":8080/wm/topology/switchclusters/json")); 
	}
	
	//	/wm/topology/external-links/json
	public JSONArray getExternalLinks() throws MalformedURLException, IOException, RuntimeException, JSONException{
		return new JSONArray(RestUtils.doGet("http://" + controllerIp + ":8080/wm/topology/external-links/json")); 
	}
	
	//	/wm/topology/links/json
	public JSONArray getDirectTunnelLinks() throws MalformedURLException, IOException, RuntimeException, JSONException{
		return new JSONArray(RestUtils.doGet("http://" + controllerIp + ":8080/wm/topology/links/json")); 
	}
	
	//	/wm/device
	public JSONArray getDevices(String mac, String ipv4, String vlan, String dpid, String port) throws MalformedURLException, IOException, RuntimeException, JSONException{
		HashMap<String, String> paraMap;
		paraMap = new HashMap<String, String>();
		
		//	set paraMap
		paraMap.put("mac", mac);
		paraMap.put("ipv4", ipv4);
		paraMap.put("vlan", vlan);
		paraMap.put("dpid", dpid);
		paraMap.put("port", port);
		
		return getDevices(paraMap);
	}
	
	//	/wm/device
	//	with Map as parameter, base case
	public JSONArray getDevices(Map<String, String> paraMap) throws MalformedURLException, IOException, RuntimeException, JSONException{
		return new JSONArray(RestUtils.doGet("http://" + controllerIp + ":8080/wm/device/", paraMap));
	}
	
	//	/wm/staticflowentrypusher/json
	//	POST method
	public JSONObject addStaticFlow(Map<String, String> paraMap) throws MalformedURLException, IOException, RuntimeException, JSONException{
		String paraString;
		paraString = prepareStaticFlowEntriesParameterString(paraMap);
		
		return new JSONObject(RestUtils.doPost("http://" + controllerIp + ":8080/wm/staticflowentrypusher/json", paraString));
	}
	
	//	/wm/staticflowentrypusher/json
	//	DELETE method
	public JSONObject deleteStaticFlow(String name) throws MalformedURLException, IOException, RuntimeException, JSONException{
		String paraString;
		Map<String, String> paraMap;
		
		paraMap = new HashMap<String, String>();
		paraMap.put("name", name);
		
		paraString = prepareStaticFlowEntriesParameterString(paraMap);
		
		return new JSONObject(RestUtils.doDelete("http://" + controllerIp + ":8080/wm/staticflowentrypusher/json", paraString));
	}
	
	//	/wm/staticflowentrypusher/list/<switch>/json
	public JSONObject getSwitchStaticFlows(String switchId) throws MalformedURLException, IOException, RuntimeException, JSONException{
		return new JSONObject(RestUtils.doGet("http://" + controllerIp + ":8080/wm/staticflowentrypusher/list/" + switchId + "/json")); 
	}
	
	//	/wm/staticflowentrypusher/clear/<switch>/json
	//	controller return: 204 no content with no response entity
	public void clearSwitchStaticFlows(String switchId) throws MalformedURLException, IOException, RuntimeException, JSONException{
		RestUtils.doGet("http://" + controllerIp + ":8080/wm/staticflowentrypusher/clear/" + switchId + "/json"); 
	}
	
	//	/networkService/v1.1/tenants/{tenant}/networks/{network}
	//	base POST method
	//	PUT method seems to be the same as put method in current floodlight version
	//	set networkName as the same as networkId 
	//	if virtual network with same id exists, then update it
	//	Tenant: Currently ignored
	//	current version: 20130522
	public JSONObject createVirtualNetwork(String networkId, String gatewayIp) throws JSONException, ClientProtocolException, IOException{
		String paraString;
		HashMap<String, String> paraMap;
		
		paraMap = new HashMap<String, String>();
		paraMap.put("gateway", gatewayIp);
		paraMap.put("name", networkId);
		
		paraString = prepareVirtualNetworkFilterParameterString("network", paraMap);
		
		return new JSONObject(RestUtils.doPost("http://" + controllerIp + ":8080/networkService/v1.1/tenants/default/networks/" + networkId, paraString));
	}
	
	//	/networkService/v1.1/tenants/{tenant}/networks/{network}
	//	POST method without gatewayIp
	//	PUT method seems to be the same as put method in current floodlight version
	//	set networkName as the same as networkId
	//	if virtual network with same id exists, then update it
	//	Tenant: Currently ignored
	//	current version: 20130522
	public JSONObject createVirtualNetwork(String networkId) throws JSONException, ClientProtocolException, IOException{
		return createVirtualNetwork(networkId, null);
	}
	
	//	/networkService/v1.1/tenants/{tenant}/networks/{network}
	//	base PUT method
	//	same as POST method in current version
	//	set networkName as the same as networkId 
	//	if virtual network with same id exists, then update it
	//	Tenant: Currently ignored
	//	current version: 20130522
	public JSONObject updateVirtualNetwork(String networkId, String gatewayIp) throws JSONException, ClientProtocolException, IOException{
		String paraString;
		HashMap<String, String> paraMap;
		
		paraMap = new HashMap<String, String>();
		paraMap.put("gateway", gatewayIp);
		paraMap.put("name", networkId);
		
		paraString = prepareVirtualNetworkFilterParameterString("network", paraMap);
		
		return new JSONObject(RestUtils.doPut("http://" + controllerIp + ":8080/networkService/v1.1/tenants/default/networks/" + networkId, paraString));
	}
	
	//	/networkService/v1.1/tenants/{tenant}/networks/{network}
	//	PUT method without gatewayIp
	//	same as POST method in current version
	//	set networkName as the same as networkId
	//	if virtual network with same id exists, then update it
	//	Tenant: Currently ignored
	//	current version: 20130522
	public JSONObject updateVirtualNetwork(String networkId) throws JSONException, ClientProtocolException, IOException{
		return updateVirtualNetwork(networkId, null);
	}
	
	//	/networkService/v1.1/tenants/{tenant}/networks/{network}
	//	DELETE method with network id in URI
	//	Tenant: Currently ignored
	//	current version: 20130522
	public JSONObject deleteVirtualNetwork(String networkId) throws ClientProtocolException, JSONException, IOException{
		return new JSONObject(RestUtils.doDelete("http://" + controllerIp + ":8080/networkService/v1.1/tenants/default/networks/" + networkId));
	}
	
	//	/networkService/v1.1/tenants/{tenant}/networks/{network}/ports/{port}/attachment
	//	PUT method
	//	only logical port is important for host to detach in current version
	//	so please use different port each time
	//	Tenant: Currently ignored
	//	current version: 20130522
	public JSONObject attachHostToVirtualNetwork(String networkId, int logicalPort, String hostMac) throws JSONException, ClientProtocolException, IOException{
		String paraString;
		HashMap<String, String> paraMap;
		
		paraMap = new HashMap<String, String>();
		paraMap.put("id", networkId);
		paraMap.put("mac", hostMac);
		
		paraString = prepareVirtualNetworkFilterParameterString("attachment", paraMap);
		
		return new JSONObject(RestUtils.doPut("http://" + controllerIp + ":8080/networkService/v1.1/tenants/default/networks/" + networkId + "/ports/" + logicalPort + "/attachment", paraString));
	}
	
	//	/networkService/v1.1/tenants/{tenant}/networks/{network}/ports/{port}/attachment
	//	DELETE method
	//	only logical port is important for host to detach in current version
	//	Tenant: Currently ignored
	//	current version: 20130522
	public JSONObject detachHostFromVirtualNetwork(String networkId, int logicalPort) throws JSONException, ClientProtocolException, IOException{
		return new JSONObject(RestUtils.doDelete("http://" + controllerIp + ":8080/networkService/v1.1/tenants/default/networks/" + networkId + "/ports/" + logicalPort + "/attachment"));
	}
	
	//	/networkService/v1.1/tenants/{tenant}/networks
	//	Tenant: Currently ignored
	//	current version: 20130522
	public JSONArray getAllVirtualNetworks() throws MalformedURLException, IOException, RuntimeException, JSONException{
		return new JSONArray(RestUtils.doGet("http://" + controllerIp + ":8080/networkService/v1.1/tenants/default/networks"));
	}
	
	//	-----------------------
	//		helper methods
	//	-----------------------
	
	private String prepareStaticFlowEntriesParameterString(Map<String, String> paraMap){
		JSONObject result;
		result = new JSONObject(paraMap);
		
		return result.toString();
	}

	//	type: network or attachment
	private String prepareVirtualNetworkFilterParameterString(String type, Map<String, String> paraMap) throws JSONException{
		JSONObject result;
		
		result = new JSONObject();
		result.put(type, paraMap);
		
		return result.toString();
	}
}
