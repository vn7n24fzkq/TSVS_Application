package vn7.tsvsapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import vn7.tsvsapplication.back_end.TSVSparser;
import vn7.tsvsapplication.back_end.UserData;
import vn7.tsvsapplication.base.ViewPageFragment;


public class PastYearsResultsFragment extends ViewPageFragment {


    private Spinner gradeSelect;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }
    @Override
    public void init(){
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red,R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Bundle faBundle = new Bundle();
                faBundle.putString("action","onRefresh");
                mFirebaseAnalytics.logEvent("pastYearsResults", faBundle);
                loadScore(gradeSelect.getSelectedItemPosition());
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.select_grade, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSelect.setAdapter(adapter);
        gradeSelect.setSelection(UserData.getGrade()-1);
       //loadScore(gradeSelect.getSelectedItemPosition());
        gradeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bundle faBundle = new Bundle();
                faBundle.putString(FirebaseAnalytics.Param.VALUE,String.valueOf(position));
                mFirebaseAnalytics.logEvent("pastYearsResults", faBundle);
                loadScore(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_past_years_results, container, false);
        tableLayout = (TableLayout) v.findViewById(R.id.MidtermResultsTable);
        remindText = (TextView) v.findViewById(R.id.remindText);
        title = (TextView) v.findViewById(R.id.result_title);
        gradeSelect = (Spinner) v.findViewById(R.id.grade_select);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);
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

    private void loadScore(final int grade) {
        mSwipeRefreshLayout.setRefreshing(true);
        tableLayout.removeAllViews();
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONObject obj = TSVSparser.getStu_PastYearScore(String.valueOf(grade+1));//+1才不會從0年級開始
                    Message message;
                    message = mHandler.obtainMessage(genrateTablerRow, obj);
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message;
                    message = mHandler.obtainMessage(endLoading);
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    public void genrateTableRow(JSONObject jsonObject) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if ((boolean) jsonObject.get("status")) {

            JSONArray jarray = (JSONArray) jsonObject.get("score_Array");

            genrateTableTitle(inflater);
            //------------------------------------
            for (int i = 0; i < jarray.size(); i++) {
                JSONArray rowArray = (JSONArray) jarray.get(i);
                TableRow row = new TableRow(getActivity());
                for (int j = 0; j < rowArray.size(); j++) {
                    View tablerowItem = inflater.inflate(R.layout.tablerow_item, null);
                    TextView text = (TextView) tablerowItem.findViewById(R.id.tablerow_text);
                    text.setText((String) rowArray.get(j));
                    textArray.add(text);
                    row.addView(tablerowItem);
                }

                tableLayout.addView(row);
                title.setText((String) jsonObject.get("title"));
                remindText.setText("向右滑動以查看更多資訊");
            }
        } else {
            title.setText("此學年無成績記錄");;
            remindText.setText("");
        }
    }
    private void genrateTableTitle( LayoutInflater inflater){
        String[] resultArray = {"類別","學分","成績","補考","重修"};
        View tablerowItem = inflater.inflate(R.layout.tablerow_item, null);
        TextView text = (TextView) tablerowItem.findViewById(R.id.tablerow_text);
        TableRow row = new TableRow(getActivity());
        //產生固定表格------------------------
        text.setText("");
        row.addView(tablerowItem);
        TableRow.LayoutParams view = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        view.span = 5;
        tablerowItem = inflater.inflate(R.layout.tablerow_item, null);
        text = (TextView) tablerowItem.findViewById(R.id.tablerow_text);
        text.setText("上學期");
        textArray.add(text);
        row.addView(tablerowItem,view);
        tablerowItem = inflater.inflate(R.layout.tablerow_item, null);
        text = (TextView) tablerowItem.findViewById(R.id.tablerow_text);
        text.setText("下學期");
        textArray.add(text);
        row.addView(tablerowItem,view);
        tableLayout.addView(row);

        row = new TableRow(getActivity());
        tablerowItem = inflater.inflate(R.layout.tablerow_item, null);
        text = (TextView) tablerowItem.findViewById(R.id.tablerow_text);
        text.setText("課程名稱");
        textArray.add(text);
        row.addView(tablerowItem);
        for(int i = 0;i < 2;i++){
            for(int j = 0;j < resultArray.length;j++){
                tablerowItem = inflater.inflate(R.layout.tablerow_item, null);
                text = (TextView) tablerowItem.findViewById(R.id.tablerow_text);
                text.setText(resultArray[j]);
                textArray.add(text);
                row.addView(tablerowItem);
            }
        }
        tableLayout.addView(row);
    }

}
