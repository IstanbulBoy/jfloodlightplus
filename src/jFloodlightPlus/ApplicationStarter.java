package jFloodlightPlus;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApplicationStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FloodlightClient fc = new FloodlightClient("127.0.0.1");
		
		try{
			JSONObject result;
			JSONArray virtualNetworksInformations;
			
			result = fc.createVirtualNetwork("virtualNetworkId1");
			System.out.println(result.toString());
			
			result = fc.attachHostToVirtualNetwork("virtualNetworkId1", 1, "00:00:00:00:00:01");
			System.out.println(result.toString());
			
			result = fc.attachHostToVirtualNetwork("virtualNetworkId1", 2, "00:00:00:00:00:02");
			System.out.println(result.toString());
			
			virtualNetworksInformations = fc.getAllVirtualNetworks();
			System.out.println(virtualNetworksInformations.toString());
		}
		catch(Exception ex){
			System.err.println(ex.toString());
		}
	}
}
