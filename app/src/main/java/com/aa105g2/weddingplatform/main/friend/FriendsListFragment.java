package com.aa105g2.weddingplatform.main.friend;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import com.aa105g2.weddingplatform.main.member.MemberVO;
import com.aa105g2.weddingplatform.main.message.MessagesFragment;

import java.util.List;

public class FriendsListFragment extends Fragment {
    private static final String TAG = "FriendListFragment";
    private SwipeRefreshLayout swipeRefreshLayout_FriendList;
    private RecyclerView rvFriendList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_list_fragment, container, false);

        swipeRefreshLayout_FriendList =
                (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout_FriendList);
        swipeRefreshLayout_FriendList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout_FriendList.setRefreshing(true);
                showAllFriends();
                swipeRefreshLayout_FriendList.setRefreshing(false);
            }
        }); //end of onRefresh() {

        rvFriendList = (RecyclerView) view.findViewById(R.id.rvNews_FriendList);
        rvFriendList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }  //end of View onCreateView(

    private void showAllFriends() {
        if (Common.networkConnected(getActivity())) {
            Bundle bundle = getActivity().getIntent().getExtras();     //use fragment to getIntent()
            String member_no = bundle.getString("member_no");
//            System.out.println("member_no from PhotoListFragment:"+member_no);

            String url = Common.URL + "FriendsServletForApp";
            List<FriendVO> friendsVO = null;
            List<MemberVO> memberVO = null;
            try {
                memberVO = new FriendsGetAllTask().execute(url,member_no).get();
//                 new PhotoGetImageTask().execute(url,member_no,imageSize).get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (memberVO == null || memberVO.isEmpty()) {
//
                Toast.makeText(getActivity(), R.string.msg_NoFriends, Toast.LENGTH_LONG).show();
//                Common.showToast(getActivity(), R.string.msg_NoPhotosFound);
            } else {
                rvFriendList.setAdapter(new FrinedsRecyclerViewAdapter(getActivity(),memberVO));  //XXXX  unfinish

            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        showAllFriends();
    }
    private class FrinedsRecyclerViewAdapter extends RecyclerView.Adapter<FrinedsRecyclerViewAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
//        private List<FriendVO> friends;
        private List<MemberVO> members;


        public FrinedsRecyclerViewAdapter(Context context, List<MemberVO> members) {
            layoutInflater = LayoutInflater.from(context);
            this.members = members;    //have to set list photo to itself
        }

        @Override
        public int getItemCount() {
            return members.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.friends_list_recyclerview_item, parent, false);
            return new MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
//            final FriendVO frined = friends.get(position);
            String url = Common.URL + "FriendsServletForApp";
            MemberVO  member = members.get(position);
            String member_no= member.getMember_no();
            Log.d("onBindViewHolder:",member_no);
//         myViewHolder.ivImageFriend.setImageResource(R.drawable.ic_add);
            int imageSize = 200;
            new FriendGetImageTask(myViewHolder.ivImageFriend).execute(url, member_no, imageSize);
            myViewHolder.tvFriendName.setText(member.getMember_name());

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {        //click icon will change page to PhotoListFragment
                    MemberVO  member = members.get(position);      //to get click item friend_no and friend_name
                    String friend_no= member.getMember_no();
                    String friend_name= member.getMember_name();

                    Bundle bundle_FromMain = getActivity().getIntent().getExtras();   //get member_no/name from MainActivity
                    String member_no = bundle_FromMain.getString("member_no");
                    String member_name = bundle_FromMain.getString("member_name");

                    Fragment fragment = new MessagesFragment();   // and bring groups_no , member_no
                    Bundle bundle = new Bundle();
                    bundle.putString("friend_no", friend_no);
                    bundle.putString("friend_name", friend_name);
                    bundle.putString("member_no", member_no);
                    bundle.putString("member_name", member_name);
                    fragment.setArguments(bundle);
                    switchFragment(fragment);
                }
            });
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImageFriend;
            TextView tvFriendName;

            public MyViewHolder(View itemView) {
                super(itemView);
                ivImageFriend = (ImageView) itemView.findViewById(R.id.ivImageFriend);
                tvFriendName = (TextView) itemView.findViewById(R.id.tvFriendName);
//                tvTitle_GroupsPhoto = (TextView) itemView.findViewById(R.id.tvTitle_GroupsPhoto);
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
