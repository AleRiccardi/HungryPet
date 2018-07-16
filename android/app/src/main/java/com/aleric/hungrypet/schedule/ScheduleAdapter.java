package com.aleric.hungrypet.schedule;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aleric.hungrypet.R;

import java.util.ArrayList;

public class ScheduleAdapter extends ArrayAdapter<Pair<String, Integer>> {

    public ScheduleAdapter(Context context, ArrayList<Pair<String, Integer>> days) {
        super(context, 0, days);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for position
        Pair<String, Integer> daySchedule = (Pair<String, Integer>) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_schedule, parent, false);
        }
        TextView txvDay = (TextView) convertView.findViewById(R.id.txv_day);
        TextView txvBadge = (TextView) convertView.findViewById(R.id.txv_badge);
        txvDay.setText(daySchedule.first);
        txvBadge.setText(daySchedule.second.toString());
        return convertView;
    }
}