package com.aleric.hungrypet.schedule;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TimePicker;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.database.DbScheduleManager;
import com.aleric.hungrypet._data.shedule.Schedule;
import com.aleric.hungrypet._data.station.Station;
import com.aleric.hungrypet._data.station.StationDirectory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleDayFragment extends Fragment {

    public static final String TAG = "ScheduleDyFragment";
    public ArrayList<Schedule> mSchedulesList;
    private DbScheduleManager mDbScheduleManager;
    private ScheduleDayAdapter mScheduleAdapter;
    private Station mStation;
    private Integer mWeekInt;

    private ImageView mImvBackgorund;
    public interface ActionDayListener {
        int getWeekDay();
        void createSchedule(int selectedHour, int selectedMinute);
        void updateSchedule(Schedule scheduleSelected, int selectedHour, int selectedMinute);
    }

    public interface DeleteDayListener {
        void deleteSchedule(Schedule schedule);
        void undoDeleteSchedule(Schedule schedule);
    }

    private ScheduleDayFragment.ActionDayListener mListenerAction;
    private ScheduleDayFragment.DeleteDayListener mListenerDelete;


    public ScheduleDayFragment() {
        // Required empty public constructor
    }

    public static ScheduleDayFragment newInstance() {
        return new ScheduleDayFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_day, container, false);

        mStation = StationDirectory.getInstance().getStation();
        mWeekInt = mListenerAction.getWeekDay();

        // Components of view
        final ListView ltvShedule = rootView.findViewById(R.id.lsv_schedule_day);
        FloatingActionButton fabAddSchedule = rootView.findViewById(R.id.fab_add_schedule);
        mImvBackgorund = rootView.findViewById(R.id.imv_bg_no_items);
        // Set the list of schedules
        mSchedulesList = new ArrayList<>();
        mDbScheduleManager = new DbScheduleManager(getContext());
        mScheduleAdapter = new ScheduleDayAdapter(getActivity(), this, mListenerDelete, mSchedulesList);
        ltvShedule.setAdapter(mScheduleAdapter);

        // Listener of the ListView day
        ltvShedule.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Schedule scheduleSelected = (Schedule) ltvShedule.getItemAtPosition(position);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mListenerAction.updateSchedule(scheduleSelected, selectedHour, selectedMinute);
                        refreshLsvSchedules();
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Modify Time");
                mTimePicker.show();
            }
        });
        // Listener of the FloatingActionButton
        fabAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mListenerAction.createSchedule(selectedHour, selectedMinute);
                        refreshLsvSchedules();
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        refreshLsvSchedules();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLsvSchedules();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ScheduleDayFragment.ActionDayListener) {
            mListenerAction = (ScheduleDayFragment.ActionDayListener) context;
        } else {
            Log.e(TAG, "ActionDayListener not set");
        }

        if (context instanceof ScheduleDayFragment.DeleteDayListener) {
            mListenerDelete = (ScheduleDayFragment.DeleteDayListener) context;
        } else {
            Log.e(TAG, "DeleteDayListener not set");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListenerAction = null;
        mListenerDelete = null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(getActivity());
                if (NavUtils.shouldUpRecreateTask(getActivity(), upIntent)) {
                    // activity is NOT part of app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(getActivity())
                            // Add all of activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // activity is part of app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(getActivity(), upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshLsvSchedules() {
        mSchedulesList = new ArrayList<>();
        List<Schedule> schedules = mDbScheduleManager.getSchedules(mStation.getMac());
        for (Schedule schedule : schedules) {
            if (schedule.isAvailable() && schedule.getWeekDay() == mWeekInt) {
                mSchedulesList.add(schedule);
            }
        }
        mScheduleAdapter.clear();
        if(mSchedulesList.size() > 0) {
            mImvBackgorund.setVisibility(View.INVISIBLE);
            mScheduleAdapter.addAll(mSchedulesList);
        } else {
            mImvBackgorund.setVisibility(View.VISIBLE);
        }
    }
}
