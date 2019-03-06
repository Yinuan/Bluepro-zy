package com.klcxkj.bluepro_zy;

import android.app.Application;

import com.klcxkj.zqxy.utils.AppPreference;

/**
 * autor:OFFICE-ADMIN
 * time:2018/9/3
 * email:yinjuan@klcxkj.com
 * description:
 */

public class ZyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppPreference.getInstance().init(getApplicationContext());
    }
}
