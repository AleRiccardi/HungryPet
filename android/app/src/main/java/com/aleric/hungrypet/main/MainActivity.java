package com.aleric.hungrypet.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.core.CoreActivity;
import com.aleric.hungrypet.wifi.WifiActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        FrameLayout btnConfigure = findViewById(R.id.btn_configure);
        btnConfigure.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToConfigure();
            }
        }));
        FrameLayout btnDashboard = findViewById(R.id.btn_dashboard);
        btnDashboard.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToDashboard();
            }
        }));

    }

    public void goToConfigure() {
        Intent intent = new Intent(this, WifiActivity.class);
        startActivity(intent);
    }

    public void goToDashboard() {
        Intent intent = new Intent(this, CoreActivity.class);
        startActivity(intent);
    }



}