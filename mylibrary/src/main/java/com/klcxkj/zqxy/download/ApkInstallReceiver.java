package com.klcxkj.zqxy.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.klcxkj.zqxy.download.config.SystemParams;

import java.io.File;


/**
 * Created by Song on 2016/11/2.
 */
public class ApkInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.d("---","--大小-"+downloadApkId);
            if (downloadApkId >0l){
                installApk(context, downloadApkId);
            }

        }
    }

    /**
     * 安装apk
     */
    private void installApk(Context context, long downloadId) {

        long downId = SystemParams.getInstance().getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
        if(downloadId == downId) {
            DownloadManager downManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
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
            } else {
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
