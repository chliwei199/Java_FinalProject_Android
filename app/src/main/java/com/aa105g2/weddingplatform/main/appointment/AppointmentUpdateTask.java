package com.aa105g2.weddingplatform.main.appointment;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
public class AppointmentUpdateTask extends AsyncTask<Object, Integer, Integer> {
    private final static String TAG = "AppointmentUpdateTask";
    private final static String action = "AppointmentInsert";
    @Override
    protected Integer doInBackground(Object... params) {
        String url = params[0].toString();
        String place_no = params[1].toString();
        String member_no = params[2].toString();
        String date_current = params[3].toString();
        String app_place_date = params[4].toString();

        String result;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", action);
        jsonObject.addProperty("place_no", place_no);
        jsonObject.addProperty("member_no", member_no);
        jsonObject.addProperty("date_current", date_current);
        jsonObject.addProperty("app_place_date", app_place_date);
//        if (params[3] != null) {
//            String imageBase64 = params[3].toString();
//            jsonObject.addProperty("imageBase64", imageBase64);
//        }
        try {
            result = getRemoteData(url, jsonObject.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }

        return Integer.parseInt(result);
    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        StringBuilder sb = new StringBuilder();
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
                sb.append(line);
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "jsonIn: " + sb);
        return sb.toString();
    }
}