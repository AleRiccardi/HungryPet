package com.aleric.hungrypet.schedule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.database.DbScheduleManager;
import com.aleric.hungrypet._data.shedule.Schedule;
import com.aleric.hungrypet._data.station.Station;
import com.aleric.hungrypet._data.station.StationDirectory;
import com.aleric.hungrypet.main.MainActivity;
import com.aleric.hungrypet.overview.OverviewActivity;
import com.aleric.hungrypet.settings.SettingsActivity;
import com.aleric.hungrypet.wifi.WifiActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    static final public String INTENT_KEY = "day";
    Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ListView ltvShedule = findViewById(R.id.lsv_schedule);

        mContext = this;
        // Set the name and the number of schedule
        Station station = StationDirectory.getInstance().getStation();
        if (station != null) {
            DbScheduleManager dbSchedule = new DbScheduleManager(this);

            ArrayList<Pair<String, Integer>> arrayOfSchedule = new ArrayList<>();
            List<Schedule> schedules = dbSchedule.getSchedules(station.getMac());
            for (String day : Schedule.WEEK_DAYS) {
                // Count number of schedule
                int numSchedule = 0;
                int weekInt = Arrays.asList(Schedule.WEEK_DAYS).indexOf(day);
                for (Schedule schedule : schedules) {
                    if (schedule.getWeekDay() == weekInt) {
                        numSchedule++;
                    }
                }
                Pair<String, Integer> daySchedule = new Pair<>(day, numSchedule);
                arrayOfSchedule.add(daySchedule);
            }
            // Create the adapter to convert the array to views
            ScheduleAdapter scheduleAdapter = new ScheduleAdapter(this, arrayOfSchedule);
            ltvShedule.setAdapter(scheduleAdapter);
            // Listener of the ListView day
            ltvShedule.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Pair<String, Integer> day = (Pair<String, Integer>) ltvShedule.getItemAtPosition(position);
                    goToDay(day);
                }
            });
        } else {
            AlertDialog.Builder alertB = new AlertDialog.Builder(this);
            alertB.setTitle("Station error");
            alertB.setMessage("Sorry, but there's not station set, do you want to set a new one?");
            alertB.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intentMain = new Intent(mContext, MainActivity.class);
                    intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentMain);
                }
            });
            alertB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intentWifi = new Intent(mContext, OverviewActivity.class);
                    intentWifi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentWifi);
                }
            });
            alertB.show();
        }
    }

    /**
     * Redirect to Wifi activity.
     */
    public void goToDay(Pair<String, Integer> day) {
        Intent intent = new Intent(this, ScheduleDayActivity.class);
        intent.putExtra(INTENT_KEY, (String) day.first);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
