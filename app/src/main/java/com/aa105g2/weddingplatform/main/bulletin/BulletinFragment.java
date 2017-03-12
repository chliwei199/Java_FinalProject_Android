package com.aa105g2.weddingplatform.main.bulletin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.aa105g2.weddingplatform.R;

public class BulletinFragment extends Fragment {
    private WebView webView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.bulletin_fragment, container, false);

        Bundle bundle_FromGroupsPhoto =  getArguments();                                //get member_no from fragment
        String groups_no = bundle_FromGroupsPhoto.getString("groups_no");

        Bundle bundle_FromGroupsBulletinListFragment = getActivity().getIntent().getExtras();     //get member_no from activity
        String member_no = bundle_FromGroupsBulletinListFragment.getString("member_no");
       System.out.println("member_no from BulletinFragment:"+groups_no+","+member_no);


        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("http://10.0.2.2:8081/AA105G2_0110/bulletin/bulletinServlet.do?action=getOne_For_Display&"+groups_no);
    webView.loadUrl("http://google.com");
        System.out.println("http:"+groups_no+","+member_no);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        return view;
    }
}
