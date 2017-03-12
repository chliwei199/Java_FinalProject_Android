package com.aa105g2.weddingplatform.main.place;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.Common;

import java.util.List;
import java.util.Random;

public class PlaceListFragment extends Fragment {
    private static final String TAG = "PlaceListFragment";
    private SwipeRefreshLayout swipeRefreshLayout_Place;
    private RecyclerView rvPlaces;
    private String[] placesAreaDB={"N", "C", "S", "E"};
    private String[] placesTypeDB = {"1", "2", "3"};
    private int placeAreaPosition=0;
    private int placeTypePosition=1;
    private int placesSearchChoose=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_list_fragment, container, false);

        swipeRefreshLayout_Place =
                (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout_Place);
        swipeRefreshLayout_Place.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout_Place.setRefreshing(true);
                showAllPlaces();
                swipeRefreshLayout_Place.setRefreshing(false);
            }
        });

        Button btSearch_Place = (Button) view.findViewById(R.id.btSearch_Place);
        btSearch_Place.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view) {
                showAllPlacesAreaType(placesAreaDB[placeAreaPosition], placesTypeDB[placeTypePosition]);
//                if(placesSearchChoose==0){
//                showAllPlacesArea(placesAreaDB[placeAreaPosition]);
////                     System.out.println("placesAreaDB[placeAreaPosition]:"+placeAreaPosition);  //0=Area, 2=type
//            } else{
//                  showAllPlacesType(placesTypeDB[placeTypePosition]);
//                }
            }
        });
        //for spinner AREA setting
        Spinner spArea = (Spinner) view.findViewById(R.id.spArea);
        spArea.setSelection(0, true);
        spArea.setOnItemSelectedListener(listener_PlaceArea);

        Spinner spType = (Spinner) view.findViewById(R.id.spType);
        spType.setSelection(1, true);
        spType.setOnItemSelectedListener(listener_PlaceType);

        rvPlaces = (RecyclerView) view.findViewById(R.id.rvNews_Place);  //get RecyclerView in place_list_fragment
        rvPlaces.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;

    }
    Spinner.OnItemSelectedListener listener_PlaceArea = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(
                AdapterView<?> parent, View view, int position, long id) {
//            String item = parent.getItemAtPosition(position).toString();
             placesAreaDB = new String[]{"N", "C", "S", "E"};
            placeAreaPosition=position;                           //in order to set N,C,S,E  search events
//            showAllPlacesArea(placesAreaDB[position]);
//            Toast.makeText(getActivity(), "Selected: " + placesAreaDB[position], Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
//            tvMessage1.setText("Nothing selected!");
//            Toast.makeText(getActivity(), "Nothing selected!", Toast.LENGTH_SHORT).show();
        }
    };   // end of  listener_PlaceArea
    Spinner.OnItemSelectedListener listener_PlaceType = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(
                AdapterView<?> parent, View view, int position, long id) {
//            String item = parent.getItemAtPosition(position).toString();
            placesTypeDB = new String[]{"1", "2", "3"};
            placeTypePosition=position;
//            System.out.println("placeTypePosition:"+ placesTypeDB[position]);  //0=Area, 2=type
//            Toast.makeText(getActivity(), "Selected " + placesTypeDB[position], Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
//            tvMessage1.setText("Nothing selected!");
            Toast.makeText(getActivity(), "Nothing selected!", Toast.LENGTH_SHORT).show();
        }
    };   // end of  listener_PlaceType

    private void showAllPlaces() {   // it will load places' specification to Adapter when it start
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "PlaceServletForApp";   // annotation: @WebServlet("/PlaceServlet") in the eclipse
            List<PlaceVO> places = null;
            try {
                places = new PlaceGetAllTask().execute(url).get();      //get the places' description
//                System.out.println("places:"+places);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (places == null || places.isEmpty()) {
                Common.showToast(getActivity(), R.string.msg_NoPlacesFound);
            } else {
                rvPlaces.setAdapter(new PlacesRecyclerViewAdapter(getActivity(), places));
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }

    private void showAllPlacesType(String placeType) {   // it will load places' specification to Adapter when it start
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "PlaceServletForApp";   // annotation: @WebServlet("/PlaceServlet") in the eclipse
            List<PlaceVO> places = null;
            try {
                places = new PlaceTypeGetTask().execute(url,placeType).get();      //get the places' description
                System.out.println("showAllPlacesType-placesType:"+places);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (places == null || places.isEmpty()) {
                Common.showToast(getActivity(), R.string.msg_NoPlacesFound);
            } else {
                rvPlaces.setAdapter(new PlacesRecyclerViewAdapter(getActivity(), places));
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }
    private void showAllPlacesArea(String placeArea) {   // it will load places' specification to Adapter when it start
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "PlaceServletForApp";   // annotation: @WebServlet("/PlaceServlet") in the eclipse
            List<PlaceVO> places = null;
            try {
                places = new PlaceAreaGetTask().execute(url,placeArea).get();      //get the places' description
                System.out.println("showAllPlacesArea-places:"+places);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (places == null || places.isEmpty()) {
                Common.showToast(getActivity(), R.string.msg_NoPlacesFound);
            } else {
                rvPlaces.setAdapter(new PlacesRecyclerViewAdapter(getActivity(), places));
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }
    private void showAllPlacesAreaType(String placeArea,String placeType) {   // it will load places' specification to Adapter when it start
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "PlaceServletForApp";   // annotation: @WebServlet("/PlaceServlet") in the eclipse
            List<PlaceVO> places = null;
            try {
                places = new PlaceAreaTypeGetTask().execute(url,placeArea,placeType).get();      //get the places' description
                System.out.println("showAllPlacesArea-places:"+places);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (places == null || places.isEmpty()) {
                Common.showToast(getActivity(), R.string.msg_NoPlacesFound);
            } else {
                rvPlaces.setAdapter(new PlacesRecyclerViewAdapter(getActivity(), places));
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showAllPlaces();
    }

    private class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesRecyclerViewAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<PlaceVO> places;

        public PlacesRecyclerViewAdapter(Context context, List<PlaceVO> places) {
            layoutInflater = LayoutInflater.from(context);
            this.places = places;
        }

        @Override
        public int getItemCount() {
            return places.size();
        }

        @Override         //create the custom view container to put the view
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.place_recyclerview_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
            final PlaceVO place = places.get(position);
            String url = Common.URL + "PlaceServletForApp";    // annotation: @WebServlet("/PlaceServletForApp") in the eclipse

            String place_no = place.getPlace_no();   //get the place's no

            int imageSize = 1024;                         //send Common.URL + "PlaceServletForApp" , place_no , imageSize
            new PlaceGetImageTask(myViewHolder.ivPlace).execute(url, place_no, imageSize);
            myViewHolder.tvPlaceName.setText(place.getPlace_name());
            myViewHolder.tvPlaceAdds.setText(place.getPlace_adds());
            myViewHolder.tvPlaceNo.setText("場地編號: "+place.getPlace_no());
            if(position%2==0){
                myViewHolder.ratingBar_Place.setRating(4);}
            else{
                myViewHolder.ratingBar_Place.setRating((float)4.5);}  //end of RatingBar

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            String place_no = place.getPlace_no();   //get the place's no
            String sup_no = place.getSup_no();      //get the supplier no
            String place_note = place.getPlace_note();  //get the Place_note and send to placeDetail fragment
            String place_name = place.getPlace_name();  //get the Place_note and send to placeDetail fragment

                    Bundle bundle_Main = getActivity().getIntent().getExtras();     //use fragment to getIntent()
                    String member_no = bundle_Main.getString("member_no");
                    String member_name = bundle_Main.getString("member_name");

                    Fragment fragment = new PlaceListDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("member_no", member_no);
                    bundle.putSerializable("member_name", member_name);
                    bundle.putSerializable("place_no", place_no);
                    bundle.putSerializable("sup_no", sup_no);
                    bundle.putSerializable("place_note", place_note);
                    bundle.putSerializable("place_name", place_name);
                    fragment.setArguments(bundle);
                    switchFragment(fragment);
                }
            });
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivPlace;
            TextView tvPlaceName, tvPlaceAdds, tvPlaceNo;
            RatingBar ratingBar_Place;
            public MyViewHolder(View itemView) {
                super(itemView);
                ratingBar_Place = (RatingBar) itemView.findViewById(R.id.ratingBar_Place);
                ivPlace = (ImageView) itemView.findViewById(R.id.ivPlace);
                tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
                tvPlaceAdds = (TextView) itemView.findViewById(R.id.tvPlaceAdds);
                tvPlaceNo = (TextView) itemView.findViewById(R.id.tvPlaceNo);
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
