package vn7.tsvsapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class TrafficActivity extends AppCompatActivity {
    private ImageButton taiwanTaxi, honrenTaxi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.Traffic);
        }
        taiwanTaxi = (ImageButton) findViewById(R.id.taiwanTaxi);
        honrenTaxi = (ImageButton) findViewById(R.id.honrenTaxi);
        taiwanTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callTaiwanTaxi();
            }
        });
        honrenTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callHonrenTaxi();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
       // inflater.inflate(R.menu.traffic_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.refresh:

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

    private void callTaiwanTaxi() {
        new AlertDialog.Builder(TrafficActivity.this)
                .setMessage("撥打55688(台灣大車隊)")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:55688"));
                        startActivity(callIntent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                }).show();

    }

    private void callHonrenTaxi() {
        new AlertDialog.Builder(TrafficActivity.this)
                .setMessage("撥打宏仁計程車")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:0228057777"));
                        startActivity(callIntent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                }).show();

    }
}
