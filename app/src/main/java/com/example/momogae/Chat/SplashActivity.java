package com.example.momogae.Chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.momogae.Login.LoginActivity;
import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;

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
                    mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                } else {
                    mainIntent = new Intent(SplashActivity.this, ChatFragmentPagerAdapter.class);
                }
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}