package com.aa105g2.weddingplatform.main.message;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.aa105g2.weddingplatform.R;
import android.util.Log;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class MessagesFragment extends Fragment {
    private static final String TAG = "MessagesFragment";
    //    private static final String SERVER_URI = "ws://10.0.2.2:8080/WebSocketChat_Web/MyWebSocketServer";
//    private static String SERVER_URI = "ws://10.0.2.2:8081/AA105G2/MessageServletForApp";
//    private static final String USER_NAME = "Android";
    private static  String member_name ;
    private static final String KEY_MEMBER_NAME = "my_name";
    private static  String friend_name ;
    private static final String KEY_FRIEND_NAME = "friend_name";

    private static  String friend_no ;
    private static final String KEY_FRIEND_NO = "friend_no";
    private static  String member_no ;
    private static final String KEY_MEMBER_NO = "member_no";

    private static final String KEY_MESSAGE = "message";
    private MyWebSocketClient myWebSocketClient;
    private TextView tvMessage;
    private EditText etMessage;
    private ScrollView scrollView;
    private Button btSendMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         String SERVER_URI = "ws://10.0.2.2:8081/AA105G2_20170115_ChatWithAppOK/MessageServletForApp";  //put uri in there because app need send member_no when it open connection
//        String SERVER_URI = "ws://114.33.129.153:8081/AA105G2_20170115_ChatWithAppOK/MessageServletForApp";
        String SERVER_URI = "ws://54.218.248.151:8081/AA105G2/MessageServletForApp";
//        String SERVER_URI = "ws://10.120.26.3:8081/AA105G2/MessageServletForApp";
        super.onCreateView(inflater, container, savedInstanceState);
        View  view = inflater.inflate(R.layout.messages_fragment, container, false);

        Bundle bundle_FriendsListFragment =  getArguments();                                //get bundle from fragment
        friend_no = bundle_FriendsListFragment.getString("friend_no");
        friend_name = bundle_FriendsListFragment.getString("friend_name");
        member_no = bundle_FriendsListFragment.getString("member_no");
        member_name = bundle_FriendsListFragment.getString("member_name");
//        Log.d("MyWebSocketClient:",FRIEND_NO+"-"+FRIEND_NAME+"-"+MEMBER_NO+"-"+MEMBER_NAME);

        SERVER_URI=SERVER_URI+"/"+member_no;    //send member_no& friend_no to server get their chat infor
        Log.d("SERVER_URI:",SERVER_URI);

        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        btSendMessage = (Button) view.findViewById(R.id.btSendMessage);

        btSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //click send the message
                String message = etMessage.getText().toString();
                if (message.trim().isEmpty()) {
                    showToast(R.string.text_MessageEmpty);
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put(KEY_MEMBER_NO, member_no);
                map.put(KEY_MEMBER_NAME, member_name);
                map.put(KEY_FRIEND_NO, friend_no);
                map.put(KEY_FRIEND_NAME, friend_name);
                map.put(KEY_MESSAGE, message);  // //map.put(message, "XXXX");
                if (myWebSocketClient != null) {
                    myWebSocketClient.send(new JSONObject(map).toString());

                    etMessage.setText("");
                }
            }
        });

        URI uri = null;
        try {

            uri = new URI(SERVER_URI);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        myWebSocketClient = new MyWebSocketClient(uri);
        myWebSocketClient.connect();

        return view;
    }



    class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverURI) {
            super(serverURI, new Draft_17());
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d(TAG, "onOpen: handshakedata.toString() = " + handshakedata.toString());
        }

        @Override
        public void onMessage(final String message) {
            Log.d(TAG, "onMessage: " + message);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String userName = jsonObject.get(KEY_MEMBER_NAME).toString();
                        String message = jsonObject.get(KEY_MESSAGE).toString();
                        String text = userName + ": " + message + "\n";

                        tvMessage.append(text);
//                        tvMessage.setGravity(Gravity.RIGHT);
                        scrollView.fullScroll(View.FOCUS_DOWN);

                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            String text = String.format(Locale.getDefault(),
                    "code = %d, reason = %s, remote = %b",
                    code, reason, remote);
            Log.d(TAG, "onClose: " + text);
        }

        @Override
        public void onError(Exception ex) {
            Log.d(TAG, "onError: exception = " + ex.toString());
        }
    }


@Override
public void onPause() {
    if (myWebSocketClient != null) {
                myWebSocketClient.close();
                showToast(R.string.text_LeftChatRoom);
            }
    Log.e("DEBUG", "OnPause of loginFragment");
    super.onPause();
}
    private void showToast(int messageId) {
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_SHORT).show();
    }

}
