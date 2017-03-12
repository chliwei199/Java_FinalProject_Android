package com.aa105g2.weddingplatform.main;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.aa105g2.weddingplatform.R;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import com.aa105g2.weddingplatform.main.appointment.AppointmentListFragment;
import com.aa105g2.weddingplatform.main.bulletin.BulletinFragment;
import com.aa105g2.weddingplatform.main.friend.FriendsListFragment;
import com.aa105g2.weddingplatform.main.groups.GroupsBulletinListFragment;
import com.aa105g2.weddingplatform.main.groups.GroupsPhotoListFragment;
import com.aa105g2.weddingplatform.main.home.HomeFragment;
import com.aa105g2.weddingplatform.main.member.LoginActivity;
import com.aa105g2.weddingplatform.main.member.MemberGetImageTask;
import com.aa105g2.weddingplatform.main.member.MemberVO;
import com.aa105g2.weddingplatform.main.message.MessagesFragment;
import com.aa105g2.weddingplatform.main.photo.PhotoListFragment;
import com.aa105g2.weddingplatform.main.place.PlaceListFragment;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
//    private PhotoListFragment PhotoListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermissions();
        setUpActionBar();
        initDrawer();
        initBody();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // home icon will keep still without calling syncState()
        actionBarDrawerToggle.syncState();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDrawer() {
        String url = Common.URL + "MemberServletForApp";

        final NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        Bundle bundle = getIntent().getExtras();                //get bundle member_name
        String member_name = bundle.getString("member_name");
        String member_no = bundle.getString("member_no");
//        System.out.println("initDrawer member_no:"+member_no);


        View nav_header = LayoutInflater.from(this).inflate(R.layout.navigate_header, null);
        ((TextView) nav_header.findViewById(R.id.tvUserName)).setText(member_name);
        int imageSize = 250;
        new MemberGetImageTask((ImageView)nav_header.findViewById(R.id.ivUser)).execute(url, member_no, imageSize);  //set navigation header image

        view.addHeaderView(nav_header);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.text_Open, R.string.text_Close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);


        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
//                if(menuItem.getItemId() != R.id.item_Groups) {
//                    drawerLayout.closeDrawers();
//                }
                drawerLayout.closeDrawers();
                Fragment fragment;
//                final Fragment[] fragment = new Fragment[1];
                switch (menuItem.getItemId()) {
                    case R.id.item_Home:
                        initBody();
                        break;
                    case R.id.item_MyAppointment:
//                        fragment[0] = new AppointmentListFragment();
//                        switchFragment(fragment[0]);
                        fragment = new AppointmentListFragment();
                          switchFragment(fragment);
                        setTitle(R.string.text_MyAppointment);
                        break;
                    case R.id.item_Messages:
//                        fragment[0] = new FriendsListFragment();
//                        switchFragment(fragment[0]);
                        fragment = new FriendsListFragment();
                        switchFragment(fragment);
                        setTitle(R.string.text_Messages);
                        break;
                    case R.id.item_Places:
//                        fragment[0] = new PlaceListFragment();
//                        switchFragment(fragment[0]);
                        fragment = new PlaceListFragment();
                        switchFragment(fragment);
                        setTitle(R.string.text_Place);
                        break;
                    case R.id.item_Groups:
                        fragment = new GroupsPhotoListFragment();
                        switchFragment(fragment);
//                        if(!(view.getMenu().getItem(5).isVisible())) {
//                            view.getMenu().getItem(5).setVisible(true);
//                        }
//                        else{
//                            view.getMenu().getItem(5).setVisible(false);
//                        }
//                        view.getMenu().getItem(5).getSubMenu().getItem(0).setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
//                            public boolean onMenuItemClick(MenuItem item) {
//                                drawerLayout.closeDrawers();
////                                fragment[0] = new BulletinFragment();
//                                fragment[0] = new GroupsBulletinListFragment();
//                                switchFragment(fragment[0]);
//                                return true;
//                            }
//                        }); // for Bulletin Group Fragment list
//                        view.getMenu().getItem(5).getSubMenu().getItem(1).setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
//                            public boolean onMenuItemClick(MenuItem item) {
//                                drawerLayout.closeDrawers();
//                                fragment[0] = new GroupsPhotoListFragment();
//                                switchFragment(fragment[0]);
//                                return true;
//                            }
//                        }); // for PhotoGraphyGroup Fragment list
                        setTitle(R.string.text_Groups);
                        break;
                    default:
                        initBody();
                        break;
                }
                return true;
            }
        });
    }

    private void initBody() {
        Fragment fragment = new HomeFragment();
//        Fragment fragment = new PhotoListFragment();
        switchFragment(fragment);
        setTitle(R.string.text_Home);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
    }

    private static final int REQ_PERMISSIONS = 0;

    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    REQ_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSIONS:
                String text = "";
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        text += permissions[i] + "\n";
                    }
                }
                if (!text.isEmpty()) {
                    text += getString(R.string.text_NotGranted);
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStack();
            Log.d("onBackPressed", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
        } else {
            super.onBackPressed();
        }
    }
}
