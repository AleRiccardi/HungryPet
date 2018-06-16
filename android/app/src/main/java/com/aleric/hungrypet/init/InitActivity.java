package com.aleric.hungrypet.init;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.data.station.Station;
import com.aleric.hungrypet.overview.OverviewActivity;
import com.aleric.hungrypet.data.station.StationDirectory;
import com.aleric.hungrypet.data.database.DbStationManager;

public class InitActivity extends AppCompatActivity{
    private DbStationManager mDbManager;
    private Station mStation;
    private EditText edtName;
    private TextView txvIp;
    private TextView txvMac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mDbManager = new DbStationManager(this);
        mStation = StationDirectory.getInstance().getStation();

        edtName = findViewById(R.id.edt_name);
        txvIp = findViewById(R.id.txv_ip_address);
        txvMac = findViewById(R.id.txv_mac_address);
        Button btnFinish = findViewById(R.id.btn_finish);

        txvIp.setText(mStation.getIp());
        txvMac.setText(mStation.getMac());

        btnFinish.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configure();
            }
        }));
    }

    public void goToDashboard() {
        Intent intent = new Intent(this, OverviewActivity.class);
        startActivity(intent);
    }

    public void configure(){
        String name = edtName.getText().toString();
        if(name.length() > 3) {
            mStation.setName(name);
            StationDirectory.getInstance().setStation(mStation);
            mDbManager.addStation(mStation);

            goToDashboard();
        } else {
            edtName.setError("Minimum 4 characters");
        }
    }
}
