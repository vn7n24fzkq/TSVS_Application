package vn7.tsvsapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import vn7.tsvsapplication.back_end.NetworkService;
import vn7.tsvsapplication.back_end.TSVSparser;
import vn7.tsvsapplication.back_end.weather.WeatherSample;


public class LoginFragment extends Fragment {
    private AdView mAdView;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    private EditText numberEdit, passwordEdit;
    private Button loginButton;
    private CheckBox passwordCheckBox;
    private ProgressDialog myProgressDialog;
    private TextView versionName;
    private final String checkBox = "checkBox";
    private final String pre_number = "number";
    private final String pre_password = "password";
    public final byte startLoading = 0x01;
    public final byte endLoading = 0x02;
    Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == startLoading) {
                myProgressDialog.show();
            } else if (msg.what == endLoading) {
                myProgressDialog.dismiss();
                if (TSVSparser.getLoginStatus() == TSVSparser.sucess) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), "登入成功ヽ(✿ﾟ▽ﾟ)ノ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("Login", "登入成功ヽ(✿ﾟ▽ﾟ)ノ");
                    MainActivity.loadStuInfo();
                    getFragmentManager().beginTransaction().replace(R.id.content_main, new WelcomFragment()).commit();
                } else {
                    if (NetworkService.isConnected())
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getActivity(), "登入失敗,請檢查帳號密碼是否正確", Toast.LENGTH_SHORT).show();
                            }
                        });
                    Log.d("Login", "登入失敗・゜・(PД`q｡)・゜・");
                }
            }
            super.handleMessage(msg);
        }
    };

    private void setProgressDialog() {
        myProgressDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        myProgressDialog.setMessage("正在登入( ﾟ∀ﾟ)o彡ﾟ");
        myProgressDialog.setCancelable(false);

    }

    private void sendMessageToHandler(int messgae) {
        Message msg = loginHandler.obtainMessage();
        msg.what = messgae;
        msg.sendToTarget();
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    private void init() {
    	getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        MobileAds.initialize(getActivity(),getString(R.string.appAdMob));

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        SharedPreferences pref = getActivity().getSharedPreferences("account", getActivity().MODE_PRIVATE);
        numberEdit.setText(pref.getString(pre_number, ""));
        passwordEdit.setText(pref.getString(pre_password, ""));
        passwordCheckBox.setChecked(pref.getBoolean(checkBox, false));
        //獲得版本號
        PackageManager manager = getActivity().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            final int appVersion = info.versionCode; //版本號
            versionName.setText("Ver." +  info.versionName);
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            mFirebaseRemoteConfig.setConfigSettings(configSettings);
            // mFirebaseRemoteConfig.setDefaults(Integer.parseInt(appVersion));
            mFirebaseRemoteConfig.fetch(30)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            try {
                                if (task.isSuccessful()) {
                                    // After config data is successfully fetched, it must be activated before newly fetched
                                    // values are returned.
                                    mFirebaseRemoteConfig.activateFetched();
                                    String new_version = (String) mFirebaseRemoteConfig.getString("versionCode");
                                    if (Integer.parseInt(new_version) > appVersion) {
                                        Toast.makeText(getActivity(), "play商店有新版的校務通", Toast.LENGTH_LONG).show();
                                    }
                                    String toast_message = (String) mFirebaseRemoteConfig.getString("toast_message");
                                    if(!toast_message.equals("")){
                                        Toast.makeText(getActivity(), toast_message, Toast.LENGTH_SHORT).show();
                                    }
                                    String dialog_message = (String) mFirebaseRemoteConfig.getString("dialog_message");
                                    if(!dialog_message.equals("")){
                                        new AlertDialog.Builder(getActivity())
                                                .setMessage(dialog_message)
                                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                    }
                                                }).show();
                                    }
                                    TSVSparser.google_calendar_url = (String) mFirebaseRemoteConfig.getString("google_calendar");
                                    WeatherSample.key = (String) mFirebaseRemoteConfig.getString("Weather_Authorization");
                                }
                            } catch (Exception e) {

                            }
                        }
                    });

            //  int versionCode = manager.getPackageInfo(getActivity().getPackageName(), 0).versionCode; //版本號
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        numberEdit = (EditText) v.findViewById(R.id.number);
        passwordEdit = (EditText) v.findViewById(R.id.password);
        loginButton = (Button) v.findViewById(R.id.login);
        passwordCheckBox = (CheckBox) v.findViewById(R.id.password_checkBox);
        versionName = (TextView) v.findViewById(R.id.versionName);
        mAdView = (AdView) v.findViewById(R.id.adView);
        init();
        loginButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                putPreferences();
                login();
            }
        });
        setProgressDialog();
        // Inflate the layout for this fragment
        return v;
    }

    private void login() {
        Log.d("login", "登入中");
        final String number = numberEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        Thread th = new Thread() {
            public void run() {
                sendMessageToHandler(startLoading);
                TSVSparser.login(number, password);
                sendMessageToHandler(endLoading);
            }
        };
        th.start();

    }

    private void putPreferences() {
        SharedPreferences pref = getActivity().getSharedPreferences("account", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean(checkBox, passwordCheckBox.isChecked());
        ed.putString(pre_number, numberEdit.getText().toString());
        if (passwordCheckBox.isChecked()) {
            ed.putString(pre_password, passwordEdit.getText().toString());
        } else {
            ed.putString(pre_password, "");
        }
        ed.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        putPreferences();
    }

}
