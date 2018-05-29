package com.aleric.hungrypet.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.data.shedule.DaySchedule;
import com.aleric.hungrypet.data.wifi.WifiCell;

import java.util.ArrayList;

public class ScheduleAdapter extends ArrayAdapter<DaySchedule> {
    // View lookup cache
    private static class ViewHolder {
        TextView txvSsid;
        TextView txvEncryption;
    }


    public ScheduleAdapter(Context context, ArrayList<DaySchedule> days) {
        super(context, 0, days);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DaySchedule daySchedule = (DaySchedule) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_schedule, parent, false);
        }
        // Lookup view for data population
        TextView txvDay = (TextView) convertView.findViewById(R.id.txv_day);
        TextView txvBadge = (TextView) convertView.findViewById(R.id.txv_badge);
        // Populate the data into the template view using the data object
        int num = daySchedule.getNumSchedule();
        txvDay.setText(daySchedule.getDayName());
        //txvBadge.setText(daySchedule.getNumSchedule());
        // Return the completed view to render on screen
        return convertView;
    }
}