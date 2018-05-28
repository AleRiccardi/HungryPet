package com.aleric.hungrypet.overview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.data.Schedule;
import com.aleric.hungrypet.data.Station;
import com.aleric.hungrypet.data.StationDirectory;
import com.aleric.hungrypet.schedule.ScheduleActivity;
import com.aleric.hungrypet.wifi.WifiActivity;

public class OverviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
        //add this line to display menu1 when the activity is loaded
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
                Intent intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_station:
                break;
            case R.id.nav_settings:
                break;
        }
    }

}
