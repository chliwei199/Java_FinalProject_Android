package com.aa105g2.weddingplatform.main.place;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.Common;
import com.aa105g2.weddingplatform.main.calendar.CalendarViewFragment;
import com.aa105g2.weddingplatform.main.menu.MenuFragment;
import com.aa105g2.weddingplatform.main.menu.MenuGetAllImageTask;
import com.aa105g2.weddingplatform.main.menu.MenuVO;

import java.util.List;

public class PlaceListDetailFragment extends Fragment {
    private static final String TAG = "PlaceListDetailFragment";
    ImageView ivPlace,ivPlaceMenu;
    TextView tvPlaceNote;
    Button btReserve;
    private ViewPager vpPlaceMenu;
    LinearLayout mLinearLayout;

    MyPageIndicator mIndicator;
    int imageSize = 1024;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.place_list_detail_fragment, container, false);
        ivPlace = (ImageView) view.findViewById(R.id.ivPlace);
        vpPlaceMenu = (ViewPager) view.findViewById(R.id.rvPlaceMenu);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.pagesContainer);

        tvPlaceNote = (TextView) view.findViewById(R.id.tvPlaceNote);
        btReserve = (Button) view.findViewById(R.id.btReserve);
        btReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {      //click icon to add photo
                Bundle bundle_FromPlaceListFragment =  getArguments();                                //get bundle from fragment
                String place_no = bundle_FromPlaceListFragment.getString("place_no");
                String sup_no = bundle_FromPlaceListFragment.getString("sup_no");
//                String place_note = bundle_FromPlaceListFragment.getString("place_note");
                String member_name = bundle_FromPlaceListFragment.getString("member_name");
                String member_no = bundle_FromPlaceListFragment.getString("member_no");
                String place_name = bundle_FromPlaceListFragment.getString("place_name");

                Fragment fragment = new CalendarViewFragment();   // and bring groups_no , member_no
                Bundle bundle = new Bundle();                     //send bundle to next fragment
                bundle.putString("place_no",place_no);
                bundle.putString("sup_no",sup_no);
//                bundle.putString("place_note",place_note);
                bundle.putString("member_no",member_no);
                bundle.putString("member_name",member_name);
                bundle.putString("place_name",place_name);
                fragment.setArguments(bundle);
                switchFragment(fragment);
            }
        });  //end of btAdd_Photo.setOnClickListener
        showPlaceDetail();

        return view;
    }
    private void showPlaceDetail() {
        Bundle bundle =  getArguments();                                //get bundle from fragment
        String place_no = bundle.getString("place_no");
        String sup_no = bundle.getString("sup_no");
        String place_note = bundle.getString("place_note");
//        String member_name = bundle.getString("member_name");
//        String member_no = bundle.getString("member_no");
//System.out.println("showPlaceDetail:"+place_no+"--"+sup_no+"--"+place_note+"--"+member_name+"--"+member_no);

        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "PlaceServletForApp";
            String urlMenu = Common.URL + "MenuServletForApp";
            List<MenuVO> menus = null;
            try {
//                menus = new MenuGetAllTask().execute(urlMenu).get(); //only get information
                menus = new MenuGetAllImageTask().execute(urlMenu,place_no).get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (menus == null || menus.isEmpty()) {
                Common.showToast(getActivity(), R.string.msg_NoMenusFound);
            } else {
                new PlaceGetImageTask(ivPlace).execute(url, place_no, imageSize);
                tvPlaceNote.setText(place_note);
                vpPlaceMenu.setAdapter(new MenuAdapter(getActivity().getSupportFragmentManager(), menus));

                mIndicator = new MyPageIndicator(getActivity(), mLinearLayout, vpPlaceMenu, R.drawable.indicator_circle);
                mIndicator.setPageCount(menus.size());
                mIndicator.show();
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }
    private class MenuAdapter extends FragmentStatePagerAdapter {
        List<MenuVO> menuList;

        private MenuAdapter(FragmentManager fm, List<MenuVO> menuList) {
            super(fm);
            this.menuList = menuList;
        }

        @Override
        public int getCount() {
            return menuList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return MenuFragment.newInstance(menuList.get(position));
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        showPlaceDetail();
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
