package vn7.tsvsapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import org.json.simple.JSONObject;

import java.io.IOException;

import vn7.tsvsapplication.back_end.NetworkService;
import vn7.tsvsapplication.back_end.TSVSparser;
import vn7.tsvsapplication.back_end.UserData;
import vn7.tsvsapplication.base.TabFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static View nav_header;
    private static TextView user_id, user_name, class_number;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    private Toolbar toolbar;
    private NavigationView navigationView;
    //set network snackbar
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 處理 Service 傳來的訊息。
            Bundle message = intent.getExtras();
            boolean value = message.getBoolean("networkConnectStatus");
            String strValue = String.valueOf(value);
            if (value == false) {
                snackbar.show();
            } else {
                snackbar.dismiss();
            }

        }
    };
    //session timeout handle
    private final byte session_timeout = 0x01;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == session_timeout) {
                sign_out(false);
                intoLogin();
            }

        }
    };

    public void sendMessageToHandler(int messgae) {
        Message msg = mHandler.obtainMessage();
        msg.what = messgae;
        msg.sendToTarget();
    }

    public void init() {

        IntentFilter filter = new IntentFilter(NetworkService.tag);
        // 將 BroadcastReceiver 在 Activity 掛起來。
        registerReceiver(receiver, filter);
        snackbar = Snackbar
                .make(coordinatorLayout, getResources().getString(R.string.no_NetWork), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.close), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent().setClass(MainActivity.this, NetworkService.class));
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.app_bar_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);//bar陰影設為0


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //設置按下nav_header 後觸發onClick
        nav_header = (View) navigationView.getHeaderView(0);
        nav_header.setOnClickListener(this);
        setNav_headerView();
        //初始化
        init();
        //進入登入頁面
        intoLogin();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onDestroy() {
        stopService(new Intent().setClass(MainActivity.this, NetworkService.class));
        super.onDestroy();
    }
    /*
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    */
    ;
    // private static HashMap<String, Fragment> fragmentArrayList = new HashMap<String, Fragment>();

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here..

        int id = item.getItemId();

        if (id == R.id.campus_information) {
            toolbar.setTitle(getResources().getString(R.string.campus_information));
            switchFragment(new CalendarFragment());
        } else if (id == R.id.school_timetable) {
            toolbar.setTitle(getResources().getString(R.string.school_timetable));
      //      switchFragment(new CowBeiTSVSFragment());
            switchFragment(TabFragment.newInstance(getResources().getStringArray(R.array.cowbei_array)));
         /*   Intent intent = new Intent();
            intent.setClass(MainActivity.this, WebviewActivity.class);
            startActivity(intent);*/
        } else if (id == R.id.semester_results) {
            if (TSVSparser.getLoginStatus() == true) {
                checkSession();
                toolbar.setTitle(getResources().getString(R.string.semester_results));
               // switchFragment(new StuScoreFragment());
                switchFragment(TabFragment.newInstance(getResources().getStringArray(R.array.results_array)));
            } else {
                plzLogin();
                intoLogin();
            }
        } else if (id == R.id.lack_of_records) {
            if (TSVSparser.getLoginStatus() == true) {
                checkSession();
                toolbar.setTitle(getResources().getString(R.string.lack_of_records));
                switchFragment(TabFragment.newInstance(getResources().getStringArray(R.array.records_array)));
            } else {
                plzLogin();
                intoLogin();
            }
        } else if (id == R.id.OnlineRepair) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, OnlineRepairActivity.class);
            startActivity(intent);
        } else if (id == R.id.Traffic) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, TrafficActivity.class);
            startActivity(intent);
        } else if (id == R.id.about_us) {
        //    toolbar.setTitle(getResources().getString(R.string.about_us));
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.sign_out) {
            sign_out(true);
            intoLogin();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchFragment(Fragment fragment) {

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
        }
    }

    private void plzLogin() {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, "請先登入", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void intoLogin() {
        toolbar.setTitle(getResources().getString(R.string.app_name));
        switchFragment(new LoginFragment());
    }

    private void setNav_headerView() {
        user_id = (TextView) nav_header.findViewById(R.id.user_id);
        user_name = (TextView) nav_header.findViewById(R.id.user_name);
        class_number = (TextView) nav_header.findViewById(R.id.class_number);
    }

    @Override
    public void onClick(View v) {
        if (v == nav_header) {
            //跳轉登入畫面
            if (TSVSparser.getLoginStatus() == true) {
                toolbar.setTitle(getResources().getString(R.string.app_name));
                switchFragment(new WelcomFragment());
            } else {
                intoLogin();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void checkSession() {
        Thread th = new Thread() {
            @Override
            public void run() {
                if (TSVSparser.checkSession() == false) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "登入資訊過期,請重新登入", Toast.LENGTH_SHORT).show();
                        }
                    });
                    sendMessageToHandler(session_timeout);
                }
            }
        };
        th.start();
    }

    private void sign_out(boolean messageVisible) {
        if (messageVisible == true) {
            if (TSVSparser.getLoginStatus())
                Toast.makeText(this, "登出囉ヽ(✿ﾟ▽ﾟ)ノ", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "還沒登入呢ヽ(✿ﾟ▽ﾟ)ノ", Toast.LENGTH_SHORT).show();
        }
        UserData.clear();
        TSVSparser.logout();
        class_number.setText("");
        user_id.setText(getResources().getString(R.string.click_to_login));
        user_name.setText(getResources().getString(R.string.not_login));
        //    fragmentArrayList  = new HashMap<String, Fragment>();
    }

    public static void loadStuInfo() {
        Thread th = new Thread() {
            public void run() {
                JSONObject jobject = new JSONObject();
                try {
                    jobject = TSVSparser.getStu_Info();
                    String classs = (String) jobject.get("class");
                    String class_number = (String) jobject.get("class_number");
                    String stu_number = (String) jobject.get("stu_number");
                    String name = (String) jobject.get("name");
                    setStuInfo(classs, class_number, stu_number, name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        th.start();

    }

    private static void setStuInfo(String classs, String classs_number, String
            stu_number, String name) {
        UserData.name = name;
        UserData.account_number = stu_number;
        UserData.classs_number = classs_number;
        UserData.classs = classs;
        class_number.setText(classs + classs_number + "號");
        user_id.setText(stu_number);
        user_name.setText(name);
    }
}
