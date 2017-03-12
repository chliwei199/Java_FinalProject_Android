package com.aa105g2.weddingplatform.main.supplier;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class SupGetAllTask extends AsyncTask<Object, Integer, JSONObject> {
    private final static String TAG = "SupplierGetAllTask";
    private final static String ACTION = "SupplierGetAllInfo";


    @Override
    protected JSONObject doInBackground(Object... params) {
        String url = params[0].toString();          //[0]= url = Common.URL + "PlaceServlet"
        String sup_no = params[1].toString();
//        String member_pw = params[2].toString();

        JSONObject jsonIn;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", ACTION);   //talk servlet I want do getAll action
        jsonObject.addProperty("sup_no", sup_no);
//        jsonObject.addProperty("member_pw", member_pw);
        try {
            jsonIn = getRemoteData(url, jsonObject.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return jsonIn;  //use gson api convert jsonIn to List of objects
    }

    private JSONObject getRemoteData(String url, String jsonOut)  throws IOException {   //pack getAll action and send it to servlet
        StringBuilder jsonIn = new StringBuilder();
        JSONObject result = null;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut: " + jsonOut);
        bw.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                jsonIn.append(line);
            }
            if (jsonIn.equals(null) || jsonIn.length() <= 0) {
                return result;
            }
            try {
                result = new JSONObject(String.valueOf(jsonIn));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else { Log.d(TAG, "response code: " + responseCode);     }
        connection.disconnect();
        Log.d(TAG, "jsonIn_SupGetAllTask: " + jsonIn);
//        return jsonIn.toString();
        return result;
    }

}
