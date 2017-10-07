package com.example.xjh786.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by XJH786 on 9/18/2017.
 */

public class LogOrReg extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logncreate);
        Button btnLogin = ((Button) findViewById(R.id.loginBtn));
        Button regBtn = ((Button) findViewById(R.id.regBtn));
        btnLogin.setOnClickListener(btnClick);
        regBtn.setOnClickListener(btnClick);
    }
    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginBtn: {
                    Intent intent = new Intent(LogOrReg.this, firstPage.class);
                    startActivity(intent);
                    break;
                }
                case R.id.regBtn: {
                    Intent intent = new Intent(LogOrReg.this, regActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    };
}