package com.aa105g2.weddingplatform.main.advertisement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aa105g2.weddingplatform.R;


public class AdvPicFragment extends Fragment {
    private AdvVO adv;

    public static AdvPicFragment newInstance(AdvVO adv) {
        AdvPicFragment fragment = new AdvPicFragment();
        Bundle args = new Bundle();
        args.putSerializable("adv", adv);
        fragment.setArguments(args);
        return fragment;
    }

    public AdvPicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            adv = (AdvVO) getArguments().getSerializable("adv");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adv_pic_fragment, container, false);


        Bitmap bitmap = BitmapFactory.decodeByteArray(adv.getAdv_pic(), 0, adv.getAdv_pic().length);

        ImageView ivImage = (ImageView) view
                .findViewById(R.id.ivImage);
        ivImage.setImageBitmap(bitmap);

        return view;
    }
}
