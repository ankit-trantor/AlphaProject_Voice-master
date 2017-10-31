package com.example.xjh786.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by XJH786 on 9/18/2017.
 */

public class phraseActivity extends AppCompatActivity {
    int number = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phrase_selector);
        Bundle extras = getIntent().getExtras();
        String firstword;
        if(extras != null) {
            TextView first = ((TextView) findViewById(R.id.textView5));
            firstword = extras.getString("Phrase");
            first.setText("Speak \"" +firstword + "\"");

        }
        Button submitBtn = ((Button) findViewById(R.id.button));
        submitBtn.setOnClickListener(btnClick);
    }
    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button: {
                    if(number == 0)
                    {
                        TextView a = ((TextView) findViewById(R.id.textView2));
                        a.setVisibility(View.VISIBLE);
                        number++;
                    }
                    else if(number==1)
                    {
                        TextView c = ((TextView) findViewById(R.id.textView3));
                        c.setVisibility(View.VISIBLE);
                        number++;
                    }
                    else if(number == 2) {
                        TextView b = ((TextView) findViewById(R.id.textView9));
                        ((Button) findViewById(R.id.button)).setText("NEXT");
                        b.setVisibility(View.VISIBLE);
                        number++;
                    }
                    else
                    {
                        number = 0;
                        Intent intent = new Intent(phraseActivity.this, succss.class);
                        startActivity(intent);
                        finish();
                    }

                    break;
                }
            }
        }
    };
}