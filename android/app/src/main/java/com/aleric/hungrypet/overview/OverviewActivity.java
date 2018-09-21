package com.aleric.hungrypet.overview;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.SynchronizeProgressBar;
import com.aleric.hungrypet._data.UploadInstantFood;
import com.aleric.hungrypet._data.station.Station;
import com.aleric.hungrypet._data.station.StationDirectory;
import com.aleric.hungrypet.schedule.ScheduleActivity;
import com.aleric.hungrypet.settings.SettingsActivity;
import com.aleric.hungrypet.station.StationActivity;

public class OverviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTxvStationName;
    private TextView mTxvLastFeedTime;
    private ProgressBar mPgbLevelContainer;
    private ProgressBar mPgbLevelBowl;
    private Button mBtnFeedNow;
    private SynchronizeProgressBar threadLevel;
    private Station mStation;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0); // 0-index header


        TextView txvNavTitle = headerLayout.findViewById(R.id.txv_nav_title);
        TextView txvNavSubtitle = headerLayout.findViewById(R.id.txv_nav_subtitle);

        Station station = StationDirectory.getInstance().getStation();
        if (station != null) {
            txvNavTitle.setText(station.getName());
            txvNavSubtitle.setText(station.getIp());
        } else {
            txvNavTitle.setText("Title");
            txvNavSubtitle.setText("Subtitle");
        }

        // Initialize the component
        this.initComponents();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mStation != null) {
            threadLevel = new SynchronizeProgressBar(this, mPgbLevelContainer, mPgbLevelBowl);
            threadLevel.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mStation != null) {
            threadLevel.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStation != null) {
            threadLevel.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_schedule:
                Intent intentSchedule = new Intent(this, ScheduleActivity.class);
                startActivity(intentSchedule);
                break;
            case R.id.nav_station:
                Intent intentStation = new Intent(this, StationActivity.class);
                startActivity(intentStation);
                break;
            case R.id.nav_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initComponents() {
        final Activity activity = this;
        mTxvStationName = findViewById(R.id.txv_station_name);
        mTxvLastFeedTime = findViewById(R.id.txv_last_feed_time);
        mPgbLevelContainer = findViewById(R.id.pgb_level_container);
        mPgbLevelBowl = findViewById(R.id.pgb_level_bowl);
        mBtnFeedNow = findViewById(R.id.btn_feed_now);

        mStation = StationDirectory.getInstance().getStation();
        if (mStation != null) {
            mTxvStationName.setText(mStation.getName());
            mTxvLastFeedTime.setText("To set");
            // BUTTON FEED NOW
            mBtnFeedNow.setEnabled(true);
            mBtnFeedNow.setClickable(true);
            mBtnFeedNow.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new UploadInstantFood(activity).execute();
                }
            }));
            // PROGRESSBAR
            mPgbLevelContainer.setProgress(0, true);
            mPgbLevelBowl.setProgress(0, true);

        } else {
            mBtnFeedNow.setEnabled(false);
            mBtnFeedNow.setClickable(false);
        }
    }

}