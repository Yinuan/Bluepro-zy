package com.klcxkj.zqxy;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * autor:OFFICE-ADMIN
 * time:2018/2/5
 * email:yinjuan@klcxkj.com
 * description:
 */

public class MyApp {

    public static String versionCode;
    public static int checkCode =-11; //租赁设备判断
    public static String washingDecivename ="洗衣机";
    public static  String accNum ="";

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext().getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String verCode =localVersion.substring(1,localVersion.length());
        return "1.3.1";
    }
}
