package com.aa105g2.weddingplatform.main;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Common {
    //    public static String URL = "http://192.168.196.189:8080/Spot_MySQL_Web/";
    public static String URL = "http://54.218.248.151:8081/AA105G2/";
//    public static String URL = "http://10.120.26.3:8081/AA105G2/";
//    public static String URL = "http://192.168.196.132:8081/AA105G2_20170116_ChatWithAppOK/";
//    public static String URL = "http://114.33.129.153:8081/AA105G2_20170115_ChatWithAppOK/";

    //模擬器連Tomcat

    //偏好設定檔案名稱
    public final static String FILE_NAME = "preference";
    // check if the device connect to the network
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
