package com.example.xjh786.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by XJH786 on 9/18/2017.
 */

public class profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        Button startBtn = ((Button) findViewById(R.id.StartUsing_btn));
        Button cnfigBtn = ((Button) findViewById(R.id.cnfigBtn));
        ImageView a = ((ImageView) findViewById(R.id.imageView2));
        startBtn.setOnClickListener(btnClick);
        cnfigBtn.setOnClickListener(btnClick);
        TextView username = ((TextView) findViewById(R.id.username_text));
        TextView userId = ((TextView)findViewById(R.id.usrid_text));
        TextView position = ((TextView) findViewById(R.id.position_text));
        TextView greeting = ((TextView)findViewById(R.id.greet_text));
        Button cnfgProfile = ((Button)findViewById(R.id.cnfigBtn));

        a.setImageResource(R.mipmap.ic_amir);
        String name = "Amir";
        String coreID = "XJH786";
        String Pos = "ISP";
        /*todo: add switch case that change the name variable*/
        greeting.setText("Hello "+name);
        username.setText("Name : "+name);
        userId.setText("ID : "+coreID);
        position.setText("Position : "+Pos);
        //todo: remove this line
        cnfgProfile.setVisibility(View.GONE);
    }
    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.StartUsing_btn: {
                    Intent intent = new Intent(profile.this, MainActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.cnfigBtn: {
                    break;
                }
            }
        }
    };
}
