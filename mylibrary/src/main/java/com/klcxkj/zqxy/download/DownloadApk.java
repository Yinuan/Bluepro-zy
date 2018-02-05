package com.klcxkj.zqxy.download;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.klcxkj.zqxy.download.config.SystemParams;

import java.io.File;


/**
 * Apk下载
 * Created by Song on 2016/11/2.
 */
public class DownloadApk {

    private static ApkInstallReceiver apkInstallReceiver;

    /**
     * 下载APK文件
     * @param context
     * @param url
     * @param title
     * @param appName
     */
    public static void downloadApk(Context context, String url, String title, final String appName) {

        //获取存储的下载ID
        long downloadId = SystemParams.getInstance().getLong(DownloadManager.EXTRA_DOWNLOAD_ID,-1L);
        Log.d("DownloadApk", "downloadId1:" + downloadId);
        if(downloadId != -1) {
            //存在downloadId
            DownLoadUtils downLoadUtils = DownLoadUtils.getInstance(context);
            //获取当前状态
            int status = downLoadUtils.getDownloadStatus(downloadId);
            Log.d("DownloadApk", "status:" + status);
            if(DownloadManager.STATUS_SUCCESSFUL == status) {
                //状态为下载成功
                //获取下载路径URI
                Uri downloadUri = downLoadUtils.getDownloadUri(downloadId);
                Log.d("DownloadApk", "downloadUri:" + downloadUri.getPath());
                Log.d("DownloadApk", Environment.DIRECTORY_DOWNLOADS);
                File file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/quzhixiaoyuan.apk");
                if(null != downloadUri) {
                    //存在下载的APK，如果两个APK相同，启动更新界面。否之则删除，重新下载。
                    if(compare(getApkInfo(context,file.getPath()),context)) {
                        Log.d("DownloadApk", "1");
                        startInstall(context, downloadUri);
                        return;
                    } else {
                        Log.d("DownloadApk", "11");
                        //删除下载任务以及文件
                        downLoadUtils.getDownloadManager().remove(downloadId);
                    }
                }
               // start(context, url, title,appName);
            } else if(DownloadManager.STATUS_FAILED == status) {
                //下载失败,重新下载
                start(context, url, title,appName);
            }else {
                Log.d("DownloadApk", "apk is already downloading");
                //打开安装界面


            }
        } else {
            //不存在downloadId，没有下载过APK
            Toast.makeText(context,"下载已开始，您可以点击查看当前下载进度条",Toast.LENGTH_SHORT).show();
            start(context, url, title,appName);
        }
    }

    /**
     * 开始下载
     * @param context
     * @param url
     * @param title
     * @param appName
     */
    private static void start(Context context, String url, String title, String appName) {

        if(hasSDKCard()) {
            long id = DownLoadUtils.getInstance(context).download(url, title, "下载完成后点击打开", appName);
            Log.d("DownloadApk", "downloadId2:" + id);
            SystemParams.getInstance().setLong(DownloadManager.EXTRA_DOWNLOAD_ID,id);
        } else {
            Toast.makeText(context,"手机未安装SD卡，下载失败", Toast.LENGTH_LONG).show();
        }
    }

    public static void registerBroadcast(Context context) {
        apkInstallReceiver = new ApkInstallReceiver();
        context.registerReceiver(apkInstallReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public static void unregisterBroadcast(Context context) {
        if(null != apkInstallReceiver) {
            context.unregisterReceiver(apkInstallReceiver);
        }
    }

    /**
     * 跳转到安装界面
     * @param context
     * @param uri
     */
    private static void startInstall(Context context, Uri uri) {
        Uri downloadUri;
        File file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/quzhixiaoyuan.apk");
        if (file !=null){
            String path = file.getAbsolutePath();
            downloadUri = Uri.parse("file://" + path);
            SystemParams.getInstance().setString("downloadApk",path);
            Intent install= new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(downloadUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        }

    }


    /**
     * 获取APK程序信息
     * @param context
     * @param path
     * @return
     */
    private static PackageInfo getApkInfo(Context context, String path) {

        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if(null != pi) {
            return pi;
        }
        return null;
    }


    /**
     * 比较两个APK的信息
     * @param apkInfo
     * @param context
     * @return
     */
    private static boolean compare(PackageInfo apkInfo, Context context) {

        if(null == apkInfo) {
            return false;
        }
        String localPackageName = context.getPackageName();
        if(localPackageName.equals(apkInfo.packageName)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackageName, 0);
                //比较当前APK和下载的APK版本号
                Log.d("DownloadApk", "apkInfo.versionCode:" + apkInfo.versionCode);
                Log.d("DownloadApk", "packageInfo.versionCode:" + packageInfo.versionCode);
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    //如果下载的APK版本号大于当前安装的APK版本号，返回true
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 是否存在SD卡
     */
    private static boolean hasSDKCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 删除已下载的文件
     */
    public static void removeFile(Context context) {
        SystemParams.getInstance().setLong(DownloadManager.EXTRA_DOWNLOAD_ID,-1L);
        String filePath = SystemParams.getInstance().getString("downloadApk",null);
        if(null != filePath) {
            File downloadFile = new File(filePath);
            if(null != downloadFile && downloadFile.exists()) {
                //删除之前先判断用户是否已经安装了，安装了才删除。
                if(!compare(getApkInfo(context,filePath),context)) {
                    downloadFile.delete();
                }
            }
        }
    }
}
