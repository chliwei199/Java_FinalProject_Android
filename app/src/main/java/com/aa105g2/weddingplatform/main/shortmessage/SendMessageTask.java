package com.aa105g2.weddingplatform.main.shortmessage;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class SendMessageTask extends AsyncTask<Object, Integer, Void> {
    private final static String TAG = "SendMessageTask";
//    "0"+sup_tel,member_no,member_name,place_no,place_name,sqlDate,app_place_date

    @Override
    protected Void doInBackground(Object... params) {
        String[] strArray = new String[] {(String) params[0]};
//        String[] SupplierTel = (String[]) params[0];
        String member_no = params[1].toString();
        String member_name = params[2].toString();
        String place_no = params[3].toString();
        String place_name = params[4].toString();
        String sqlDate = params[5].toString();
        String app_place_date = params[6].toString();
      try {
            StringBuffer buf_FromAPP = new StringBuffer();
          buf_FromAPP.append("會員:"+member_no+","+member_name+","+"場地編號:"+place_no+","+"預約場地:"+place_name+","+"預約日期:"+sqlDate+","+"宴客日期:"+app_place_date).toString();
        String buf_ToSMS=buf_FromAPP.toString();
            Send se = new Send();
               se.sendMessage(strArray , buf_ToSMS);
//Log.d("SendMessageTask",strArray.toString()+"-"+member_no+"-"+member_name+"-"+place_no+"-"+place_name+"-"+sqlDate+"-"+app_place_date);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }

        return null;
    }
}

