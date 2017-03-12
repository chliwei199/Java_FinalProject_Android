package com.aa105g2.weddingplatform.main.appointment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.StaggeredGridLayoutManager;
import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.Common;
import com.aa105g2.weddingplatform.main.home.HomeFragment;

import java.util.List;

import static android.content.ContentValues.TAG;

public class AppointmentListFragment extends Fragment {
    private RecyclerView rv_AppointmentList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.appointment_list_fragment, container, false);

        rv_AppointmentList = (RecyclerView) view.findViewById(R.id.rv_AppointmentList);
        rv_AppointmentList.setLayoutManager( new StaggeredGridLayoutManager(
                1, StaggeredGridLayoutManager.VERTICAL));
        return view;
    }
    private void showAllAppointmentList() {
        if (Common.networkConnected(getActivity())) {
            Bundle bundle = getActivity().getIntent().getExtras();     //use fragment to getIntent()
//            String member_name = bundle.getString("member_name");
            String member_no = bundle.getString("member_no");

//            System.out.println("member_no from PhotoListFragment:"+member_no);

            String url = Common.URL + "AppointmentServletForApp";
            List<AppointmentVO> appointmentListVO = null;
            try {
                appointmentListVO = new AppointmentListGetAllTask().execute(url,member_no).get();
//                System.out.println("appointmentListVO from AppointmentListGetAllTask():"+appointmentListVO);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (appointmentListVO == null || appointmentListVO.isEmpty()) {

                Fragment fragment = new AppointmentNoDataFragment();
                switchFragment(fragment);
                Toast.makeText(getActivity(), R.string.msg_NoAppointmentList, Toast.LENGTH_SHORT).show();
            } else {
                rv_AppointmentList.setAdapter(new AppointmentListRecyclerViewAdapter(getActivity(),appointmentListVO));

            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        showAllAppointmentList();
    }
    private class AppointmentListRecyclerViewAdapter extends RecyclerView.Adapter<AppointmentListRecyclerViewAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<AppointmentVO> appointments;

        public AppointmentListRecyclerViewAdapter(Context context, List<AppointmentVO> appointments) {
            layoutInflater = LayoutInflater.from(context);
            this.appointments = appointments;    //have to set list photo to itself
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.appointment_list_recyclerview_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
            final AppointmentVO appointment = appointments.get(position);
            Bundle bundle = getActivity().getIntent().getExtras();     //use fragment to getIntent()
            String member_name = bundle.getString("member_name");
            final String member_no = bundle.getString("member_no");

            String reserveDate = appointment.getApp_date().toString();
            String appointmentPlaceDate = appointment.getApp_place_date().toString();    //according to memberNo to find photo in DB
//            final String place_no = appointment.getPlace_no();
            final String App_no = appointment.getApp_no();

//            System.out.println("onBindViewHolder groups_title:"+groups_title);

//            myViewHolder.ivImage_GroupsPhoto.setImageResource(R.drawable.ic_group_logo_480);
            myViewHolder.tvMemberName.setText("會員姓名:　"+member_name);
            myViewHolder.tvReserveDate.setText("預約日期:　"+appointment.getApp_date().toString()); //get
            myViewHolder.tvAppointmentPlaceDate.setText("宴客日期:　"+appointment.getApp_place_date().toString());
            myViewHolder.tvPlaceNo.setText("場地編號:　"+appointment.getPlace_no());

            myViewHolder.tvAppointmentNo.setText("預約訂單編號:　"+appointment.getApp_no());//test  tvPlaceName replace by App_no

            myViewHolder.btCancel_AppointmentList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {    //click icon will change page to PhotoListFragment
                    if (Common.networkConnected(getActivity())) {
                    String url = Common.URL + "AppointmentServletForApp";

                    int count = 0;
                    try {
//                        count = new AppointmentCancelTask().execute(url,place_no,member_no).get();
                        count = new AppointmentCancelTask().execute(url,App_no).get();
//                        System.out.println("AppointmentCancelTask()_count:"+count);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(getActivity(), R.string.msg_AppointmentListDeleteFail);
                    }else{
                        Common.showToast(getActivity(), R.string.msg_AppointmentListDeleteOk);
                        Fragment fragment = new HomeFragment();
                        switchFragment(fragment);
                    }

                } else {
                    Common.showToast(getActivity(), R.string.msg_NoNetwork);
                }
                }
            });
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvMemberName,tvReserveDate,tvAppointmentPlaceDate,tvPlaceNo,tvAppointmentNo;
            Button  btCancel_AppointmentList;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvMemberName = (TextView) itemView.findViewById(R.id.tvMemberName);
                tvReserveDate = (TextView) itemView.findViewById(R.id.tvReserveDate);
                tvAppointmentPlaceDate = (TextView) itemView.findViewById(R.id.tvAppointmentPlaceDate);
                tvPlaceNo = (TextView) itemView.findViewById(R.id.tvPlaceNo);
                tvAppointmentNo = (TextView) itemView.findViewById(R.id.tvAppointmentNo);
                btCancel_AppointmentList = (Button) itemView.findViewById(R.id.btCancel_AppointmentList);
            }
        }
    }
    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
