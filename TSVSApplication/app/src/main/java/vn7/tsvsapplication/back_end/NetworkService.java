package vn7.tsvsapplication.back_end;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class NetworkService extends Service {
    private static boolean networkConnectStatus = false;
    public static final String tag = "NetworkService";
    private final BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
      /*       ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
           if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
               Log.d("NetworkService","lost connect");
                networkConnectStatus = false;
                // unconnect network
            } else {
            }
        }*/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            networkConnectStatus = cm.getActiveNetworkInfo() != null;
            if ( networkConnectStatus) {
             //   networkConnectStatus = true;
                Log.d("NetworkService", "connected");
            } else {
             //   networkConnectStatus = false;
                Log.d("NetworkService", "lost connect");
            }

            Bundle message = new Bundle();
            message.putBoolean("networkConnectStatus", networkConnectStatus);
            Intent network_intent = new Intent(tag);
            network_intent.putExtras(message);
            sendBroadcast(network_intent);

        }
    };
    public void init() {
        networkConnectStatus = false;
    }

    @Override
    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, intentFilter);
        init();
    }

    @Override
    public void onStart(Intent intent, int startId) {
    }

    @Override
    public void onDestroy() {
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static boolean isConnected() {
    /*   String command = "ping -c 1 google.com";
        boolean result = false;
        try {
            result = ( Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;*/
        return  networkConnectStatus;
    }
}
