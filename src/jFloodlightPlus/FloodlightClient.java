package jFloodlightPlus;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FloodlightClient {
    private static final String LOCALHOST = "127.0.0.1";

    private String controllerIp;
    private String uriPrefix;

    // ------------
    // constructors
    // ------------

    public FloodlightClient() {
        this(LOCALHOST);
    }

    // base constructor
    public FloodlightClient(String ip) {
        this.controllerIp = ip;
        this.uriPrefix = "http://" + controllerIp + ":8080";
    }

    // ------------------
    // API implementation
    // ------------------

    /**
     * Retrieve aggregate stats across all switches.
     * 
     * @param statType
     *            port, queue, flow, aggregate, desc, table, features
     * 
     * @return aggregate stats across all switches
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONObject getAggregateSwitchesStats(String statType)
            throws MalformedURLException, IOException, RuntimeException, JSONException {
        String mountPoint = "/wm/core/switch/all/" + statType + "/json";

        return new JSONObject(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * Retrieve per switch stats.
     * 
     * @param switchId
     *            Valid Switch DPID (XX:XX:XX:XX:XX:XX:XX:XX)
     * @param statType
     *            port, queue, flow, aggregate, desc, table, features
     * 
     * @return per switch stats
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    public JSONObject getSwitchStats(String switchId, String statType)
            throws MalformedURLException, JSONException, IOException, RuntimeException {
        String mountPoint = "/wm/core/switch/" + switchId + "/" + statType + "/json";

        return new JSONObject(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * List of all switch informations connected to the controller. <br>
     * Not only DPIDs as written in document, but many informations of switches. <br>
     * DPID may be the most important.
     * 
     * @return all switch informations
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    public JSONArray getSwitchesInformations() throws MalformedURLException,
            JSONException, IOException, RuntimeException {
        String mountPoint = "/wm/core/controller/switches/json";
        return new JSONArray(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * Get all switch DPIDs of the network, which are mined from all switch
     * informations.
     * 
     * @return all switch DPIDs
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    public List<String> getSwitchesDPIDs() throws MalformedURLException,
            JSONException, IOException, RuntimeException {
        ArrayList<String> result;
        JSONArray allSwitchInformations;

        // get all switch infos
        allSwitchInformations = getSwitchesInformations();

        // data mining
        result = new ArrayList<String>();
        for (int i = 0; i < allSwitchInformations.length(); i++) {
            result.add(allSwitchInformations.getJSONObject(i).getString("dpid"));
        }

        return result;
    }

    /**
     * Controller summary (# of Switches, # of Links, etc). <br>
     * #hosts seems to be odd now (2014/02/18)
     * 
     * @return Controller summary (# of Switches, # of Links, etc)
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONObject getControllerSummary() throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        String mountPoint = "/wm/core/controller/summary/json";
        return new JSONObject(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * List of global traffic counters in the controller (across all switches).
     * 
     * @param counterTitle
     * <br>
     *            "all" or something of the form DPID_COUNTER_NAME_SUB_CATEGORY ie. <br>
     *            00:00:00:00:00:00:00:01_OFPacketIn_broadcast (SUB_CATEGORY
     *            being "broadcast") <br>
     *            00:00:00:00:00:00:00:01_OFPacketIn_L3_ARP (SUB_CATEGORY being
     *            "L3_ARP") <br>
     *            L3 sub_categories take the form "L3_etherType", L4
     *            sub_categories take the form "L4_protocol" <br>
     *            For more details look at
     *            net.floodlightcontroller.counter.CounterStore.java
     * 
     * @return global traffic counters in the controller (across all switches)
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    public JSONObject getGlobalTrafficCounters(String counterTitle)
            throws MalformedURLException, JSONException, IOException, RuntimeException {
        String mountPoint = "/wm/core/counter/" + counterTitle + "/json";
        return new JSONObject(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * List of traffic counters per switch
     * 
     * @param switchId
     *            Valid Switch DPID
     * @param counterName
     * <br>
     *            "all" or something of the form DPID_COUNTER_NAME_SUB_CATEGORY ie. <br>
     *            00:00:00:00:00:00:00:01_OFPacketIn_broadcast (SUB_CATEGORY
     *            being "broadcast") <br>
     *            00:00:00:00:00:00:00:01_OFPacketIn_L3_ARP (SUB_CATEGORY being
     *            "L3_ARP") <br>
     *            L3 sub_categories take the form "L3_etherType", L4
     *            sub_categories take the form "L4_protocol" <br>
     *            For more details look at
     *            net.floodlightcontroller.counter.CounterStore.java
     * 
     * @return traffic counters per switch
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONObject getSwitchTrafficCounters(String switchId, String counterName)
            throws MalformedURLException, IOException, RuntimeException, JSONException {
        String mountPoint = "/wm/core/counter/" + switchId + "/" + counterName + "/json";
        return new JSONObject(RestUtils.doGet(uriPrefix + mountPoint));
    }

    // FIXME: refactor the following methods
    // /wm/core/memory/json
    public JSONObject getControllerMemoryUsage() throws MalformedURLException,
            JSONException, IOException, RuntimeException {
        return new JSONObject(RestUtils.doGet("http://" + controllerIp
                + ":8080/wm/core/memory/json"));
    }

    // /wm/topology/links/json
    public JSONArray getInterSwitchLinks() throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        return new JSONArray(RestUtils.doGet("http://" + controllerIp
                + ":8080/wm/topology/links/json"));
    }

    // /wm/topology/switchclusters/json
    public JSONObject getSwitchClusters() throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        return new JSONObject(RestUtils.doGet("http://" + controllerIp
                + ":8080/wm/topology/switchclusters/json"));
    }

    // /wm/topology/external-links/json
    public JSONArray getExternalLinks() throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        return new JSONArray(RestUtils.doGet("http://" + controllerIp
                + ":8080/wm/topology/external-links/json"));
    }

    // /wm/topology/links/json
    public JSONArray getDirectTunnelLinks() throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        return new JSONArray(RestUtils.doGet("http://" + controllerIp
                + ":8080/wm/topology/links/json"));
    }

    // /wm/topology/route
    // not list in REST API page, but appears in CircuitPusher.py
    public JSONArray getRoute(String srcSwitchDPID, int srcPort, String dstSwitchDPID,
            int dstPort) throws MalformedURLException, JSONException, IOException,
            RuntimeException {
        return new JSONArray(RestUtils.doGet("http://" + controllerIp
                + ":8080/wm/topology/route/" + srcSwitchDPID + "/" + srcPort + "/"
                + dstSwitchDPID + "/" + dstPort + "/json"));
    }

    // /wm/device
    public JSONArray getDevices(String mac, String ipv4, String vlan, String dpid,
            String port) throws MalformedURLException, IOException, RuntimeException,
            JSONException {
        HashMap<String, String> paraMap;
        paraMap = new HashMap<String, String>();

        // set paraMap
        paraMap.put("mac", mac);
        paraMap.put("ipv4", ipv4);
        paraMap.put("vlan", vlan);
        paraMap.put("dpid", dpid);
        paraMap.put("port", port);

        return getDevices(paraMap);
    }

    // /wm/device
    // with Map as parameter, base case
    public JSONArray getDevices(Map<String, String> paraMap)
            throws MalformedURLException, IOException, RuntimeException, JSONException {
        return new JSONArray(RestUtils.doGet("http://" + controllerIp
                + ":8080/wm/device/", paraMap));
    }

    // /wm/staticflowentrypusher/json
    // POST method
    public JSONObject addStaticFlow(Map<String, String> paraMap)
            throws MalformedURLException, IOException, RuntimeException, JSONException {
        String paraString;
        paraString = prepareStaticFlowEntriesParameterString(paraMap);

        return new JSONObject(RestUtils.doPost("http://" + controllerIp
                + ":8080/wm/staticflowentrypusher/json", paraString));
    }

    // /wm/staticflowentrypusher/json
    // DELETE method
    public JSONObject deleteStaticFlow(String name) throws MalformedURLException,
            IOException, RuntimeException, JSONException {
        String paraString;
        Map<String, String> paraMap;

        paraMap = new HashMap<String, String>();
        paraMap.put("name", name);

        paraString = prepareStaticFlowEntriesParameterString(paraMap);

        return new JSONObject(RestUtils.doDelete("http://" + controllerIp
                + ":8080/wm/staticflowentrypusher/json", paraString));
    }

    // /wm/staticflowentrypusher/list/<switch>/json
    public JSONObject getSwitchStaticFlows(String switchId) throws MalformedURLException,
            IOException, RuntimeException, JSONException {
        return new JSONObject(RestUtils.doGet("http://" + controllerIp
                + ":8080/wm/staticflowentrypusher/list/" + switchId + "/json"));
    }

    // /wm/staticflowentrypusher/clear/<switch>/json
    // controller return: 204 no content with no response entity
    public void clearSwitchStaticFlows(String switchId) throws MalformedURLException,
            IOException, RuntimeException, JSONException {
        RestUtils.doGet("http://" + controllerIp
                + ":8080/wm/staticflowentrypusher/clear/" + switchId + "/json");
    }

    // /networkService/v1.1/tenants/{tenant}/networks/{network}
    // base POST method
    // PUT method seems to be the same as put method in current floodlight version
    // set networkName as the same as networkId
    // if virtual network with same id exists, then update it
    // Tenant: Currently ignored
    // current version: 20130522
    public JSONObject createVirtualNetwork(String networkId, String gatewayIp)
            throws JSONException, ClientProtocolException, IOException {
        String paraString;
        HashMap<String, String> paraMap;

        paraMap = new HashMap<String, String>();
        paraMap.put("gateway", gatewayIp);
        paraMap.put("name", networkId);

        paraString = prepareVirtualNetworkFilterParameterString("network", paraMap);

        return new JSONObject(RestUtils.doPost("http://" + controllerIp
                + ":8080/networkService/v1.1/tenants/default/networks/" + networkId,
                paraString));
    }

    // /networkService/v1.1/tenants/{tenant}/networks/{network}
    // POST method without gatewayIp
    // PUT method seems to be the same as put method in current floodlight version
    // set networkName as the same as networkId
    // if virtual network with same id exists, then update it
    // Tenant: Currently ignored
    // current version: 20130522
    public JSONObject createVirtualNetwork(String networkId) throws JSONException,
            ClientProtocolException, IOException {
        return createVirtualNetwork(networkId, null);
    }

    // /networkService/v1.1/tenants/{tenant}/networks/{network}
    // base PUT method
    // same as POST method in current version
    // set networkName as the same as networkId
    // if virtual network with same id exists, then update it
    // Tenant: Currently ignored
    // current version: 20130522
    public JSONObject updateVirtualNetwork(String networkId, String gatewayIp)
            throws JSONException, ClientProtocolException, IOException {
        String paraString;
        HashMap<String, String> paraMap;

        paraMap = new HashMap<String, String>();
        paraMap.put("gateway", gatewayIp);
        paraMap.put("name", networkId);

        paraString = prepareVirtualNetworkFilterParameterString("network", paraMap);

        return new JSONObject(RestUtils.doPut("http://" + controllerIp
                + ":8080/networkService/v1.1/tenants/default/networks/" + networkId,
                paraString));
    }

    // /networkService/v1.1/tenants/{tenant}/networks/{network}
    // PUT method without gatewayIp
    // same as POST method in current version
    // set networkName as the same as networkId
    // if virtual network with same id exists, then update it
    // Tenant: Currently ignored
    // current version: 20130522
    public JSONObject updateVirtualNetwork(String networkId) throws JSONException,
            ClientProtocolException, IOException {
        return updateVirtualNetwork(networkId, null);
    }

    // /networkService/v1.1/tenants/{tenant}/networks/{network}
    // DELETE method with network id in URI
    // Tenant: Currently ignored
    // current version: 20130522
    public JSONObject deleteVirtualNetwork(String networkId)
            throws ClientProtocolException, JSONException, IOException {
        return new JSONObject(RestUtils.doDelete("http://" + controllerIp
                + ":8080/networkService/v1.1/tenants/default/networks/" + networkId));
    }

    // /networkService/v1.1/tenants/{tenant}/networks/{network}/ports/{port}/attachment
    // PUT method
    // only logical port is important for host to detach in current version
    // so please use different port each time
    // Tenant: Currently ignored
    // current version: 20130522
    public JSONObject attachHostToVirtualNetwork(String networkId, int logicalPort,
            String hostMac) throws JSONException, ClientProtocolException, IOException {
        String paraString;
        HashMap<String, String> paraMap;

        paraMap = new HashMap<String, String>();
        paraMap.put("id", networkId);
        paraMap.put("mac", hostMac);

        paraString = prepareVirtualNetworkFilterParameterString("attachment", paraMap);

        return new JSONObject(RestUtils.doPut("http://" + controllerIp
                + ":8080/networkService/v1.1/tenants/default/networks/" + networkId
                + "/ports/" + logicalPort + "/attachment", paraString));
    }

    // /networkService/v1.1/tenants/{tenant}/networks/{network}/ports/{port}/attachment
    // DELETE method
    // only logical port is important for host to detach in current version
    // Tenant: Currently ignored
    // current version: 20130522
    public JSONObject detachHostFromVirtualNetwork(String networkId, int logicalPort)
            throws JSONException, ClientProtocolException, IOException {
        return new JSONObject(RestUtils.doDelete("http://" + controllerIp
                + ":8080/networkService/v1.1/tenants/default/networks/" + networkId
                + "/ports/" + logicalPort + "/attachment"));
    }

    // /networkService/v1.1/tenants/{tenant}/networks
    // Tenant: Currently ignored
    // current version: 20130522
    public JSONArray getAllVirtualNetworks() throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        return new JSONArray(RestUtils.doGet("http://" + controllerIp
                + ":8080/networkService/v1.1/tenants/default/networks"));
    }

    // -----------------------
    // helper methods
    // -----------------------

    private String prepareStaticFlowEntriesParameterString(Map<String, String> paraMap) {
        JSONObject result;
        result = new JSONObject(paraMap);

        return result.toString();
    }

    // type: network or attachment
    private String prepareVirtualNetworkFilterParameterString(String type,
            Map<String, String> paraMap) throws JSONException {
        JSONObject result;

        result = new JSONObject();
        result.put(type, paraMap);

        return result.toString();
    }
}
