package com.aleric.hungrypet.schedule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.database.DbScheduleManager;
import com.aleric.hungrypet._data.shedule.Schedule;
import com.aleric.hungrypet._data.station.Station;
import com.aleric.hungrypet._data.station.StationDirectory;
import com.aleric.hungrypet.main.MainActivity;
import com.aleric.hungrypet.overview.OverviewActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduleFragment extends Fragment {

    static final public String INTENT_KEY = "day";

    private ListView mLtvShedule;

    private ScheduleAdapter mAdapterSchedule;

    public interface ListListener {
        void onClickGoToDay(Pair<String, Integer> day);
    }

    private ListListener listener;


    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        mLtvShedule = rootView.findViewById(R.id.lsv_schedule);
        // Set the name and the number of schedule
        Station station = StationDirectory.getInstance().getStation();
        if (station != null) {

            // Prepare adapter for list of days of week
            ArrayList<Pair<String, Integer>> arrayOfSchedule = new ArrayList<>();
            mAdapterSchedule = new ScheduleAdapter(getContext(), arrayOfSchedule);
            mLtvShedule.setAdapter(mAdapterSchedule);
            this.populateLsvDays();

            // Listener of the ListView day
            mLtvShedule.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Pair<String, Integer> day = (Pair<String, Integer>) mLtvShedule.getItemAtPosition(position);
                    listener.onClickGoToDay(day);
                }
            });
        } else {
            AlertDialog.Builder alertB = new AlertDialog.Builder(getContext());
            alertB.setTitle("Station error");
            alertB.setMessage("Sorry, but there's not station set, do you want to set a new one?");
            alertB.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intentMain = new Intent(getContext(), MainActivity.class);
                    intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentMain);
                }
            });
            alertB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intentWifi = new Intent(getContext(), OverviewActivity.class);
                    intentWifi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentWifi);
                }
            });
            alertB.show();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.populateLsvDays();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListListener) {
            listener = (ListListener) context;
        } else {
            //Mettere un log
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(getActivity());
                if (NavUtils.shouldUpRecreateTask(getActivity(), upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(getContext())
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(getActivity(), upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateLsvDays() {
        Station station = StationDirectory.getInstance().getStation();
        if (station != null) {
            mAdapterSchedule.clear();

            DbScheduleManager dbSchedule = new DbScheduleManager(getContext());
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

            mAdapterSchedule.addAll(arrayOfSchedule);
        }
    }
}
