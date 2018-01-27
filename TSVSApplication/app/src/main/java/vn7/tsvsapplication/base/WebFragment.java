package vn7.tsvsapplication.base;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;

import vn7.tsvsapplication.R;

public class WebFragment extends Fragment {
    private String url = "www.google.com";

    public static WebFragment newInstance(String url) {
        WebFragment newFragment = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_web, container, false);
        ProgressWebView myWebView = (ProgressWebView) v.findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);//to use reCAPTCHA on website
        myWebView.requestFocus();
        myWebView.setWebViewClient(new WebViewClient());
        Bundle args = getArguments();
        if (args != null) {
            url = args.getString("url", "www.google.com");
        }
        myWebView.loadUrl(url);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
