package com.example.momogae.Chat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    private Util() {
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE); //키보드 보이기&숨기기
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getUniqueValue() { //스토리지 사진 업데이트(이름설정) 항목
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        return ft.format(new Date()) + (int) (Math.random()*10);
    }

    public static String size2String(Long filesize) { //파이어스토어 사진 업데이트 항목
        Integer unit = 1024;
        if (filesize < unit){
            return String.format("%d bytes", filesize);
        }

        int exp = (int) (Math.log(filesize) / Math.log(unit));

        return String.format("%.0f %sbytes", filesize / Math.pow(unit, exp), "KMGTPE".charAt(exp-1));
    }

    public static String getRootPath() {
        String sdPath;
        String ext1 = Environment.getExternalStorageState();
        if (ext1.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            sdPath = Environment.MEDIA_UNMOUNTED;
        }
        return sdPath;
    }

    public  static boolean isPermissionGranted(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }
}