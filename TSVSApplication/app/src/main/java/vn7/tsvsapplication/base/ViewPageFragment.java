package vn7.tsvsapplication.base;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import vn7.tsvsapplication.R;


public class ViewPageFragment extends Fragment{

    public TableLayout tableLayout;
    public TextView title, remindText;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public ArrayList<TextView> textArray = new ArrayList<>();


    public void init() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadScore();
            }
        });
        loadScore();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_page, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);
        tableLayout = (TableLayout) v.findViewById(R.id.MidtermResultsTable);
        remindText = (TextView) v.findViewById(R.id.remindText);
        title = (TextView) v.findViewById(R.id.result_title);
        //***********************


        //   seekbar= (SeekBar) (inflater.inflate(R.layout.textsize_seekbar, null).findViewById(R.id.seekbar));
        init();
        tableLayout.setOnClickListener(new View.OnClickListener() {
            AlertDialog.Builder popDialog  = new AlertDialog.Builder(getActivity());
            int seekprogress = 0;
            @Override
            public void onClick(View v) {
                SeekBar seek = new SeekBar(getActivity());
                seek.setMax(20);
                seek.setProgress(seekprogress);
                seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {


                        Log.d("seekbar", String.valueOf(progress));
                        for (int i = 0; i < textArray.size(); i++) {
                            try {
                                textArray.get(i).setTextSize(progress+14);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        seekprogress = progress;
                    }

                });
                popDialog.setView(seek);
                popDialog.show();
            }});
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void genrateTableRow(JSONObject jsonObject) {
        if ((boolean) jsonObject.get("status")) {

            JSONArray jarray = (JSONArray) jsonObject.get("score_Array");
            LayoutInflater inflater = getActivity().getLayoutInflater();
            for (int i = 0; i < jarray.size(); i++) {
                JSONArray rowArray = (JSONArray) jarray.get(i);
                TableRow row = new TableRow(getActivity());
                for (int j = 0; j < rowArray.size(); j++) {
                    View tablerowItem = inflater.inflate(R.layout.tablerow_item, null);
                    TextView text = (TextView) tablerowItem.findViewById(R.id.tablerow_text);
                    textArray.add(text);
                    text.setText((String) rowArray.get(j));
                    row.addView(tablerowItem);
                }
                tableLayout.addView(row);
                title.setText((String) jsonObject.get("title"));
                remindText.setText("向右滑動以查看更多資訊");
            }
        } else {
            title.setText("此學生無記錄");
            remindText.setText("");
        }
    }

    public final byte startLoading = 0x01;
    public final byte endLoading = 0x02;
    public final byte genrateTablerRow = 0x03;

   public  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == genrateTablerRow) {
                genrateTableRow((JSONObject) msg.obj);
            } else if (msg.what == startLoading) {

            } else if (msg.what == endLoading) {

            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    public void loadScore() {
        mSwipeRefreshLayout.setRefreshing(true);
        tableLayout.removeAllViews();

    }



}
