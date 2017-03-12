package com.aa105g2.weddingplatform.main.groups;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.Common;
import com.aa105g2.weddingplatform.main.photo.PhotoListFragment;

import java.util.List;

public class GroupsPhotoListFragment extends Fragment {
    private static final String TAG = "GroupsPhotoListFragment";
    private SwipeRefreshLayout swipeRefreshLayout_GroupsPhoto;
    private RecyclerView rvGroupsPhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_photo_list_fragment, container, false);

        //**************get member_no String ***************************
//         Bundle bundle = getActivity().getIntent().getExtras();     //use fragment to getIntent()
//         String member_no = bundle.getString("member_no");
//        System.out.println("member_no12346:"+member_no);

        swipeRefreshLayout_GroupsPhoto =
                (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout_GroupsPhoto);
        swipeRefreshLayout_GroupsPhoto.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout_GroupsPhoto.setRefreshing(true);
                showAllGroups();
                swipeRefreshLayout_GroupsPhoto.setRefreshing(false);
            }
        }); //end of onRefresh() {

        rvGroupsPhoto = (RecyclerView) view.findViewById(R.id.rvNews_GroupsPhoto);
        rvGroupsPhoto.setLayoutManager(   new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL));
        return view;
    }  //end of View onCreateView(

    private void showAllGroups() {
        if (Common.networkConnected(getActivity())) {
            Bundle bundle = getActivity().getIntent().getExtras();     //use fragment to getIntent()
            String member_no = bundle.getString("member_no");
//            System.out.println("member_no from PhotoListFragment:"+member_no);

            int imageSize = 250;
            String url = Common.URL + "GroupsServletForApp";
            List<GroupsVO> groupsVO = null;
            try {
                groupsVO = new GroupsGetAllTask().execute(url,member_no).get();
//                 new PhotoGetImageTask().execute(url,member_no,imageSize).get();
//                System.out.println("photos from GroupsGetAllTask():"+groupsVO);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (groupsVO == null || groupsVO.isEmpty()) {
//
                Toast.makeText(getActivity(), R.string.msg_NoGroups, Toast.LENGTH_LONG).show();
            } else {
                rvGroupsPhoto.setAdapter(new GroupsPhotoRecyclerViewAdapter(getActivity(),groupsVO));  //XXXX  unfinish

            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        showAllGroups();
    }
    private class GroupsPhotoRecyclerViewAdapter extends RecyclerView.Adapter<GroupsPhotoRecyclerViewAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<GroupsVO> groups;

        public GroupsPhotoRecyclerViewAdapter(Context context, List<GroupsVO> groups) {
            layoutInflater = LayoutInflater.from(context);
            this.groups = groups;    //have to set list photo to itself
        }

        @Override
        public int getItemCount() {
            return groups.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.groups_photo_list_recyclerview_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
            final GroupsVO group = groups.get(position);
//            String url = Common.URL + "PhotoServletForApp";

            String groups_no = group.getGroups_no();
            String groups_title = group.getGroups_title();    //according to memberNo to find photo in DB


            myViewHolder.ivImage_GroupsPhoto.setImageResource(R.drawable.ic_group_logo_480);
            myViewHolder.tvNo_GroupsPhoto.setText(group.getGroups_no());
            myViewHolder.tvTitle_GroupsPhoto.setText(group.getGroups_title());

           myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {    //click icon will change page to PhotoListFragment
                    Fragment fragment = new PhotoListFragment();   // and bring groups_no , member_no
                    Bundle bundle = new Bundle();
                    bundle.putString("groups_no",group.getGroups_no());
                    bundle.putString("member_no", bundle.getString("member_no"));
                    fragment.setArguments(bundle);
                    switchFragment(fragment);
                }
            });
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage_GroupsPhoto;
            TextView tvNo_GroupsPhoto,tvTitle_GroupsPhoto;

            public MyViewHolder(View itemView) {
                super(itemView);
                ivImage_GroupsPhoto = (ImageView) itemView.findViewById(R.id.ivImage_GroupsPhoto);
                tvNo_GroupsPhoto = (TextView) itemView.findViewById(R.id.tvNo_GroupsPhoto);
                tvTitle_GroupsPhoto = (TextView) itemView.findViewById(R.id.tvTitle_GroupsPhoto);
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
