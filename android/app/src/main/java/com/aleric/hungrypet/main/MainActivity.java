package com.aleric.hungrypet.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet.wifi.WifiActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Button btnConfigure = findViewById(R.id.btn_configure);
        btnConfigure.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configure();
            }
        }));
    }


    public void configure() {
        Intent intent = new Intent(this, WifiActivity.class);
        startActivity(intent);
    }


}