package com.aleric.hungrypet.schedule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class ScheduleActivity extends AppCompatActivity implements ScheduleFragment.ListListener, DownloadFragment.DownloadListener {

    static final public String INTENT_KEY = "day";
    Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addFragment(DownloadFragment.newInstance(), false);
    }


    @Override
    public void onFinishDownload() {
        replaceFragment(ScheduleFragment.newInstance(), false);
    }

    @Override
    public void onClickGoToDay(Pair<String, Integer> day) {
        Intent intent = new Intent(this, ScheduleDayActivity.class);
        intent.putExtra(INTENT_KEY, (String) day.first);
        startActivity(intent);
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
