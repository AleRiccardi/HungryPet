package com.aleric.hungrypet.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.overview.OverviewActivity;
import com.aleric.hungrypet.data.Station;
import com.aleric.hungrypet.data.StationDirectory;
import com.aleric.hungrypet.data.database.DbStationManager;
import com.aleric.hungrypet.wifi.WifiActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Station mStation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If the station is already set, then start the Overview activity.
        if (existStation()) {
            StationDirectory.getInstance().setStation(mStation);
            goToOverview();
        } else {
            // otherwise show the main activity
            Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(myToolbar);

            FrameLayout btnConfigure = findViewById(R.id.btn_configure);
            btnConfigure.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToWifi();
                }
            }));
            FrameLayout btnDashboard = findViewById(R.id.btn_dashboard);
            btnDashboard.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToOverview();
                }
            }));
        }
    }

    /**
     * Redirect to Wifi activity.
     */
    public void goToWifi() {
        Intent intent = new Intent(this, WifiActivity.class);
        startActivity(intent);
    }

    /**
     * Redirect to Core activity.
     */
    public void goToOverview() {
        Intent intent = new Intent(this, OverviewActivity.class);
        startActivity(intent);
    }


    /**
     * Check if exist in the database a recent station.
     * @return true if exist, false otherwise.
     */
    public boolean existStation() {
        DbStationManager dbStationManager = new DbStationManager(this);
        List<Station> stations = dbStationManager.getStations();
        for (Station station : stations) {
            if (mStation == null) {
                mStation = station;
            } else {
                if (mStation.getUpdate().compareTo(station.getUpdate()) < 0) {
                    mStation = station;
                }
            }
        }
        return mStation != null;
    }
}