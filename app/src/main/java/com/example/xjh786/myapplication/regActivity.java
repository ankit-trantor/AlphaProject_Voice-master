package com.example.xjh786.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by XJH786 on 9/18/2017.
 */

public class regActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_layout);
        Button submitBtn = ((Button) findViewById(R.id.submitBtn));
        submitBtn.setOnClickListener(btnClick);
    }
    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.submitBtn: {
                    Intent intent = new Intent(regActivity.this, phraseActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    };
}
