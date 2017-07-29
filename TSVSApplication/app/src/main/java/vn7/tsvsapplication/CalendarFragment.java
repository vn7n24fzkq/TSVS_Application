package vn7.tsvsapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.Toast;


import org.json.simple.*;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

import vn7.tsvsapplication.adapter.CalendarListAdapter;
import vn7.tsvsapplication.back_end.TSVSparser;
import vn7.tsvsapplication.base.CalendarItem;


public class CalendarFragment extends Fragment implements View.OnClickListener {

    private ListView calendar_list;
    private CalendarListAdapter mListAdapter;
    private Button search;
    private EditText editText_calendar;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy年MM月", Locale.TAIWAN);
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public CalendarFragment() {

    }
    private void init(){

        mSwipeRefreshLayout.setColorSchemeResources(R.color.red,R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));
            }
        });
        setDateTimeField();
        Calendar newDate = Calendar.getInstance();
        editText_calendar.setText(dateFormatter.format(newDate.getTime()));
        search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dateFormatter.parse(editText_calendar.getText().toString());
                    Log.d("日期", String.valueOf(dateFormatter.getCalendar().get(Calendar.MONTH)));
                    search(dateFormatter.getCalendar().get(Calendar.YEAR), dateFormatter.getCalendar().get(Calendar.MONTH));
                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendar_list = (ListView) v.findViewById(R.id.calendar_list);
        editText_calendar = (EditText) v.findViewById(R.id.editText_calendar);
        editText_calendar.setInputType(InputType.TYPE_NULL);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);
        search = (Button) v.findViewById(R.id.search);
        init();
        search(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));
        // Inflate the layout for this fragment
        return v;
    }

    private void setDateTimeField() {
        editText_calendar.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editText_calendar.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        try{
           ((ViewGroup) datePickerDialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        }catch(Exception e){

        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }





    private final byte startLoading = 0x01;
    private final byte endLoading = 0x02;
    private final byte setAdapterList = 0x03;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == setAdapterList) {
                calendar_list.setAdapter(mListAdapter);
            } else if (msg.what == startLoading) {
                Toast.makeText(getActivity(),"努力載入中─=≡Σ((( つ•̀ω•́)つ",Toast.LENGTH_SHORT);
                mSwipeRefreshLayout.setRefreshing(true);
            } else if (msg.what == endLoading) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

        }
    };

    @Override
    public void onClick(View v) {
        if (v == editText_calendar) {
            datePickerDialog.show();
        }
    }

    class CalendarLoader extends Thread {
        Context context;
        String year, month;

        public CalendarLoader(Context context, String year, String month) {
            this.context = context;
            this.year = year;
            this.month = month;
        }

        public void run() {
            try {
                sendMessageToHandler(startLoading);
                JSONObject jsonObject = TSVSparser.getGoogle_Calendar(year, month);
                JSONArray jArray = (JSONArray) jsonObject.get("calendar");
                List<CalendarItem> itemList = new ArrayList<CalendarItem>();
                for (int i = 0; i < jArray.size(); i++) {
                    JSONObject jObject = (JSONObject) jArray.get(i);
                    itemList.add(new CalendarItem(jObject.get("date").toString(), jObject.get("schedule").toString(), jObject.get("department").toString()));
                }
                itemList.add(new CalendarItem("本月尚無更多排定事項", "(,,・ω・,,)已經沒有囉", ""));
                mListAdapter = new CalendarListAdapter(context, itemList);
                calendar_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View v, int arg2,
                                            long arg3) {
                    /*
                             TextView date = (TextView) v.findViewById(R.id.date);
                            TextView schedule = (TextView) v.findViewById(R.id.schedule);
                            */
                    }

                });
                sendMessageToHandler(setAdapterList);
                sendMessageToHandler(endLoading);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendMessageToHandler(int messgae) {
            Message msg = mHandler.obtainMessage();
            msg.what = messgae;
            msg.sendToTarget();
        }
    }

    private void search(int year, int month) {
        CalendarLoader loader = new CalendarLoader(this.getActivity(), String.valueOf(year), String.valueOf(month + 1));
        loader.start();
    }

    public void onButtonPressed(Uri uri) {

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


