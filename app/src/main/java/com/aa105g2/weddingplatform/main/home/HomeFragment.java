package com.aa105g2.weddingplatform.main.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.Common;
import com.aa105g2.weddingplatform.main.advertisement.AdvPicFragment;
import com.aa105g2.weddingplatform.main.advertisement.AdvPicGetAllTask;
import com.aa105g2.weddingplatform.main.advertisement.AdvTitleGetAllTask;
import com.aa105g2.weddingplatform.main.advertisement.AdvVO;
import com.aa105g2.weddingplatform.main.place.MyPageIndicator;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class HomeFragment extends Fragment {
    private static final String TAG = "PlaceListFragment";
    private RecyclerView rvAdvsTitle;
    private ViewPager vpAdv;
    LinearLayout mLinearLayout;
    MyPageIndicator mIndicator;
    List<AdvVO> advs = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.home_fragment, container, false);


        vpAdv = (ViewPager) view.findViewById(R.id.vpAdv);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.pagesContainer);


        rvAdvsTitle = (RecyclerView) view.findViewById(R.id.rvAdvsTitle);  //get RecyclerView in place_list_fragment
        rvAdvsTitle.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }
    private void showAllAdvsTitle() {   // it will load places' specification to Adapter when it start
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "AdvServletForApp";   // annotation: @WebServlet("/PlaceServlet") in the eclipse
            try {
                advs = new AdvTitleGetAllTask().execute(url).get();      //get the places' description
//                System.out.println("places:"+places);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (advs == null || advs.isEmpty()) {
                Common.showToast(getActivity(), R.string.msg_NoAdvsFound);
            } else {
                rvAdvsTitle.setAdapter(new AdvsRecyclerViewAdapter(getActivity(), advs));// set Homepage rv AdvTitle
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }
    private void showAllAdvsPicture() {   // it will load places' specification to Adapter when it start
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "AdvServletForApp";   // annotation: @WebServlet("/PlaceServlet") in the eclipse
            List<AdvVO> advsPic = null;
            try {
                advsPic = new AdvPicGetAllTask().execute(url).get();      //get the places' description
                System.out.println("advsPic:"+advsPic.size()+","+advsPic);
//                if(advsPic.get(0)==null){
//                    Resources res=getResources();
//                    Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.default_image);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
//                    byte[] AdvBytes = stream.toByteArray();
//                    advsPic.add(new AdvVO(AdvBytes));
//                    advsPic.add(new AdvVO(AdvBytes));
//                    advsPic.add(new AdvVO(AdvBytes));
//                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (advsPic == null || advsPic.isEmpty()) {
                Common.showToast(getActivity(), R.string.msg_NoAdvsPicFound);
            } else {
                vpAdv.setAdapter(new AdvPicAdapter(getActivity().getSupportFragmentManager(),advsPic));

                mIndicator = new MyPageIndicator(getActivity(), mLinearLayout, vpAdv, R.drawable.indicator_circle);
                mIndicator.setPageCount(advsPic.size());
                mIndicator.show();                  //unfinish    servlet
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }
    private class AdvPicAdapter extends FragmentStatePagerAdapter {
        List<AdvVO> advPicList;

        private AdvPicAdapter(FragmentManager fm, List<AdvVO> advPicList) {
            super(fm);
            this.advPicList = advPicList;
        }

        @Override
        public int getCount() {
            return advPicList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return AdvPicFragment.newInstance(advPicList.get(position));
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        showAllAdvsTitle();
        showAllAdvsPicture();
    }
    private class AdvsRecyclerViewAdapter extends RecyclerView.Adapter<AdvsRecyclerViewAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<AdvVO> advs;

        public AdvsRecyclerViewAdapter(Context context, List<AdvVO> advs) {
            layoutInflater = LayoutInflater.from(context);
            this.advs = advs;
        }

        @Override
        public int getItemCount() {
            return advs.size();
        }

        @Override         //create the custom view container to put the view
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.adv_recyclerview_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
            final AdvVO adv = advs.get(position);
            String url = Common.URL + "AdvServletForApp";    // annotation: @WebServlet("/PlaceServletForApp") in the eclipse

            myViewHolder.tvAdvTitle.setText(adv.getAdv_note());

//            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String place_no = place.getPlace_no();   //get the place's no
//                    String sup_no = place.getSup_no();      //get the supplier no
//                    String place_note = place.getPlace_note();  //get the Place_note and send to placeDetail fragment
//                    String place_name = place.getPlace_name();  //get the Place_note and send to placeDetail fragment
//
//                    Bundle bundle_Main = getActivity().getIntent().getExtras();     //use fragment to getIntent()
//                    String member_no = bundle_Main.getString("member_no");
//                    String member_name = bundle_Main.getString("member_name");
//
//                    Fragment fragment = new PlaceListDetailFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("member_no", member_no);
//                    bundle.putSerializable("member_name", member_name);
//                    bundle.putSerializable("place_no", place_no);
//                    bundle.putSerializable("sup_no", sup_no);
//                    bundle.putSerializable("place_note", place_note);
//                    bundle.putSerializable("place_name", place_name);
//                    fragment.setArguments(bundle);
//                    switchFragment(fragment);
//                }
//            });
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvAdvTitle;
            public MyViewHolder(View itemView) {
                super(itemView);
                tvAdvTitle = (TextView) itemView.findViewById(R.id.tvAdvTitle);
            }
        }
    }
}
