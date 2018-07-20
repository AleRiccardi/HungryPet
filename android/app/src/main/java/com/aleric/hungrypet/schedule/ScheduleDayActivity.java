package com.aleric.hungrypet.schedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.aleric.hungrypet.DownloadListener;
import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.DownloadData;
import com.aleric.hungrypet._data.database.DbScheduleManager;
import com.aleric.hungrypet._data.shedule.Schedule;
import com.aleric.hungrypet._data.station.Station;
import com.aleric.hungrypet._data.station.StationDirectory;

import java.util.Arrays;

public class ScheduleDayActivity extends AppCompatActivity implements ScheduleDayFragment.ActionDayListener,
        ScheduleDayFragment.DeleteDayListener, DownloadListener {

    private static String TAG = "schedule-day-activity";
    private static final int LONG_DELAY = 3500; // 3.5 seconds

    private Station mStation;
    private String mWeekString;
    private Integer mWeekInt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_day);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        mWeekString = getIntent().getExtras().getString(ScheduleActivity.INTENT_KEY);

        if (mWeekString != null) {
            mStation = StationDirectory.getInstance().getStation();
            mWeekInt = Arrays.asList(Schedule.WEEK_DAYS).indexOf(mWeekString);

            // Set the title
            actionBar.setTitle(mWeekString);
        } else {
            actionBar.setTitle("No day");
        }

        addFragment(DownloadFragment.newInstance(), false);
    }


    @Override
    public void onFinishDownload() {
        replaceFragment(ScheduleDayFragment.newInstance(), false);
    }

    @Override
    public int getWeekDay() {
        return mWeekInt;
    }

    @Override
    public void createSchedule(int selectedHour, int selectedMinute) {
        DbScheduleManager dbScheduleManager = new DbScheduleManager(this);
        Integer dayNum = Arrays.asList(Schedule.WEEK_DAYS).indexOf(mWeekString);
        int hourInt = selectedHour * 100 + selectedMinute;
        String hourString = Schedule.createStringHour(hourInt);
        // MODEL
        Schedule schedule = new Schedule(mStation.getMac(), dayNum, hourInt);
        Boolean inserted = dbScheduleManager.addScheduleWithCheck(schedule);
        if (inserted) {
            new DownloadData(this, null).execute(); // Trigger update thread
            // Updating VIEW
            Toast.makeText(this, "Added schedule at " + hourString, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error with the insertion of " + hourString, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void updateSchedule(Schedule schedule, int selectedHour, int selectedMinute) {
        DbScheduleManager dbScheduleManager = new DbScheduleManager(this);
        Integer dayNum = Arrays.asList(Schedule.WEEK_DAYS).indexOf(mWeekString);
        int hourInt = selectedHour * 100 + selectedMinute;
        String hourString = Schedule.createStringHour(hourInt);
        // MODEL
        schedule.setHour(hourInt); // Update hour
        schedule.setUpdate(); // Update date

        Boolean inserted = dbScheduleManager.updateSchedule(schedule);
        if (inserted) {
            new DownloadData(this, null).execute(); // Trigger update thread
            // Updating VIEW
            Toast.makeText(this, "Updated schedule at " + hourString, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error with the update of " + hourString, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void deleteSchedule(Schedule schedule) {
        DbScheduleManager dbScheduleManager = new DbScheduleManager(this);
        schedule.setDelete(true);
        schedule.setUpdate(); // Update date
        dbScheduleManager.updateSchedule(schedule);
        new DownloadData(this, null).execute(); // Trigger update thread
    }

    @Override
    public void undoDeleteSchedule(Schedule schedule) {
        DbScheduleManager dbScheduleManager = new DbScheduleManager(this);
        schedule.setDelete(false);
        schedule.setUpdate(); // Update date
        dbScheduleManager.updateSchedule(schedule);
        new DownloadData(this, null).execute(); // Trigger update thread
    }


    protected void addFragment(Fragment fragment, boolean back) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        if (back) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    protected void replaceFragment(Fragment fragment, boolean back) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (back) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
