package com.aleric.hungrypet.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.data.shedule.DaySchedule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    static private String[] days = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView ltvShedule = findViewById(R.id.lsv_schedule);

        ArrayList<DaySchedule> arrayOfSchedule = new ArrayList<>();

        for(String day : days){
            DaySchedule daySchedule = new DaySchedule(day, 2);
            arrayOfSchedule.add(daySchedule);
        }
        // Create the adapter to convert the array to views
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(this, arrayOfSchedule);
        ltvShedule.setAdapter(scheduleAdapter);
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
