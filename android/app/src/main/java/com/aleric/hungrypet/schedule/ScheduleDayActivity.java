package com.aleric.hungrypet.schedule;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.database.DbScheduleManager;
import com.aleric.hungrypet._data.shedule.Schedule;
import com.aleric.hungrypet._data.station.Station;
import com.aleric.hungrypet._data.station.StationDirectory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ScheduleDayActivity extends AppCompatActivity {

    private static String TAG = "schedule-day-activity";
    private static final int LONG_DELAY = 3500; // 3.5 seconds

    public ArrayList<Schedule> mSchedulesList;
    private DbScheduleManager mDbScheduleManager;
    private ScheduleDayAdapter mScheduleAdapter;
    private Station mStation;
    private String mWeekString;
    private Integer mWeekInt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_day);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        String day = getIntent().getExtras().getString(ScheduleActivity.INTENT_KEY);
        if (day != null) {
            mStation = StationDirectory.getInstance().getStation();
            mWeekString = day;
            mWeekInt = Arrays.asList(Schedule.WEEK_DAYS).indexOf(mWeekString);

            // Set the title
            actionBar.setTitle(mWeekString);
            // Components of view
            final ListView ltvShedule = findViewById(R.id.lsv_schedule_day);
            FloatingActionButton fabAddSchedule = findViewById(R.id.fab_add_schedule);
            // Set the list of schedules
            mSchedulesList = new ArrayList<>();
            mDbScheduleManager = new DbScheduleManager(this);
            mScheduleAdapter = new ScheduleDayAdapter(this, mSchedulesList);
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
                    mTimePicker = new TimePickerDialog(ScheduleDayActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            updateSchedule(scheduleSelected, selectedHour, selectedMinute);
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
                    mTimePicker = new TimePickerDialog(ScheduleDayActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            createSchedule(selectedHour, selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });
            refreshLsvSchedules();
        } else {
            actionBar.setTitle("No day");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // activity is NOT part of app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // activity is part of app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshLsvSchedules() {
        mSchedulesList = new ArrayList<>();
        List<Schedule> schedules = mDbScheduleManager.getSchedules(mStation.getMac());
        for (Schedule schedule : schedules) {
            if (schedule.isAvailable() && schedule.getWeekDay() == mWeekInt) {
                mSchedulesList.add(schedule);
            }
        }
        mScheduleAdapter.clear();
        mScheduleAdapter.addAll(mSchedulesList);
    }

    private void createSchedule(int selectedHour, int selectedMinute) {
        Integer dayNum = Arrays.asList(Schedule.WEEK_DAYS).indexOf(mWeekString);
        int hourInt = selectedHour * 100 + selectedMinute;
        String hourString = Schedule.createStringHour(hourInt);
        Toast.makeText(this, "Added schedule at " + hourString, Toast.LENGTH_LONG).show();
        Schedule schedule = new Schedule(mStation.getMac(), dayNum, hourInt);
        mDbScheduleManager.addSchedule(schedule);
        refreshLsvSchedules();
    }

    public void updateSchedule(Schedule schedule, int selectedHour, int selectedMinute) {
        Integer dayNum = Arrays.asList(Schedule.WEEK_DAYS).indexOf(mWeekString);
        int hourInt = selectedHour * 100 + selectedMinute;
        String hourString = Schedule.createStringHour(hourInt);
        // Updating MODEL
        schedule.setHour(hourInt); // Update hour
        schedule.setUpdate(); // Update date
        mDbScheduleManager.updateSchedule(schedule);
        // Updating VIEW
        Toast.makeText(this, "Updated schedule at " + hourString, Toast.LENGTH_LONG).show();
        refreshLsvSchedules();
    }

    public void deleteSchedule(Schedule schedule) {
        schedule.delete(true);
        mDbScheduleManager.updateSchedule(schedule);
        refreshLsvSchedules();
    }

    public void undoDeleteSchedule(Schedule schedule) {
        schedule.delete(false);
        mDbScheduleManager.updateSchedule(schedule);
        refreshLsvSchedules();
    }


}
