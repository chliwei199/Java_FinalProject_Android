package com.aa105g2.weddingplatform.main.photo;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.aa105g2.weddingplatform.R;
import com.aa105g2.weddingplatform.main.Common;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import static android.app.Activity.RESULT_OK;
import java.text.DateFormat;

public class PhotInsertFragment  extends Fragment {
    private final static String TAG = "PhotoInsertFragment";
    private EditText etPhotoTitle;
    private EditText etPhotoName;
    private Button btTakePicture;
    private Button btFinishInsert;
    private Button btCancel;
    private ImageView ivPhoto;
    private Bitmap picture;
    private byte[] image;
    private File file;
    java.sql.Timestamp sql_currentTime;
    private static final int REQUEST_TAKE_PICTURE = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_insert_fragment, container, false);

        findViews(getActivity());
         ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
         btTakePicture = (Button) view.findViewById(R.id.btTakePicture);
         etPhotoTitle = (EditText) view.findViewById(R.id.etPhotoTitle);
         etPhotoName = (EditText) view.findViewById(R.id.etPhotoName);
         btFinishInsert = (Button) view.findViewById(R.id.btFinishInsert);
         btCancel = (Button) view.findViewById(R.id.btCancel);

        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                if (isIntentAvailable( getActivity(), intent)) {
                    startActivityForResult(intent, REQUEST_TAKE_PICTURE);
                } else {
                    Toast.makeText( getActivity(), R.string.msg_NoCameraAppsFound, Toast.LENGTH_SHORT).show();
                }
            }
        });  //end of btTakePicture.setOnClickListener

        btFinishInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String PhotoTitle = etPhotoTitle.getText().toString().trim();
                String PhotoName = etPhotoName.getText().toString().trim();

                if (PhotoTitle.length() <= 0) {
                    Toast.makeText( getActivity(), R.string.msg_TitleIsInvalid, Toast.LENGTH_SHORT).show();
                    return;   }
                if (PhotoName.length() <= 0) {
                    Toast.makeText( getActivity(), R.string.msg_NameIsInvalid, Toast.LENGTH_SHORT).show();
                    return;   }

                if (Common.networkConnected( getActivity())) {
                    Bundle bundle_Activity = getActivity().getIntent().getExtras();     //get bundle from Activity
                    String member_no = bundle_Activity.getString("member_no");

                    Bundle bundle =  getArguments();                                //get bundle from fragment
                    String groups_no = bundle.getString("groups_no");
//                    System.out.println("member_no from PhotInsertFragment:"+member_no);
//                    System.out.println("groups_no from PhotInsertFragment:"+groups_no);

                    String url = Common.URL + "PhotoServletForApp";
//                    PhotoVO photoVO = new PhotoVO(member_no,groups_no, PhotoTitle,new java.sql.Timestamp(System.currentTimeMillis()));
//                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                    String date_current=df.format(Calendar.getInstance().getTime());
//                    String date_currentFinal= String.valueOf(String.valueOf(date_current).subSequence(0,18));
//                    sql_currentTime=java.sql.Timestamp.valueOf(date_currentFinal);
//
//                    System.out.println("TEST:"+sql_currentTime);
                    PhotoVO photoVO=new PhotoVO();
                    photoVO.setMember_no(member_no);
                    photoVO.setGroups_no(groups_no);
                    photoVO.setPhoto_title(PhotoTitle);
//                    photoVO.setPhoto_createdate(sql_currentTime);
//                    PhotoVO photoVO = new PhotoVO(member_no,groups_no, PhotoTitle,image, new java.sql.Timestamp(System.currentTimeMillis()));
                    String imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
                    String action = "PhotoInsert";
                    int count = 0;
                    try {

                        count = new PhotoUpdateTask().execute(url, action, photoVO, imageBase64).get();
//                        count = new PhotoUpdateTask().execute(url, action, photoVO).get();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(getActivity(), R.string.msg_InsertFail);
                    } else {
                        Common.showToast(getActivity(), R.string.msg_InsertSuccess);
                    }
                } else {
                    Common.showToast( getActivity(), R.string.msg_NoNetwork);
                }
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });  //end of btFinishInsert.setOnClickListener

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Fragment fragment = new GroupsPhotoListFragment();   // back to
//                switchFragment(fragment);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });  //end of  btCancel.setOnClickListener

        return view;
    } //end of onCreateView

    private void findViews(FragmentActivity view) {
        ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
        btTakePicture = (Button) view.findViewById(R.id.btTakePicture);
        etPhotoTitle = (EditText) view.findViewById(R.id.etPhotoTitle);
        etPhotoName = (EditText) view.findViewById(R.id.etPhotoName);
        btFinishInsert = (Button) view.findViewById(R.id.btFinishInsert);
        btCancel = (Button) view.findViewById(R.id.btCancel);
    }
    private boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 手機拍照App拍照完成後可以取得照片圖檔
                case REQUEST_TAKE_PICTURE:
//                    Bitmap picture = BitmapFactory.decodeFile(file.getPath());
                    picture = downSize(file.getPath());
                    ivPhoto.setImageBitmap(picture);

                    ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                    picture.compress(Bitmap.CompressFormat.JPEG, 100, out1);
                    image = out1.toByteArray();
                    break;
            }
        }
    }
    private Bitmap downSize(String path) {
        Bitmap picture = BitmapFactory.decodeFile(path);
//        String text = "original size = " + picture.getWidth() + "x" + picture.getHeight();
//        tvMessage.setText(text);
        // 設定寬度不超過width，並利用Options.inSampleSize來縮圖
        int scaleSize = 512;
        int longer = Math.max(picture.getWidth(), picture.getHeight());
        if (longer > scaleSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 若原始照片寬度無法整除width，則inSampleSize + 1，
            // 若則inSampleSize = 3，實際縮圖時為2，參看javadoc
            options.inSampleSize = longer % scaleSize == 0 ?
                    longer / scaleSize : longer / scaleSize + 1;
            picture = BitmapFactory.decodeFile(file.getPath(), options);
            System.gc();
//            text = "\ninSampleSize = " + options.inSampleSize +
//                    "\nnew size = " + picture.getWidth() + "x" + picture.getHeight();
//            tvMessage.append(text);
        }
        return picture;
    }
}
