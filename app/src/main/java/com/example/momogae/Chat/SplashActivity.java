package com.example.momogae.Chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.momogae.R;
import com.example.momogae.Login.LoginActivity;
import com.example.momogae.Login.SharedPreference;

public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //FirebaseAuth.getInstance().signOut();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = null;
                if ( SharedPreference.getAttribute(getApplicationContext(),"userID")==null) {
                    mainIntent = new Intent(com.example.momogae.Chat.SplashActivity.this, LoginActivity.class);
                } else {
                    mainIntent = new Intent(com.example.momogae.Chat.SplashActivity.this, com.example.momogae.Chat.FriendActivity.class);
                }
                com.example.momogae.Chat.SplashActivity.this.startActivity(mainIntent);
                com.example.momogae.Chat.SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}