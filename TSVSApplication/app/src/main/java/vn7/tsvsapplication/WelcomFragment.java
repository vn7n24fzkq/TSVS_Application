package vn7.tsvsapplication;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import vn7.tsvsapplication.adapter.CalendarListAdapter;
import vn7.tsvsapplication.base.CalendarItem;
import vn7.tsvsapplication.back_end.TSVSparser;
import vn7.tsvsapplication.back_end.weather.WeatherSample;
import vn7.tsvsapplication.back_end.weather.WeekWeather;


public class WelcomFragment extends Fragment {



    private static RecyclerView weather_list;

    private static SwipeRefreshLayout  weatherRefreshLayout;



    private static final byte startLoading = 0x01;
    private static final byte endLoading = 0x02;
    private static  final byte setAdapterList = 0x03;
    private static final byte weatherRefresh = 0x05;
    private static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int Adapter = msg.arg1, loadStatus = msg.what;
            switch (Adapter) {
                case weatherRefresh:
                    if (msg.what == setAdapterList) {
                        final MainAdapter adapter = new MainAdapter((ArrayList<WeekWeather>) msg.obj);
                        weather_list.setAdapter(adapter);
                        sendMessageToHandler(weatherRefresh, endLoading);
                        //  calendar_list.setAdapter(mListAdapter);
                    } else if (msg.what == startLoading) {
                        weatherRefreshLayout.setRefreshing(true);
                    } else if (msg.what == endLoading) {
                        weatherRefreshLayout.setRefreshing(false);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    private final static MyHandler mHandler = new MyHandler();
    private void init() {

        weather_list.getItemAnimator().setChangeDuration(300);
        weather_list.getItemAnimator().setMoveDuration(300);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        weather_list.setLayoutManager(layoutManager);

        weatherRefreshLayout.setColorSchemeResources(R.color.red, R.color.orange, R.color.green, R.color.blue);
        weatherRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                weather_refresh();
            }
        });
    }

    public WelcomFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcom, container, false);
        weather_list = (RecyclerView) v.findViewById(R.id.weather_list);
        weatherRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.weather_refresh_layout);
        init();

        weather_refresh();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void weather_refresh() {
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    sendMessageToHandler(weatherRefresh, startLoading);
                    ArrayList<WeekWeather> obj = new WeatherSample().getWeekWeather("淡水區");
                    Message message = mHandler.obtainMessage(setAdapterList, obj);
                    message.arg1 = weatherRefresh;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        th.start();

    }




    private static void sendMessageToHandler(int refreshLayout, int messgae) {
        Message msg = mHandler.obtainMessage();
        msg.arg1 = refreshLayout;
        msg.what = messgae;
        msg.sendToTarget();
    }
}

class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private final ArrayList<WeekWeather> weatherList;
    private int opened = -1;
    public MainAdapter(ArrayList<WeekWeather> weatherList) {
        this.weatherList = weatherList;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        return new MainViewHolder((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int pos) {
        holder.bind(pos);
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView day;
        private final TextView pop;
        private final TextView temprature;
        private final TextView UCV;
        private final TextView infos;
        private final LinearLayout weather_item;
        SimpleDateFormat sdfor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd  HH時 \n EEE");


        private MainViewHolder(ViewGroup itemView) {
            super(itemView);
            weather_item =(LinearLayout)itemView.findViewById(R.id. weather_item);
            day = ((TextView) itemView.findViewById(R.id.day));
            pop = ((TextView) itemView.findViewById(R.id.pop));
            temprature = ((TextView) itemView.findViewById(R.id.temprature));
            UCV = ((TextView) itemView.findViewById(R.id.UCV));
            infos = ((TextView) itemView.findViewById(R.id.infos));

            weather_item.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        private void bind(int pos) {

            WeekWeather weather = weatherList.get(pos);
            try {
                Date date = sdfor.parse(weather.start_time);
                day.setText(sdFormat.format(date));
            } catch (Exception e) {

            }

            temprature.setText(weather.MaxAT + "°" + "/" + weather.MinAT + "°");
            if (!weather.ExposureDescription.equals("")) {
                UCV.setText(weather.ExposureDescription+"\n"+weather.UVILevel);
            }else{
                UCV.setText("--");
            }
            if (!weather.PoP.equals("")) {
                pop.setText(weather.PoP+"%");
            }else{
                pop.setText("--");
            }
            infos.setText(weather.WeatherDescription);

            if (pos == opened)
                infos.setVisibility(View.VISIBLE);
            else
                infos.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (opened == getPosition()) {
                opened = -1;
                notifyItemChanged(getPosition());
            } else {
                int oldOpened = opened;
                opened = getPosition();
                notifyItemChanged(oldOpened);
                notifyItemChanged(opened);
            }
        }
    }
}