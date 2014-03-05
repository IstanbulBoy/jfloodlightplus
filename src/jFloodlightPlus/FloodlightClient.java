package jFloodlightPlus;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author floodlight
 * 
 */
public class FloodlightClient {
    private static final String ETHER_TYPE_IPV4 = "0x0800";
    private static final String ETHER_TYPE_ARP = "0x0806";

    private static final String LOCALHOST = "127.0.0.1";

    private String controllerIp;
    private String uriPrefix;

    // ------------
    // constructors
    // ------------

    /**
     * Default constructor with ip=localhost
     */
    public FloodlightClient() {
        this(LOCALHOST);
    }

    /**
     * Base constructor with ip parameter
     * 
     * @param ip
     *            The controller ip address
     */
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
    public List<String> getAllSwitchDPIDs() throws MalformedURLException,
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

    /**
     * Current controller memory usage.
     * 
     * @return Current controller memory usage
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    public JSONObject getControllerMemoryUsage() throws MalformedURLException,
            JSONException, IOException, RuntimeException {
        String mountPoint = "/wm/core/memory/json";
        return new JSONObject(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * Status/Health of REST API <br>
     * Wrapped in a more intuitive format in isRestApiHealthy()
     * 
     * @return JSONObject of Status/Health of REST API
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    private JSONObject getRestApiHealthStatus() throws MalformedURLException,
            JSONException, IOException, RuntimeException {
        String mountPoint = "/wm/core/health/json";
        return new JSONObject(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * Primitive boolean value of Status/Health of REST API
     * 
     * @return Primitive boolean value of Status/Health of REST API
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    public boolean isRestApiHealthy() throws MalformedURLException, JSONException,
            IOException, RuntimeException {
        return getRestApiHealthStatus().getBoolean("healthy");
    }

    /**
     * Controller uptime <br>
     * Wrapped in a more intuitive format in getSystemUptimeMsec()
     * 
     * @return JSONObject of Controller uptime
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    private JSONObject getSystemUptime() throws MalformedURLException, JSONException,
            IOException, RuntimeException {
        String mountPoint = "/wm/core/system/uptime/json";
        return new JSONObject(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * Primitive long value in Msec of Controller uptime
     * 
     * @return Primitive long value in Msec of Controller uptime
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    public long getSystemUptimeMsec() throws MalformedURLException, JSONException,
            IOException, RuntimeException {
        return getSystemUptime().getLong("systemUptimeMsec");
    }

    /**
     * List all the inter-switch links. <br>
     * Note that these are only for switches connected to the same controller. <br>
     * This is not available in the 0.8 release.
     * 
     * @return all the inter-switch links
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONArray getInterSwitchLinks() throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        String mountPoint = "/wm/topology/links/json";
        return new JSONArray(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * List of all switch clusters connected to the controller. <br>
     * This is not available in the 0.8 release.
     * 
     * @return all switch clusters connected to the controller
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONObject getSwitchClusters() throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        String mountPoint = "/wm/topology/switchclusters/json";
        return new JSONObject(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * Show "external" links <br>
     * i.e., multi-hop links discovered by BDDP instead of LLDP packets
     * 
     * @return "external" links (multi-hop links discovered by BDDP)
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONArray getExternalLinks() throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        String mountPoint = "/wm/topology/external-links/json";
        return new JSONArray(RestUtils.doGet(uriPrefix + mountPoint));
    }

    // FIXME: check mount point periodly for correctness
    /**
     * Show DIRECT and TUNNEL links discovered based on LLDP packets <br>
     * Same mount point as getInterSwitchLinks() in REST API doc webpage
     * 
     * @return DIRECT and TUNNEL links discovered based on LLDP packets
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONArray getDirectAndTunnelLinks() throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        String mountPoint = "/wm/topology/links/json";
        return new JSONArray(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * Provides a route between srcPort on src and dstPort on dst. <br>
     * Not list in REST API page, but appears in CircuitPusher.py
     * 
     * @param srcId
     *            src Switch DPID
     * @param srcPort
     * @param dstId
     *            dst Switch DPID
     * @param dstPort
     * 
     * @return a route between srcPort on srcSwitch and dstPort on dstSwitch
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    public JSONArray getRoute(String srcId, int srcPort, String dstId, int dstPort)
            throws MalformedURLException, JSONException, IOException,
            RuntimeException {
        String mountPoint = "/wm/topology/route/" + srcId + "/" + srcPort + "/" + dstId
                + "/" + dstPort + "/json";
        return new JSONArray(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * List of all devices (i.e. hosts, etc) tracked by the controller. <br>
     * This includes MACs, IPs, and attachment points.
     * 
     * @param paraMap
     *            Map of parameters to filter the devices <br>
     * <br>
     *            Passed as GET parameters: <br>
     *            mac (colon-separated hex-encoded), <br>
     *            ipv4 (dotted decimal), <br>
     *            vlan, <br>
     *            dpid (attachment point DPID) (colon-separated hex-encoded), <br>
     *            port (the attachment point port). <br>
     * 
     * @return all devices (i.e. hosts, etc) tracked by the controller
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONArray getDevices(Map<String, String> paraMap)
            throws MalformedURLException, IOException, RuntimeException, JSONException {
        String mountPoint = "/wm/device/";
        return new JSONArray(RestUtils.doGet(uriPrefix + mountPoint, paraMap));
    }

    /**
     * List of all devices (i.e. hosts, etc) tracked by the controller. <br>
     * This includes MACs, IPs, and attachment points.<br>
     * <br>
     * Parameters to filter the devices are listed below:<br>
     * <b>mac</b> (colon-separated hex-encoded), <br>
     * <b>ipv4</b> (dotted decimal), <br>
     * <b>vlan</b>, <br>
     * <b>dpid</b> (attachment point DPID) (colon-separated hex-encoded), <br>
     * <b>port</b> (the attachment point port). <br>
     * 
     * @param key
     *            Parameter key to filter the devices<br>
     * 
     * 
     * @param value
     *            Parameter value to filter the devices
     * 
     * @return all devices (i.e. hosts, etc) tracked by the controller
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONArray getDevices(String key, String value) throws MalformedURLException,
            IOException, RuntimeException, JSONException {
        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put(key, value);

        return getDevices(paraMap);
    }

    /**
     * General method to add a static flow entry
     * 
     * @param name
     *            Name of the flow entry, this is the primary key, it MUST be unique
     * @param paraMap
     *            key/value pairs for flow entry, check <a target="_blank" href =
     *            "http://docs.projectfloodlight.org/display/floodlightcontroller/Static+Flow+Pusher+API+%28New%29"
     *            >here</a> for details.
     * 
     * @return if add OK, return JSONObject {"status":"Entry pushed"}
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONObject addFlow(String name, Map<String, String> paraMap)
            throws MalformedURLException, IOException, RuntimeException, JSONException {
        String mountPoint = "/wm/staticflowentrypusher/json";

        // Force user to provide flowName in paras to avoid error
        // If already provided in paraMap, just replace it with the one in para
        paraMap.put("name", name);

        return new JSONObject(RestUtils.doPost(uriPrefix + mountPoint,
                toJSONString(paraMap)));
    }

    /**
     * Simple method to add static IPv4 flow entry
     * 
     * @param name
     *            Name of the flow entry, this is the primary key, it MUST be unique
     * @param switchId
     *            ID of the switch (data path) that this rule should be added to
     *            xx:xx:xx:xx:xx:xx:xx:xx
     * @param srcIp
     *            xx.xx.xx.xx
     * @param dstIp
     *            xx.xx.xx.xx
     * @param outputPort
     *            port number to output
     * 
     * @return if add OK, return JSONObject {"status":"Entry pushed"}
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONObject addIPv4Flow(String name, String switchId, String srcIp,
            String dstIp, int outputPort) throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        Map<String, String> paraMap;
        paraMap = new TreeMap<String, String>();

        paraMap.put("switch", switchId);
        paraMap.put("ether-type", ETHER_TYPE_IPV4);
        paraMap.put("src-ip", srcIp);
        paraMap.put("dst-ip", dstIp);
        paraMap.put("actions", "output=" + outputPort);

        return addFlow(name, paraMap);
    }

    /**
     * Push circuit between two hosts according to their IPs, <br>
     * using the getRoute method to get the route from controller. <br>
     * <br>
     * The name of each flow entries will be generated in the format of
     * circuit_namePrefix_switchId_direction. <br>
     * Direction could be forward or reverse.
     * 
     * @param namePrefix
     *            name of the circuit
     * @param srcIp
     *            xx.xx.xx.xx
     * @param dstIp
     *            xx.xx.xx.xx
     * 
     * @return if add OK, return a JSONArray, which contains many JSONObjects
     *         {"status":"Entry pushed"}
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    public JSONArray pushCircuit(String namePrefix, String srcIp, String dstIp)
            throws MalformedURLException, JSONException, IOException, RuntimeException {
        JSONObject srcAp, dstAp;
        JSONArray route, results;

        // init results
        results = new JSONArray();

        // get attachmentPoints of these hosts
        srcAp = getDevices("ipv4", srcIp).getJSONObject(0).getJSONArray(
                "attachmentPoint").getJSONObject(0);
        dstAp = getDevices("ipv4", dstIp).getJSONObject(0).getJSONArray(
                "attachmentPoint").getJSONObject(0);

        // get default route between these attachmentPoints
        route = getRoute(srcAp.getString("switchDPID"), srcAp.getInt("port"),
                dstAp.getString("switchDPID"), dstAp.getInt("port"));

        // add IPv4 flow entries along the path
        for (int i = 0; i < route.length(); i += 2) {
            String flowName, switchId;
            JSONObject result, fnc, rnc;    // nc means nodeConnector in Opendaylight

            // read data from default route
            rnc = route.getJSONObject(i);
            fnc = route.getJSONObject(i + 1);

            // set forward flow entry
            switchId = fnc.getString("switch");
            flowName = "circuit_" + namePrefix + "_" + switchId + "_forward";
            result = addIPv4Flow(flowName, switchId, srcIp, dstIp, fnc.getInt("port"));
            results.put(result);

            // set reverse flow entry
            switchId = rnc.getString("switch");
            flowName = "circuit_" + namePrefix + "_" + switchId + "_reverse";
            result = addIPv4Flow(flowName, switchId, dstIp, srcIp, rnc.getInt("port"));
            results.put(result);
        }

        return results;
    }

    /**
     * Simple method to add static ARP flow entry with flood action
     * 
     * @param switchId
     *            ID of the switch (data path) that this rule should be added to
     *            xx:xx:xx:xx:xx:xx:xx:xx
     * 
     * @return if add OK, return JSONObject {"status":"Entry pushed"}
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONObject addARPFloodFlow(String switchId) throws MalformedURLException,
            IOException, RuntimeException, JSONException {
        Map<String, String> paraMap;
        paraMap = new TreeMap<String, String>();

        paraMap.put("switch", switchId);
        paraMap.put("ether-type", ETHER_TYPE_ARP);
        paraMap.put("actions", "output=flood");

        return addFlow(switchId + "_ARP_flood", paraMap);
    }

    /**
     * Add static ARP flow entries with flood action on all switches
     * 
     * @return an JSONArray of many JSONObject {"status":"Entry pushed"}
     * 
     * @throws MalformedURLException
     * @throws JSONException
     * @throws IOException
     * @throws RuntimeException
     */
    public JSONArray addAllARPFloodFlows() throws MalformedURLException, JSONException,
            IOException, RuntimeException {
        JSONArray results = new JSONArray();
        List<String> switchIds = getAllSwitchDPIDs();

        for (String switchId : switchIds) {
            JSONObject result = addARPFloodFlow(switchId);
            results.put(result);
        }

        return results;
    }

    /**
     * Delete static flow
     * 
     * @param name
     *            flow name to delete
     * 
     * @return if delete OK, return JSONObject {"status":"Entry FLOW_NAME deleted"}
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONObject deleteFlow(String name) throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        Map<String, String> paraMap;
        String mountPoint = "/wm/staticflowentrypusher/json";

        paraMap = new HashMap<String, String>();
        paraMap.put("name", name);

        return new JSONObject(RestUtils.doDelete(uriPrefix + mountPoint,
                toJSONString(paraMap)));
    }

    /**
     * List static flows for a switch or all switches
     * 
     * @param switchId
     *            Valid Switch DPID (XX:XX:XX:XX:XX:XX:XX:XX) or "all"
     * 
     * @return static flow entries on a switch or all switches
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public JSONObject getFlows(String switchId) throws MalformedURLException,
            IOException, RuntimeException, JSONException {
        String mountPoint = "/wm/staticflowentrypusher/list/" + switchId + "/json";
        return new JSONObject(RestUtils.doGet(uriPrefix + mountPoint));
    }

    /**
     * Clear static flows for a switch or all switches
     * 
     * @param switchId
     *            Valid Switch DPID (XX:XX:XX:XX:XX:XX:XX:XX) or "all"
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws RuntimeException
     * @throws JSONException
     */
    public void clearFlows(String switchId) throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        String mountPoint = "/wm/staticflowentrypusher/clear/" + switchId + "/json";
        RestUtils.doGet(uriPrefix + mountPoint);
    }

    // TODO: refactoring below when needed

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

        paraString = toJSONString("network", paraMap);

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

        paraString = toJSONString("network", paraMap);

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

        paraString = toJSONString("attachment", paraMap);

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

    private String toJSONString(Map<String, String> paraMap) {
        return new JSONObject(paraMap).toString();
    }

    // type: network or attachment
    private String toJSONString(String type, Map<String, String> paraMap)
            throws JSONException {
        JSONObject result;

        result = new JSONObject();
        result.put(type, paraMap);

        return result.toString();
    }
}
