package jFloodlightPlus;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

public class ApplicationStarter {
    /**
     * Program starter, you can remove it if you want.
     * 
     * @param args
     * @throws JSONException
     * @throws RuntimeException
     * @throws IOException
     * @throws MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException, IOException,
            RuntimeException, JSONException {
        // init the client
        FloodlightClient fc = new FloodlightClient();

        // do something
        System.out.println(fc.getAggregateSwitchesStats("flow"));
    }
}
