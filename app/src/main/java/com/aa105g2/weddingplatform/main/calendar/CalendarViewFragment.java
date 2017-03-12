package com.aa105g2.weddingplatform.main.calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.os.Handler;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.Common;
import com.aa105g2.weddingplatform.main.appointment.AppointmentFragment;
import com.aa105g2.weddingplatform.main.appointment.AppointmentGetTask;
import com.aa105g2.weddingplatform.main.appointment.AppointmentVO;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalendarViewFragment extends Fragment {
    private final static String TAG = "AppointmentGetTask";
    private static final String KEY_PLACE_NO = "place_no";

    private static final String KEY_FORBIDDENDATE = "forbiddenDate";
    private static final String KEY_ARRAY = "array";
    private static final String KEY_INDEXOFARRAY = "indexOfArray";

    List<AppointmentVO> appointments = null;
    public GregorianCalendar month, itemmonth;// calendar instances.
    Button btReserveCalendar;

    public CalendarAdapter adapter;// adapter instance
    public Handler handler;// for grabbing some event values for showing the dot
    // marker.
    public ArrayList<String> items; // container to store calendar items which
    // needs showing the event marker
    String selectedGridDate;

    //Reserve WebSocket open
    private MyWebSocketClient myWebSocketClient;
    JSONArray arrayFirst=null;
    JSONArray arraySecond=null;
    int time=0;
    int indexOfArray=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        String SERVER_URI = "ws://10.0.2.2:8081/AA105G2_20170115_ChatWithAppOK/MyEchoServerCal";  //put uri in there because app need send member_no when it open connection
//        String SERVER_URI = "ws://114.33.129.153:8081/AA105G2_20170115_ChatWithAppOK/MyEchoServerCal";
        String SERVER_URI = "ws://54.218.248.151:8081/AA105G2/MyEchoServerCal";
//        String SERVER_URI = "ws://10.120.26.3:8081/AA105G2/MyEchoServerCal";
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.calendar, container, false);


        URI uri = null;
        try {
            uri = new URI(SERVER_URI);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        myWebSocketClient = new MyWebSocketClient(uri);
        myWebSocketClient.connect();


        Locale.setDefault( Locale.US );
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        itemmonth = (GregorianCalendar) month.clone();

        items = new ArrayList<String>();
        adapter = new CalendarAdapter(getActivity(), month);

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        handler = new Handler();
        handler.post(calendarUpdater);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        btReserveCalendar = (Button) view.findViewById(R.id.btReserveCalendar);
        btReserveCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           Bundle bundle_PlaceListDetailFragment = getArguments();                        //keep the user in calendarView wait onMessage
           String member_no = bundle_PlaceListDetailFragment.getString("member_no");
           String member_name = bundle_PlaceListDetailFragment.getString("member_name");
           String place_no = bundle_PlaceListDetailFragment.getString("place_no");
           String sup_no = bundle_PlaceListDetailFragment.getString("sup_no");
           String place_name = bundle_PlaceListDetailFragment.getString("place_name");
//                System.out.println("CalendarViewFragment:"+place_no+"--"+sup_no+"--"+member_name+"--"+member_no+"--"+place_name);
// ****************get String **************
           Fragment fragment = new AppointmentFragment();
           Bundle bundle = new Bundle();                     //send bundle to next fragment
           bundle.putString("member_no", member_no);
           bundle.putString("member_name", member_name);
           bundle.putString("place_no", place_no);
           bundle.putString("place_name", place_name);
           bundle.putString("sup_no", sup_no);
           bundle.putString("app_place_date", selectedGridDate);
           fragment.setArguments(bundle);
           switchFragment(fragment);
            }
        });  //end of btReserveCalendar.setOnClickListener

        RelativeLayout previous = (RelativeLayout) view.findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar(getView());
            }
        });

        RelativeLayout next = (RelativeLayout) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar(getView());

            }
        });
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                boolean ReservedDate =false;

                ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                selectedGridDate = CalendarAdapter.dayString
                        .get(position);
                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*",
                        "");// taking last part of date. ie; 2 from 2012-12-02.
                int gridvalue = Integer.parseInt(gridvalueString);
                // navigate to next or previous month on clicking offdays.
                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar(getView());
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar(getView());
                }
//                System.out.println(" gridview.setOnItemClickListener+separatedTime:"+selectedGridDate);
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String   date_current = df.format(Calendar.getInstance().getTime()); //get current time
                java.sql.Date  sqlDateNow = java.sql.Date.valueOf(date_current);

                java.sql.Date  selectedGridDateUserCoose = java.sql.Date.valueOf(selectedGridDate);
                for(AppointmentVO appointment : appointments) {                    //check is it the date reserved
                    if(String.valueOf(appointment.getApp_place_date()).equals(selectedGridDate)){
                        ReservedDate=true;
//                 items.add(String.valueOf(appointment.getApp_place_date()));
                  }
                }
               if(selectedGridDateUserCoose.compareTo(sqlDateNow)<0){               // avoid the user to click the day before today.
                   showToast(getResources().getString(R.string.text_DateFilter));
               }
               else if(selectedGridDateUserCoose.compareTo(sqlDateNow)>=0){
              if(ReservedDate){
                  showToast(getResources().getString(R.string.text_BeReserved));
              }
               else {                                          //if user choose the day and the place can be reserve so switch to appointment page
                  Bundle bundle_PlaceListDetailFragment = getArguments();                        //keep the user in calendarView wait onMessage
                  String place_no = bundle_PlaceListDetailFragment.getString("place_no");
                 showToast(selectedGridDate);
                  Map<String, Object> map = new LinkedHashMap<>();
                  if(time==0){

//                      indexOfArray=arrayFirst.length();

                 map.put(KEY_PLACE_NO, new Integer(place_no));
                 map.put(KEY_FORBIDDENDATE, selectedGridDate);
                      try {
                          map.put(KEY_ARRAY, new JSONArray(new String[]{selectedGridDate}));
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }
                      map.put(KEY_INDEXOFARRAY, -1);            //map.put(message, "XXXX");
                     if (myWebSocketClient != null) {
myWebSocketClient.send(new JSONObject(map).toString());
                    }
//                      System.out.println("myWebSocketClient1:"+new JSONObject(map).toString());
                 }
                else{
                      map.put(KEY_PLACE_NO, new Integer(place_no));
                      map.put(KEY_FORBIDDENDATE, selectedGridDate);
                      try {
                          map.put(KEY_ARRAY, new JSONArray(new String[]{""}));
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }
                      map.put(KEY_INDEXOFARRAY, 0);            //map.put(message, "XXXX");
                      if (myWebSocketClient != null) {
                          myWebSocketClient.send(new JSONObject(map).toString());
                      System.out.println("indexOfArray:"+indexOfArray);
                      }
                  }
                  time++;
//                System.out.println(" time:"+time);
              } //end of else
            } //end of if(selectedGridDateUserCoose.compareTo(sqlDateNow)<0)
          } //end of onItemClick method
        });  //****************Send String **************

        return view;
    }//end of onCreateView
        @Override
    public void onStart() {
        super.onStart();
            Bundle bundle =  getArguments();                      //get bundle from PlaceListDetailFragment
            String place_no = bundle.getString("place_no");

            if (Common.networkConnected(getActivity())) {
                String url = Common.URL + "AppointmentServletForApp";
//                List<AppointmentVO> appointments = null;
                try {
                    appointments = new AppointmentGetTask().execute(url,place_no).get();
//                    System.out.println("CalendarViewFragment_appointmentVO:"+appointments);
// for(AppointmentVO appointment : appointments) {
//     System.out.println("appointments is: " + appointment.getApp_place_date()); }

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
    protected void showToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }
    public void refreshCalendar(View view) {
        TextView title = (TextView) view.findViewById(R.id.title);

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some calendar items

        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
    }
    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month.getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + 1);
        }
    }

    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
    }

    public Runnable calendarUpdater = new Runnable() {
//        Bundle bundle =  getArguments();                      //get bundle from PlaceListDetailFragment
//        String place_no = bundle.getString("place_no");
//
        @Override
        public void run() {
            items.clear();

            // Print dates of the current week
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
            String itemvalue;
            itemvalue = df.format(itemmonth.getTime());
            itemmonth.add(GregorianCalendar.DATE, 1);

             for(AppointmentVO appointment : appointments) {
                 items.add(String.valueOf(appointment.getApp_place_date()));
//                 System.out.println("calendarUpdater:"+String.valueOf(appointment.getApp_place_date()));
             }
//            items.add("2016-12-01");
//            items.add("2016-12-02");

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    };

    //for WebSocket calendar
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
                         JSONObject jsonObject = new JSONObject(message);   //ArraySecond= save user choose date in the second time
                        System.out.println("arrayFirst:" + arrayFirst);
                        System.out.println(jsonObject.get(KEY_ARRAY).equals( new JSONArray(new String[]{selectedGridDate})));
                        if(jsonObject.get(KEY_ARRAY).equals( new JSONArray(new String[]{selectedGridDate}))){
                            arrayFirst = (JSONArray) jsonObject.get(KEY_ARRAY);
                            indexOfArray=arrayFirst.length();
//                            System.out.println("TEST:");  //for others' user date
                        }else {
                             items.clear();

                            for(AppointmentVO appointment : appointments) {
                                items.add(String.valueOf(appointment.getApp_place_date()));
                            }
                                 arrayFirst = (JSONArray) jsonObject.get(KEY_ARRAY);
//                            System.out.println("arrayFirst.length():" + arrayFirst.length());
////                            System.out.println("arrayFirst1" + String.valueOf(arrayFirst.getString(1)));
                            for(int i=0;i<arrayFirst.length();i++) {
//                                System.out.println("arrayFirst" + String.valueOf(arrayFirst.getString(i)));
                                if(!arrayFirst.getString(i).equals(new JSONArray(new String[]{selectedGridDate}))) {
                                    items.add(String.valueOf(arrayFirst.getString(i)));   //calendar can add date because in first time
                                }
                            }
                            adapter.setItems(items);
                            adapter.notifyDataSetChanged();

                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }//end of catch(JSONException e)

                } //end of  public void run()


            }); //end of   getActivity().runOnUiThread(new Runnable()


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
//            showToast(R.string.text_LeftChatRoom);
        }
        Log.e("DEBUG", "OnPause of loginFragment");
        super.onPause();
    }
    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void showToast(int messageId) {
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_SHORT).show();
    }
}//end of CalendarViewFragment


