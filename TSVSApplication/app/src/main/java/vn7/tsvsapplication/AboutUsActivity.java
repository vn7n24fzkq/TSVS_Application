package vn7.tsvsapplication;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;

public class AboutUsActivity extends AppCompatActivity  implements View.OnClickListener{
    private FirebaseAnalytics mFirebaseAnalytics;
    View github,email,googleplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getResources().getString(R.string.about_us));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 给左上角加上一个返回的圖示
           actionBar.setDisplayHomeAsUpEnabled(true);
        //再actionbar顯示Android.R.id.home圖示
       //  actionBar.setDisplayShowHomeEnabled(true);
        }
        //set github_link
        TextView t = (TextView) findViewById(R.id.github_link);
        t.setMovementMethod(LinkMovementMethod.getInstance());
        FloatingActionButton fab =(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AboutUsActivity.this, OpenSourceActivity.class);
                startActivity(intent);
            }
        });
        github = findViewById(R.id.view_github);
        email = findViewById(R.id.view_email);
        googleplay = findViewById(R.id.view_googleplay);
        github.setOnClickListener(this);
        email.setOnClickListener(this);
        googleplay.setOnClickListener(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle faBundle = new Bundle();
        switch (item.getItemId()) {
            case android.R.id.home:
                faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,"home");
                mFirebaseAnalytics.logEvent( "onOptionsItemSelected", faBundle);
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        Bundle faBundle = new Bundle();
        if(v.getId() == R.id.view_email){
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,"email");
            Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:1104108117@kuas.edu.tw"));
            startActivity(browserIntent);
        }else if(v.getId() == R.id.view_github ){
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,"github");
            Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/vn7n24fzkq/TSVS_Application"));
            startActivity(browserIntent);
        }else if(v.getId() == R.id.view_googleplay){
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,"googleplay");
            String sParam = "vn7.tsvsapplication";
            try{
                // Open app with Google Play app
                Intent  intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+sParam));
                startActivity(intent);
            }
            catch (android.content.ActivityNotFoundException anfe)
            {
                // Open Google Play website
                Intent  intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+sParam));
                startActivity(intent);
            }
        }
        mFirebaseAnalytics.logEvent( "onClick", faBundle);
    }
}
