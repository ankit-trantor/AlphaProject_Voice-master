package com.example.xjh786.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by XJH786 on 9/18/2017.
 */

public class profile extends AppCompatActivity {

    private String profileId;
    private Handler logoutHandler = new Handler();;
    private boolean enableIdentification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        AzureUsersInfo UsersInfo = new AzureUsersInfo();
        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            profileId = extras.getString("authProfileId");
            MotoUserInfo userInfo = UsersInfo.getMotoInfo(profileId);

            if (userInfo != null) {
                Button startBtn = ((Button) findViewById(R.id.StartUsing_btn));
                Button cnfigBtn = ((Button) findViewById(R.id.cnfigBtn));
                ImageView a = ((ImageView) findViewById(R.id.imageView2));
                startBtn.setOnClickListener(btnClick);
                cnfigBtn.setOnClickListener(btnClick);
                Button cnfgProfile = ((Button)findViewById(R.id.cnfigBtn));

                a.setImageResource(userInfo.getProfileImage());
                String name = userInfo.getName();
                String coreID = userInfo.getCoreId();
                String Pos = userInfo.getPosition();

                TextView username = ((TextView) findViewById(R.id.username_text));
                TextView userId = ((TextView)findViewById(R.id.usrid_text));
                TextView position = ((TextView) findViewById(R.id.position_text));
                TextView greeting = ((TextView)findViewById(R.id.greet_text));
                greeting.setText("Hello " + name + " !");
                username.setText("Name : " + name);
                userId.setText("ID : " + coreID);
                position.setText("Position : " + Pos);
                //todo: remove this line
                cnfgProfile.setVisibility(View.GONE);
            }
        }

        if (!enableIdentification) {
            logoutHandler.postDelayed(handlerCallback, 8000);
        }
    }

    @Override
    public void onBackPressed() {
        if (!enableIdentification) {
            logoutHandler.removeCallbacks(handlerCallback);
        }
        finish();
    }

    private Runnable handlerCallback = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.StartUsing_btn: {
                    Intent intent = new Intent(profile.this, MainActivity.class);
                    intent.putExtra("authProfileId", profileId);
                    startActivity(intent);
                    finish();
                    break;
                }
                case R.id.cnfigBtn: {
                    break;
                }
            }
        }
    };
}
