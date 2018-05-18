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

/**
 * @todo fix views mix
 */
public class WifiActivity extends AppCompatActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    public static final String TAG = "WifiActivity";

    public WifiPresenter mWifiPresenter;

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
                (WifiFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (wifiFragment == null) {
            // Create the fragment
            wifiFragment = WifiFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), wifiFragment, R.id.contentFrame);
        }

        // Create the presenter
        mWifiPresenter = new WifiPresenter(wifiFragment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi, menu);
        return true;
    }

}