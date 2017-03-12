package com.aa105g2.weddingplatform.main.appointment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.Common;
import com.aa105g2.weddingplatform.main.home.HomeFragment;
import com.aa105g2.weddingplatform.main.shortmessage.*;
import com.aa105g2.weddingplatform.main.supplier.SupGetAllTask;
import com.aa105g2.weddingplatform.main.supplier.SupVO;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.ContentValues.TAG;


public class AppointmentFragment extends Fragment {
    String member_no =null;
    String member_name =null;
    String place_no =null;
    String place_name =null;
    String sup_no =null;
    String app_place_date =null;
    String sup_name =null;
    String sup_tel =null;
    java.sql.Date  sqlDate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.appointment_fragment, container, false);

        TextView  tvMemberName = (TextView) view.findViewById(R.id.tvMemberName);  //get
        TextView  tvAppointmentPlaceDate = (TextView) view.findViewById(R.id.tvAppointmentPlaceDate);  //get
        TextView   tvPlaceNo = (TextView) view.findViewById(R.id.tvPlaceNo);        //get
        TextView   tvPlaceName = (TextView) view.findViewById(R.id.tvPlaceName);    //get
        TextView   tvSupplierName = (TextView) view.findViewById(R.id.tvSupplierName); //get
        TextView   tvSupplierTel = (TextView) view.findViewById(R.id.tvSupplierTel);  //get
        Button  btCommit = (Button) view.findViewById(R.id.btCommit);
        Button  btCancel = (Button) view.findViewById(R.id.btCancel);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {           //click btCancel to popup Alert
                AppointmentAlertDFragment AppointmentAlertDFragment = new AppointmentAlertDFragment();   // Show Alert DialogFragment
                AppointmentAlertDFragment.show(getActivity().getSupportFragmentManager(), "Alert Dialog Fragment");
            }
        });  //end of btCancel.setOnClickListener

        btCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {           //click btCommit to send appointment
                if (Common.networkConnected( getActivity())) {

                    String url = Common.URL + "AppointmentServletForApp";

                    int count = 0;
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String   date_current = df.format(Calendar.getInstance().getTime()); //get current time
                         sqlDate = java.sql.Date.valueOf(date_current);
                        count = new AppointmentUpdateTask().execute(url,place_no,member_no,sqlDate,app_place_date).get();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(getActivity(), R.string.msg_ReserveFail);
                    } else {
                        Common.showToast(getActivity(), R.string.msg_ReserveSuccess);
                        try {
                            Bundle bundle_CalendarViewFragment =  getArguments();
                            member_no = bundle_CalendarViewFragment.getString("member_no");
                            member_name = bundle_CalendarViewFragment.getString("member_name");
                            place_no = bundle_CalendarViewFragment.getString("place_no");
                            place_name = bundle_CalendarViewFragment.getString("place_name");
                            sup_no = bundle_CalendarViewFragment.getString("sup_no");
                            app_place_date = bundle_CalendarViewFragment.getString("app_place_date");
            new SendMessageTask().execute(("0"+sup_tel).toString(),member_no,member_name,place_no,place_name,sqlDate,app_place_date).get();
                        }catch (Exception e) {                    //this line to send the SMS             //預約日期，宴客日期
                            Log.e(TAG, e.toString());
                        }
                    }
                } else {
                    Common.showToast( getActivity(), R.string.msg_NoNetwork);
                }
                Fragment fragment = new HomeFragment();
                switchFragment(fragment);
                getActivity().setTitle(R.string.text_Home);
            }
        });  //end of btCommit.setOnClickListener

        Bundle bundle_CalendarViewFragment =  getArguments();                                //get bundle from fragment
         member_no = bundle_CalendarViewFragment.getString("member_no");
         member_name = bundle_CalendarViewFragment.getString("member_name");
         place_no = bundle_CalendarViewFragment.getString("place_no");
         place_name = bundle_CalendarViewFragment.getString("place_name");
         sup_no = bundle_CalendarViewFragment.getString("sup_no");
         app_place_date = bundle_CalendarViewFragment.getString("app_place_date");
      System.out.println("AppointmentFragment:"+place_no+"--"+sup_no+"--"+member_name+"--"+member_no+"--"+place_name);

// ****************get String **************
        if (Common.networkConnected(getActivity())){
            String url = Common.URL + "SupServletFroApp";               // annotation: @WebServlet("/MemberServlet") in the eclipse
            try {

                JSONObject supVO = new SupGetAllTask().execute(url,sup_no).get();

                if ((supVO.equals(JSONObject.NULL))) {  //compare is it null
                    Toast.makeText(getActivity(), R.string.msg_NoSuppliersFound, Toast.LENGTH_SHORT).show();
                }
                else {
                     sup_name =  supVO.getString("sup_name");
                     sup_tel = supVO.getString("sup_tax");    //******** read supplier from  Tax
                }
            }catch(Exception e){  Log.e(TAG, e.toString());
            }
        }
        else {    Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        String   date_current = df.format(Calendar.getInstance().getTime()); //get current time
//        java.sql.Date  sqlDate = java.sql.Date.valueOf(date_current);
        tvMemberName.setText("會員姓名:　"+member_name);
        tvAppointmentPlaceDate.setText("宴客日期:　"+app_place_date);
        tvPlaceNo.setText("場地編號:　"+place_no);
        tvPlaceName.setText("場地名稱:　"+place_name);
        tvSupplierName.setText("廠商名稱:　"+sup_name);
        tvSupplierTel.setText("廠商電話:　"+("0"+sup_tel).toString());

        return view;
    }//end of onCreateView

    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
} //end of AppointmentFragment
