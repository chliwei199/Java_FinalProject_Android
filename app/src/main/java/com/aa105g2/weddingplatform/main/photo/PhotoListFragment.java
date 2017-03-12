package com.aa105g2.weddingplatform.main.photo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.Common;

import java.util.List;

public class PhotoListFragment extends Fragment {
    private static final String TAG = "PhotoListFragment";
    private SwipeRefreshLayout swipeRefreshLayout_Photo;
    private RecyclerView rvPhotos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_list_fragment, container, false);

        //**************get member_no String ***************************
//         Bundle bundle = getActivity().getIntent().getExtras();     //use fragment to getIntent()
//         String member_no = bundle.getString("member_no");
//        System.out.println("member_no12346:"+member_no);

        swipeRefreshLayout_Photo =
                (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout_Photo);
        swipeRefreshLayout_Photo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout_Photo.setRefreshing(true);
                showAllPhotos();
                swipeRefreshLayout_Photo.setRefreshing(false);
            }
        }); //end of onRefresh() {

        rvPhotos = (RecyclerView) view.findViewById(R.id.rvNews_Photo);
        rvPhotos.setLayoutManager(   new StaggeredGridLayoutManager(
                        3, StaggeredGridLayoutManager.VERTICAL));

        FloatingActionButton btAdd_Photo = (FloatingActionButton) view.findViewById(R.id.btAdd_Photo);
        btAdd_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {      //click icon to add photo
                Fragment fragment = new PhotInsertFragment();   // and bring groups_no , member_no

                Bundle bundle_FromGroupsPhoto =  getArguments();                                //get bundle from fragment
                String groups_no = bundle_FromGroupsPhoto.getString("groups_no");

                Bundle bundle = new Bundle();
                bundle.putString("groups_no",groups_no);
                bundle.putString("member_no", bundle.getString("member_no"));
                fragment.setArguments(bundle);
                switchFragment(fragment);

//                Intent insertIntent = new Intent(getActivity(), PhotInsertFragment.class);
//                startActivity(insertIntent);
            }
        });  //end of btAdd_Photo.setOnClickListener
        return view;
    }  //end of View onCreateView(

    private void showAllPhotos() {
        if (Common.networkConnected(getActivity())) {
         Bundle bundle_Activity = getActivity().getIntent().getExtras();     //get bundle from Activity
         String member_no = bundle_Activity.getString("member_no");

         Bundle bundle =  getArguments();                                //get bundle from fragment
         String groups_no = bundle.getString("groups_no");
//            System.out.println("member_no from MainActivity:"+member_no);
//             System.out.println("groups_no from GroupsPhotoListFragment:"+groups_no);

            int imageSize = 250;
            String url = Common.URL + "PhotoServletForApp";
            List<PhotoVO> photos = null;
            try {
                photos = new PhotoGetAllTask().execute(url,member_no,groups_no).get();
//                System.out.println("photos from PhotoGetAllTask():"+photos);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (photos == null || photos.isEmpty()) {
//
                Toast.makeText(getActivity(), R.string.msg_NoPhotosFound, Toast.LENGTH_LONG).show();
            } else {
                rvPhotos.setAdapter(new PhotosRecyclerViewAdapter(getActivity(),photos));  //XXXX  unfinish
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        showAllPhotos();
    }
    private class PhotosRecyclerViewAdapter extends RecyclerView.Adapter<PhotosRecyclerViewAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<PhotoVO> photos;

        public PhotosRecyclerViewAdapter(Context context, List<PhotoVO> photos) {
            layoutInflater = LayoutInflater.from(context);
            this.photos = photos;    //have to set list photo to itself
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.photo_recyclerview_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
            final PhotoVO photo = photos.get(position);
            String url = Common.URL + "PhotoServletForApp";

            String photo_no = photo.getPhoto_no();  //according to memberNo to find photo in DB
//            System.out.println("onBindViewHolder:"+photo_no);
            int imageSize = 250;
            new PhotoGetImageTask(myViewHolder.ivImage_Photo).execute(url, photo_no, imageSize);

            myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), view, Gravity.END);
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
//                                case R.id.update:
//                                    Intent updateIntent = new Intent(getActivity(),
//                                            SpotUpdateActivity.class);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putSerializable("place", place);
//                                    updateIntent.putExtras(bundle);
//                                    startActivity(updateIntent);
//                                    break;
                                case R.id.delete:
                                    if (Common.networkConnected(getActivity())) {
                                        String url = Common.URL + "PhotoServletForApp";
                                        String action = "photoDelete";
                                        int count = 0;
                                        try {
                                            count = new PhotoUpdateTask().execute(url, action, photo, null).get();
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(getActivity(), R.string.msg_DeleteFail);
                                        } else {
                                            photos.remove(photo);
                                            PhotosRecyclerViewAdapter.this.notifyDataSetChanged();
                                            Common.showToast(getActivity(), R.string.msg_DeleteSuccess);
                                        }
                                    } else {
                                        Common.showToast(getActivity(), R.string.msg_NoNetwork);
                                    }
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage_Photo;

            public MyViewHolder(View itemView) {
                super(itemView);
                ivImage_Photo = (ImageView) itemView.findViewById(R.id.ivImage_Photo);
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
