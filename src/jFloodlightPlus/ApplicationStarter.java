package jFloodlightPlus;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

public class ApplicationStarter {

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

        System.out.println(fc.getSwitchTrafficCounters("00:00:00:00:00:00:00:01", "all"));
    }
}
