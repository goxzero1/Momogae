package com.example.momogae.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.momogae.Board.BoardActivity;
import com.example.momogae.Chat.ChatActivity;
import com.example.momogae.Classification.ClassificationActivity;
import com.example.momogae.Diary.DiaryActivity;
import com.example.momogae.R;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button imageButton1 = (Button) findViewById(R.id.butt1);
        Button imageButton2 = (Button) findViewById(R.id.butt2);
        Button imageButton3 = (Button) findViewById(R.id.butt3);
        Button imageButton4 = (Button) findViewById(R.id.butt4);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
                startActivity(intent);
            }

        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
            }

        });

        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
            }

        });

        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ClassificationActivity.class);
                startActivity(intent);
            }

        });
    }
}