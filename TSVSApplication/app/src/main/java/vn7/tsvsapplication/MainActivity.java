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

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.simple.JSONObject;

import java.io.IOException;

import vn7.tsvsapplication.back_end.NetworkService;
import vn7.tsvsapplication.back_end.TSVSparser;
import vn7.tsvsapplication.back_end.UserData;
import vn7.tsvsapplication.base.TabFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private FirebaseAnalytics mFirebaseAnalytics;
    private View nav_header;
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
            if (!value) {
                snackbar.show();
            } else {
                snackbar.dismiss();
            }

        }
    };
    //session timeout handle
    private static final byte session_timeout = 0x01;
    Handler mHandler;

    public void sendMessageToHandler(int messgae) {
        Message msg = mHandler.obtainMessage();
        msg.what = messgae;
        msg.sendToTarget();
    }

    public void init() {

        IntentFilter filter = new IntentFilter(NetworkService.tag);
        // 將 BroadcastReceiver 在 Activity 掛起來。
        registerReceiver(receiver, filter);
        //無網路時顯示snackbar
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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //進入登入頁面
        intoLogin();

    }
    @Override
    public void onResume(){
        super.onResume();
        Bundle faBundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, faBundle);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == session_timeout) {
                    sign_out(false);
                    intoLogin();
                }
            }
        };
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //螢幕改變方向
        super.onConfigurationChanged(newConfig);
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public void onPause(){
        super.onPause();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.getStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        stopService(new Intent().setClass(MainActivity.this, NetworkService.class));
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here..
        int id = item.getItemId();
        Bundle faBundle = new Bundle();
        if (id == R.id.campus_information) {    //行事曆
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,getResources().getString(R.string.campus_information));
            toolbar.setTitle(getResources().getString(R.string.campus_information));
            switchFragment(new CalendarFragment());
        } else if (id == R.id.tamsui_weather) {  //靠北淡商
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,getResources().getString(R.string.tamsui_weather));
            toolbar.setTitle(getResources().getString(R.string.tamsui_weather));
            switchFragment(new WelcomFragment());
        }else if (id == R.id.cowbei_school) {  //靠北淡商
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,getResources().getString(R.string.cowbei_school));
            toolbar.setTitle(getResources().getString(R.string.cowbei_school));
            switchFragment(TabFragment.newInstance(getResources().getStringArray(R.array.cowbei_array)));
        } else if (id == R.id.semester_results) {   //學期成績
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,getResources().getString(R.string.semester_results));
            if (TSVSparser.isLogin()) {
                checkSession();
                toolbar.setTitle(getResources().getString(R.string.semester_results));
                switchFragment(TabFragment.newInstance(getResources().getStringArray(R.array.results_array)));
            } else {
                plzLogin();
                intoLogin();
            }
        } else if (id == R.id.lack_of_records) { //學期記錄
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,getResources().getString(R.string.lack_of_records));
            if (TSVSparser.isLogin()) {
                checkSession();
                toolbar.setTitle(getResources().getString(R.string.lack_of_records));
                switchFragment(TabFragment.newInstance(getResources().getStringArray(R.array.records_array)));
            } else {
                plzLogin();
                intoLogin();
            }
        } else if (id == R.id.OnlineRepair) {   //線上報修
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,getResources().getString(R.string.onlineRepair));
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, OnlineRepairActivity.class);
            startActivity(intent);

        }else if (id == R.id.about_us) {       //關於我們
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,getResources().getString(R.string.about_us));
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.sign_out) {       //登出
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,getResources().getString(R.string.sing_out));
            sign_out(true);
            intoLogin();
        }/* else if (id == R.id.Traffic) {        //交通資訊
        faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,getResources().getString());
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, TrafficActivity.class);
            startActivity(intent);
        } */
        //傳送事件到firebase分析

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, faBundle);
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
            Bundle faBundle = new Bundle();
            faBundle.putString(FirebaseAnalytics.Param.ITEM_NAME,"nav_header");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, faBundle);
            //跳轉登入畫面
            if (TSVSparser.isLogin()) {
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
                if (TSVSparser.checkSession()) {
                    Bundle faBundle = new Bundle();
                    faBundle.putString(FirebaseAnalytics.Param.VALUE,String.valueOf(TSVSparser.checkSession()));
                    mFirebaseAnalytics.logEvent("checkSession", faBundle);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "登入資訊過期,請重新登入", Toast.LENGTH_SHORT).show();
                        }
                    });
                    sendMessageToHandler(session_timeout);
                }
    }

    private void sign_out(boolean messageVisible) {
        if (messageVisible) {
            if (TSVSparser.isLogin())
                Toast.makeText(this, "登出囉ヽ(✿ﾟ▽ﾟ)ノ", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "還沒登入呢ヽ(✿ﾟ▽ﾟ)ノ", Toast.LENGTH_SHORT).show();
        }
        UserData.clear();
        TSVSparser.logout();
        class_number.setText("");
        user_id.setText(getResources().getString(R.string.click_to_login));
        user_name.setText(getResources().getString(R.string.not_login));
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
                }catch (Exception e) {
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
