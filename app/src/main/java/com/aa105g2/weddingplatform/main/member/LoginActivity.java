package com.aa105g2.weddingplatform.main.member;

import static com.aa105g2.weddingplatform.main.Common.FILE_NAME;

import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.Common;
import com.aa105g2.weddingplatform.main.MainActivity;
import com.aa105g2.weddingplatform.main.photo.PhotoListFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.concurrent.ExecutionException;
public class LoginActivity extends Activity  {
    private final static String DEFAULT_MEMBER_ACC ="aaaaa11111";
    private final static String DEFAULT_MEMBER_PW ="abcde12345";
    private final static String TAG = "LoginActivity";
    private final static String ACTION = "getAll";
    private EditText etUserName;
    private EditText etPassword;
    private CheckBox cbSaved;
    private Button btLogin;
    private String member_acc;
    private String member_pw;
    private ProgressDialog progressDialog;
    private PhotoListFragment PhotoListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
//        loadPreferences();
        findViews();

    }
    @Override
    public void onStart() {
        super.onStart();
        loadPreferences();
    }
    private void loadPreferences() {
        SharedPreferences preferences =
                getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String member_acc = preferences.getString("member_acc", DEFAULT_MEMBER_ACC);
        String member_pw = preferences.getString("member_pw",DEFAULT_MEMBER_PW);

        etUserName.setText(member_acc);
        etPassword.setText(member_pw);
    }
    private void findViews() {
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        cbSaved = (CheckBox) findViewById(R.id.cbSaved);
        btLogin = (Button) findViewById(R.id.btLogin);

        btLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                member_acc = etUserName.getText().toString().trim();
                member_pw = etPassword.getText().toString().trim();

                if (member_acc.length() <= 0 || member_pw.length() <= 0) {
//                    System.out.println("member_acc.length() <= 0 ");
                    Toast.makeText(LoginActivity.this, R.string.msg_errorUserOrPassword, Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                if (cbSaved.isChecked()) {                                      //for check box to save account & password
                    pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                    pref.edit().putBoolean("saved", true)
                            .putString("member_acc", member_acc)
                            .putString("member_pw", member_pw).apply();
                } else {
                    pref.edit().clear().putBoolean("saved", false).apply();
                }
                if (Common.networkConnected(LoginActivity.this)) {
                    String url = Common.URL + "MemberServletForApp";               // annotation: @WebServlet("/MemberServlet") in the eclipse
                    try {
//                      member_name = new MemberGetAllTask().execute(url, member_acc, member_pw).get().replace("\"", "");    //get the places' description & add .replace("\"", "") to remove ""
                        JSONObject  memberVO = new MemberGetAllTask().execute(url, member_acc, member_pw).get();
//                        System.out.println("JSONObject memberVO:"+memberVO);
//                        System.out.println("memberVO.getString(\"member_name\"):"+memberVO.getString("member_name"));
//                        System.out.println("memberVO.getString(\"member_no\"):"+memberVO.getString("member_no"));

                        if ((memberVO.getString("member_name").equals("None"))) {
//                            System.out.println("memberVOnull12313:"+memberVO.isNull(memberVO.getString("member_name")));
                            Toast.makeText(LoginActivity.this, R.string.msg_errorUserOrPassword, Toast.LENGTH_LONG).show();
                        }
//                        else if (memberVO.getString("member_name").equals("None"))
//                        {
//                            Toast.makeText(LoginActivity.this, R.string.msg_errorNoAccount, Toast.LENGTH_LONG).show();
//                        }
                        else {
                            Bundle bundle = new Bundle();                     //send login member_name to MainActivity
//                            bundle.putSerializable("memberVO", (Serializable) memberVO);
                            bundle.putString("member_name", memberVO.getString("member_name"));//send member_name to MainAcivity picture
                            bundle.putString("member_no", memberVO.getString("member_no"));  //send String to Fragment photo
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        }
                    }catch(Exception e){  Log.e(TAG, e.toString());
                        }
                    }
                else {    Common.showToast(new Activity(), R.string.msg_NoNetwork);
                      }
                } //end of  if (Common.networkConnected(new Activity()))

            });// end of setOnClickListener
        }
    }