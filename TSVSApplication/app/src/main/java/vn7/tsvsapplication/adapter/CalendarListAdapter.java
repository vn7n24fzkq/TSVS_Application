package vn7.tsvsapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import vn7.tsvsapplication.base.CalendarItem;
import vn7.tsvsapplication.R;


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

        private ViewHolder(TextView date, TextView schedule, TextView department) {
            this.date = date;
            this.schedule = schedule;
            this.department = department;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.calendar_item,parent,false);
            holder = new ViewHolder((TextView) convertView.findViewById(R.id.date),
                    (TextView) convertView.findViewById(R.id.schedule),
                    (TextView) convertView.findViewById(R.id.department));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CalendarItem item = (CalendarItem) getItem(position);
        holder.date.setText(item.getDate());
        holder.schedule.setText(item.getSchedule());
        holder.department.setText(item.getDepartment());

        return convertView;
    }
}
