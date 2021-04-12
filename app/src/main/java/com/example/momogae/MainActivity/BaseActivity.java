package com.example.momogae.MainActivity;

import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.momogae.Login.SharedPreference;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return SharedPreference.getAttribute(getApplicationContext(), "userID");
    }


}