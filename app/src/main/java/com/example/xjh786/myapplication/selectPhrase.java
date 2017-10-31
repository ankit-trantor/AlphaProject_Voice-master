package com.example.xjh786.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by XJH786 on 10/31/2017.
 */

public class selectPhrase extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecting_phrase);
        Button phrase1 = ((Button) findViewById(R.id.phrase1));
        phrase1.setOnClickListener(btnClick);
        Button phrase2 = ((Button) findViewById(R.id.phrase2));
        phrase2.setOnClickListener(btnClick);
        Button phrase3 = ((Button) findViewById(R.id.phrase3));
        phrase3.setOnClickListener(btnClick);
        Button phrase4 = ((Button) findViewById(R.id.phrase4));
        phrase4.setOnClickListener(btnClick);
        Button phrase5 = ((Button) findViewById(R.id.phrase5));
        phrase5.setOnClickListener(btnClick);
        Button phrase6 = ((Button) findViewById(R.id.phrase6));
        phrase6.setOnClickListener(btnClick);
        Button phrase7 = ((Button) findViewById(R.id.phrase7));
        phrase7.setOnClickListener(btnClick);
    };

    String passphrase ;
    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.phrase1: {
                    passphrase = "My voice is stronger than password";
                    break;
                }
                case R.id.phrase2: {
                    passphrase = "My voice is my passport verify me";
                    break;
                }
                case R.id.phrase3: {
                    passphrase = "My password is not your business";
                    break;
                }
                case R.id.phrase4: {
                    passphrase = "My name is unknown to you";
                    break;
                }
                case R.id.phrase5: {
                    passphrase = "Im going to make him an offer that he cannot refuse";
                    break;
                }
                case R.id.phrase6: {
                    passphrase = "Houston we have a problem";
                    break;
                }
                case R.id.phrase7: {
                    passphrase = "Apple juice tastes funny after toothpaste";
                    break;
                }

            }
            Intent intent = new Intent(selectPhrase.this, phraseActivity.class);
            intent.putExtra("Phrase", passphrase);
            startActivity(intent);
        }

    };
}
