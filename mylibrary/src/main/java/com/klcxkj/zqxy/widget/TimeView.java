package com.klcxkj.zqxy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import com.klcxkj.mylibrary.R;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by 贺志虎 on 2016/2/29 0029.
 */
public class TimeView extends android.support.v7.widget.AppCompatTextView {

    private long hours;
    private long minutes;
    private long seconds;
    private long diff;
    private long days;
    private long time = 0;
    private TypedArray a;

    public TimeView(Context context) {
        this(context, null);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);

         a = context.obtainStyledAttributes(attrs, R.styleable.TimeView);

        Log.d("TAG", "再打印  onCreate :" + diff);

       // onCreate();

    }


    /**
     * 根据 attrs 设置时间开始
     */
    private void onCreate() {
        start();
    }

    //开始计时
    private void start() {

        handler.removeMessages(1);

        Log.d("TAG", "再打印  onCreate");
        Log.d("TAG", "再打印  setTime/..................................//////////////////////////");
        getTime();
        Message message = handler.obtainMessage(1);
        handler.sendMessageDelayed(message, 1000);
    }

    public void log() {
        Log.d("TAG", "再打印  log/////////////////////////////////////////////////////////////////");
    }
    public void stop(){
        handler.removeMessages(1);
        handler.removeMessages(2);
    }

    public Long getDiff(){

        return diff;
    }
    /**
     * 设置事件
     *
     * @param time
     */
    public void setTime(int time) {
        this.time = time * 1000;
        diff = a.getInteger(R.styleable.TimeView_time, time) * 1000;
        onCreate();
    }

    final Handler handler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) {         // handle message
            Log.d("TAG", "再打印  handleMessage"+diff);
            switch (msg.what) {
                case 1:
                    //setVisibility(View.VISIBLE);
                    diff = diff - 1000;
                    getShowTime();
                    if (diff > 0) {
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 1000);
                    } else {
                        Message message = handler.obtainMessage(2);
                        handler.sendMessageDelayed(message, 1000);
                    }
                    break;
                case 2:
                    diff = 0;
                    getShowTime();
                   // Message message = handler.obtainMessage(2);
                   // handler.sendMessageDelayed(message, 1000);
                    handler.removeMessages(1);
                    handler.removeMessages(2);
                    EventBus.getDefault().postSticky("washingtimeover");
                    break;
                default:
                    break;
            }
            Log.d("TAG", "再打印");
            super.handleMessage(msg);
        }
    };

    /**
     * 得到时间差
     * 第一次显示
     */
    private void getTime() {
        Log.d("TAG", "再打印 :getTime");

        try {

            days = diff / (1000 * 60 * 60 * 24);
            hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            seconds = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);
            if (minutes<10 && seconds<10){
                setText("0"+minutes+":0"+seconds);
            }else if (minutes<10 && seconds>10){
                setText("0"+minutes+":"+seconds);
            }else if (minutes>10 && seconds>10){
                setText(minutes + ":" + seconds);
            }else if (minutes>10 && seconds<10){
                setText(minutes + ":0" + seconds);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 获得要显示的时间
     * 持续显示
     */
    private void getShowTime() {
        Log.d("TAG", "再打印 :getShowTime");

        days = diff / (1000 * 60 * 60 * 24);
        hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        seconds = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);
        if (seconds >=0){
            if (minutes<10 && seconds<10){
                setText("0"+minutes+":0"+seconds);
            }else if (minutes<10 && seconds>10){
                setText("0"+minutes+":"+seconds);
            }else if (minutes>10 && seconds>10){
                setText(minutes + ":" + seconds);
            }else if (minutes>10 && seconds<10){
                setText(minutes + ":0" + seconds);
            }
        }

    }

    /**
     * 以之前设置的时间重新开始
     */
    public void reStart() {
        this.diff = this.time;
        start();
    }

    /**
     * 设置时间重新开始
     *
     * @param time 重新开始的事件
     */
    public void reStart(long time) {
        if (time > 0) {
            this.diff = time * 1000;
            Log.d("TAG", "+=========================" + diff);
        }
        start();
    }

}
