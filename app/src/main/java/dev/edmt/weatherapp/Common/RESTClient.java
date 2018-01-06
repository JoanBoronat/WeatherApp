package dev.edmt.weatherapp.Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RESTClient {
    static String stream = null;

    public RESTClient() {
    }

    public String getHTTPData(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            if(httpURLConnection.getResponseCode() == 200) // OK - 200
            {
                BufferedReader r = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = r.readLine())!=null)
                    sb.append(line);
                stream = sb.toString();
                httpURLConnection.disconnect();
            } else {
                return "error";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  stream;
    }
}
