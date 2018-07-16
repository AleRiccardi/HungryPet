package com.aleric.hungrypet.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;

import com.aleric.hungrypet.DownloadListener;
import com.aleric.hungrypet.R;

public class ScheduleActivity extends AppCompatActivity implements ScheduleFragment.ListListener, DownloadListener {

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
