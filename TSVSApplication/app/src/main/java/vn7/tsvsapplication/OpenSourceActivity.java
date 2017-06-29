package vn7.tsvsapplication;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class OpenSourceActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //     actionBar.setDisplayShowHomeEnabled(true);
        }
        TextView t = (TextView) findViewById(R.id.weather_license_link);
        t.setMovementMethod(LinkMovementMethod.getInstance());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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

    }
}
