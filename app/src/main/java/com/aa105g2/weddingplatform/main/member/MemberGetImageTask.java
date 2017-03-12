package com.aa105g2.weddingplatform.main.member;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import com.aa105g2.weddingplatform.R;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MemberGetImageTask extends AsyncTask<Object, Integer, Bitmap> {
    private final static String TAG = "MemberGetImageTask";
    private final static String ACTION = "getImage";
    private final WeakReference<ImageView> imageViewWeakReference;


    public MemberGetImageTask(ImageView imageView) {
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        String url = params[0].toString();           //get Common.URL + "MemberServletForApp"
        String member_no = params[1].toString();        //get place_no , imageSize
        int imageSize = Integer.parseInt(params[2].toString());   //get imageSize

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", ACTION);     //talk servlet I want do getImage action
        jsonObject.addProperty("member_no", member_no);   //talk servlet I want get place_no picture
        jsonObject.addProperty("imageSize", imageSize);

        Bitmap bitmap;
        try {
            bitmap = getRemoteImage(url, jsonObject.toString());  //get bitmap pic from servlet
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        ImageView imageView = imageViewWeakReference.get();
        if (imageView != null) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);   //display the bitmap to imageView
            } else {
                imageView.setImageResource(R.drawable.default_image);
            }
        }
        super.onPostExecute(bitmap);
    }
    //pack Image data request and send it to servlet
    private Bitmap getRemoteImage(String url, String jsonOut) throws IOException {
        Bitmap bitmap = null;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut: " + jsonOut);
        bw.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "jsonInImage: " + bitmap);
        return bitmap;
    }
}
