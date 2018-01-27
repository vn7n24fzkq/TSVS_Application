package vn7.tsvsapplication;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import vn7.tsvsapplication.back_end.NetworkService;
import vn7.tsvsapplication.back_end.TSVSparser;
import vn7.tsvsapplication.base.ProgressWebView;

public class OnlineRepairActivity extends AppCompatActivity {
    private ProgressWebView myWebView;
    private FirebaseAnalytics mFirebaseAnalytics;

    public OnlineRepairActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_repair);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
             actionBar.setTitle("線上報修");
        }
        myWebView = (ProgressWebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.requestFocus();
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(TSVSparser.onlineRepair_url);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.webview_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle faBundle = new Bundle();
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,"home");
                this.finish();
                return true;
            case R.id.download:
                faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,"download");
                if(NetworkService.isConnected()) {
                    new AlertDialog.Builder(OnlineRepairActivity.this)
                            .setMessage("下載目前線上報修結果")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(getApplicationContext(), "開始下載線上報修結果", Toast.LENGTH_SHORT).show();
                                    // app icon in action bar clicked; goto parent activity.
                                    downloadOnlineRepairFile();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            }).show();

                }else{
                    Toast.makeText(getApplicationContext(),"沒有網路啦(#`Д´)ﾉ",Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                break;
        }
        mFirebaseAnalytics.logEvent("onOptionsItemSelected", faBundle);
        return true;
    }
    @Override
    public void onBackPressed() {
        Bundle faBundle = new Bundle();
        mFirebaseAnalytics.logEvent("onBackPressed", faBundle);
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }

    }
    private void downloadOnlineRepairFile(){
            if(NetworkService.isConnected()){
                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(TSVSparser.onlineRepairFile_url));
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                req.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS,"新北市立淡水商工 一般設施線上報修結果");
                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                long downloadId = dm.enqueue(req);
            }else {
                Toast.makeText(getApplicationContext(), "下載失敗，沒有網路連接", Toast.LENGTH_SHORT).show();
            }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }}
