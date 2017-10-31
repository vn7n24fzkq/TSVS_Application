package vn7.tsvsapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import vn7.tsvsapplication.base.CalendarItem;
import vn7.tsvsapplication.R;

/**
 * Created by casper on 2017/5/16.
 */
public class CalendarListAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    private List<CalendarItem> items;

    public CalendarListAdapter(Context context, List<CalendarItem> items) {
        this.items = items;
        myInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.indexOf(getItem(position));
    }

    private class ViewHolder {
        TextView date;
        TextView schedule;
        TextView department;
        View colorTag;

        public ViewHolder(TextView date, TextView schedule, TextView department, View colorTag) {
            this.date = date;
            this.schedule = schedule;
            this.department = department;
            this.colorTag = colorTag;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.calendar_item, null);
            holder = new ViewHolder((TextView) convertView.findViewById(R.id.date),
                    (TextView) convertView.findViewById(R.id.schedule),
                    (TextView) convertView.findViewById(R.id.department), convertView.findViewById(R.id.color_tag));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CalendarItem item = (CalendarItem) getItem(position);
        holder.date.setText(item.getDate());
        holder.schedule.setText(item.getSchedule());
        holder.department.setText(item.getDepartment());
        //set tag color
        try {
            int date = Integer.valueOf(item.getDate().substring(item.getDate().indexOf("月") + 1, item.getDate().indexOf("日")));
            int[] rainbow = convertView.getResources().getIntArray(R.array.color_circle);
            holder.colorTag.setBackgroundColor(rainbow[date % rainbow.length]);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return convertView;
    }
}
