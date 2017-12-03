package com.example.xjh786.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by XJH786 on 10/31/2017.
 */

public class freq extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel);
        Button submitBtn = ((Button) findViewById(R.id.button2));
        submitBtn.setOnClickListener(btnClick);
    }
    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button2: {
                    Intent intent = new Intent(freq.this, selectPhrase.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    };
}
