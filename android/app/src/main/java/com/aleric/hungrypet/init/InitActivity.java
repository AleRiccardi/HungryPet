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
import com.aleric.hungrypet.core.CoreActivity;
import com.aleric.hungrypet.data.Device;
import com.aleric.hungrypet.data.DeviceDirectory;
import com.aleric.hungrypet.data.database.DbDeviceManager;

public class InitActivity extends AppCompatActivity{
    private DbDeviceManager mDbManager;
    private Device mDevice;
    private EditText edtName;
    private TextView txvIp;
    private TextView txvMac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mDbManager = new DbDeviceManager(this);
        mDevice = DeviceDirectory.getInstance().getDevice();

        edtName = findViewById(R.id.edt_name);
        txvIp = findViewById(R.id.txv_ip_address);
        txvMac = findViewById(R.id.txv_mac_address);
        Button btnFinish = findViewById(R.id.btn_finish);

        txvIp.setText(mDevice.getIp());
        txvMac.setText(mDevice.getMac());

        btnFinish.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                if(name.length() > 3) {
                    mDevice.setName(name);
                    DeviceDirectory.getInstance().setDevice(mDevice);
                    mDbManager.addDevice(mDevice);

                    goToDashboard();
                } else {
                    edtName.setError("Minimum 4 characters");
                }
            }
        }));

    }

    public void goToDashboard() {
        Intent intent = new Intent(this, CoreActivity.class);
        startActivity(intent);
    }
}
