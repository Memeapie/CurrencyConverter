package com.gre.assessment.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ASyncHttpsTask extends android.os.AsyncTask<Void, Void, Void> {

    private String result;
    private String urlRaw;
    private String method;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(urlRaw);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            result = content.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void setRequestHeaders(String url, String method) {
        this.urlRaw = url;
        this.method = method;
    }

    public String getResult(){
        return result;
    }
}
