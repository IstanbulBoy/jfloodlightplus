package jFloodlightPlus;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

public class ApplicationStarter {

    private static final String S1_ID = "00:00:00:00:00:00:00:01";
    private static final String S6_ID = "00:00:00:00:00:00:00:06";

    /**
     * @param args
     * @throws JSONException
     * @throws RuntimeException
     * @throws IOException
     * @throws MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        FloodlightClient fc = new FloodlightClient();

        fc.clearFlows(S1_ID);

        // doing ARP
        // System.out.println(fc.addAllARPFloodFlows());

        // doing IPv4
        // fc.addIPv4Flow("flr", S1_ID, "10.0.0.2", "10.0.0.1", 1);
        // fc.addIPv4Flow("frl", S1_ID, "10.0.0.1", "10.0.0.2", 2);
    }
}
