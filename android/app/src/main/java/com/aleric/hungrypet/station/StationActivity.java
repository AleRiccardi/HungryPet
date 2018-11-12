package com.aleric.hungrypet.station;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.database.DbStationManager;
import com.aleric.hungrypet._data.station.Station;
import com.aleric.hungrypet._data.station.StationDirectory;
import com.aleric.hungrypet.main.MainActivity;
import com.aleric.hungrypet.overview.OverviewActivity;
import com.aleric.hungrypet.settings.SettingsActivity;

public class StationActivity extends AppCompatActivity {

    private TextView txvStationName;
    private TextView txvMac;
    private TextView txvIp;
    private TextView txvUpdate;
    private Button btnDisconnect;

    private Station mStation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // activity is NOT part of app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // activity is part of app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        final Activity context = this;
        mStation = StationDirectory.getInstance().getStation();
        if (mStation != null) {
            txvStationName = findViewById(R.id.txv_station_name);
            txvMac = findViewById(R.id.txv_mac_address);
            txvIp = findViewById(R.id.txv_ip_address);
            txvUpdate = findViewById(R.id.txv_update);
            btnDisconnect = findViewById(R.id.btn_disconnect);

            txvStationName.setText(mStation.getName());
            txvMac.setText(mStation.getMac());
            txvIp.setText(mStation.getIp());
            txvUpdate.setText(mStation.getDateUpdate().toString());

            btnDisconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DbStationManager dbStationManager = new DbStationManager(context);
                    dbStationManager.deleteStation(mStation);
                    StationDirectory.getInstance().setStation(null);

                    Intent intentMain = new Intent(context, MainActivity.class);
                    startActivity(intentMain);
                }
            });
        } else {
            AlertDialog.Builder alertB = new AlertDialog.Builder(this);
            alertB.setTitle("Station error");
            alertB.setMessage("Sorry, but there's not station set, do you want to set a new one?");
            alertB.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intentMain = new Intent(context, MainActivity.class);
                    intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentMain);
                }
            });
            alertB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intentWifi = new Intent(context, OverviewActivity.class);
                    intentWifi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentWifi);
                }
            });
            alertB.show();
        }

    }
}
