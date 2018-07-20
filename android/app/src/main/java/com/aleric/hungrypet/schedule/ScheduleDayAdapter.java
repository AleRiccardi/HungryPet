package com.aleric.hungrypet.schedule;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.shedule.Schedule;

import java.util.ArrayList;

public class ScheduleDayAdapter extends ArrayAdapter<Schedule> {
    private Context mContext;
    private ScheduleDayFragment mFragment;
    private ScheduleDayFragment.DeleteDayListener mListener;
    private static Schedule mSchedule;


    public ScheduleDayAdapter(Activity context, ScheduleDayFragment fragment,
                              ScheduleDayFragment.DeleteDayListener listener,
                              ArrayList<Schedule> schedules) {
        super(context, 0, schedules);
        mContext = context;
        mFragment = fragment;
        mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_schedule_day, parent, false);
        }
        mSchedule = (Schedule) getItem(position);
        // Get the data item for position

        if(mSchedule == null){
            return convertView;
        }

        String hourAndMinutes = Schedule.createStringHour(mSchedule.getHour());
        // Check if an existing view is being reused, otherwise inflate the view
        TextView txvHour = (TextView) convertView.findViewById(R.id.txv_hour);
        txvHour.setText(hourAndMinutes);
        // Delete button (Image View) holder
        ItemDayHolder holder;
        holder = new ItemDayHolder();
        holder.imvDelete = (ImageView) convertView.findViewById(R.id.imv_delete);
        holder.imvDelete.setTag(holder);
        convertView.setTag(holder);
        holder.imvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.deleteSchedule(mSchedule);
                mFragment.refreshLsvSchedules(); // Refresh VIEW
                Snackbar mySnackbar = Snackbar.make(v, "Deleted schedule", Snackbar.LENGTH_LONG);
                mySnackbar.setAction("undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.undoDeleteSchedule(mSchedule);
                        mFragment.refreshLsvSchedules(); // Refresh VIEW
                    }
                });
                mySnackbar.setActionTextColor(ContextCompat.getColor(mContext, R.color.bootstrapGray));
                mySnackbar.show();
            }
        });

        return convertView;
    }

    static class ItemDayHolder {
        public ImageView imvDelete;
    }
}