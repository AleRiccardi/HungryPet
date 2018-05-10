package com.aleric.hungrypet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_welcome);

        Button btnConfigure = findViewById(R.id.btn_configure);
        btnConfigure.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configure();
            }
        }));
    }


    public void configure(){
        Intent intent = new Intent(this, WifiActivity.class);
        startActivity(intent);
    }


}