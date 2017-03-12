package com.aa105g2.weddingplatform.main.menu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aa105g2.weddingplatform.R;




public class MenuFragment extends Fragment {
    private MenuVO menu;

    public static MenuFragment newInstance(MenuVO menu) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putSerializable("menu", menu);
        fragment.setArguments(args);
        return fragment;
    }

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            menu = (MenuVO) getArguments().getSerializable("menu");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);


        Bitmap bitmap = BitmapFactory.decodeByteArray(menu.getMenu_pic(), 0, menu.getMenu_pic().length);

        ImageView ivImage = (ImageView) view
                .findViewById(R.id.ivImage);
        ivImage.setImageBitmap(bitmap);

        return view;
    }
}
