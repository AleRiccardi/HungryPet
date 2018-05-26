package com.aleric.hungrypet.wifi;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.main.MainActivity;
import com.aleric.hungrypet.util.ActivityUtils;

public class WifiActivity extends AppCompatActivity {

    public static final String TAG = "WifiActivity";

    public static WifiPresenter mWifiPresenter;

    private boolean isNewActivity;


    public WifiActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Select WIFI");
        setSupportActionBar(myToolbar);

        WifiFragment wifiFragment =
                (WifiFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_cont_wifi);
        if (wifiFragment == null) {
            // Create the fragment
            wifiFragment = WifiFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), wifiFragment, R.id.fragment_cont_wifi);
        }

        // Check if it's a new view
        isNewActivity = (savedInstanceState == null);
        // Create the presenter

        mWifiPresenter = new WifiPresenter(wifiFragment, isNewActivity);

        isNewActivity = false;

    }

    // Save UI state changes to the savedInstanceState.
    // This bundle will be passed to onCreate if the process is
    // killed and restarted.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi, menu);
        return true;
    }

}