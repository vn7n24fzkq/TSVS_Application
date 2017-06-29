package vn7.tsvsapplication;


import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import vn7.tsvsapplication.back_end.TSVSparser;
import vn7.tsvsapplication.base.ViewPageFragment;


public class MidtermResultsFragment extends ViewPageFragment {
    @Override
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
                    text.setText((String) rowArray.get(j));
                    textArray.add(text);
                    row.addView(tablerowItem);
                }
                tableLayout.addView(row);
                title.setText((String) jsonObject.get("title"));
                remindText.setText("向右滑動以查看更多資訊");
            }
        } else {
            title.setText("此學生無成績記錄");
            remindText.setText("");
        }
    }
    @Override
   public void loadScore() {
        mSwipeRefreshLayout.setRefreshing(true);
        tableLayout.removeAllViews();
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONObject obj = TSVSparser.getStu_Score();
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

}
