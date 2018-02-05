package com.klcxkj.zqxy.widget;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;


/**
 * 自定义加载对话框
 * @author Zeng
 * @date 2015-11-13
 */
public class LoadingDialogProgress extends Dialog
{

	public LoadingDialogProgress(Context context)
	{

		super(context);
	}

	public LoadingDialogProgress(Context context, int theme)
	{
		super(context, theme);
	}

	/**
	 * 当窗口焦点改变时调用
	 */
	public void onWindowFocusChanged(boolean hasFocus)
	{
		ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
		// 获取ImageView上的动画背景
		AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
		// 开始动画
		spinner.start();
	}

	/**
	 * 给Dialog设置提示信息
	 * @param message
	 */
	public void setMessage(CharSequence message)
	{
		if (message != null && message.length() > 0)
		{
			findViewById(R.id.message).setVisibility(View.VISIBLE);
			TextView txt = (TextView) findViewById(R.id.message);
			txt.setText(message);
			//	txt.invalidate();
		}
	}


	public static LoadingDialogProgress show(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener)
	{
		LoadingDialogProgress dialog = new LoadingDialogProgress(context, R.style.Custom_Progress);
		dialog.setTitle("");
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.loadingdialog_progress);
		if (message == null || message.length() == 0)
		{
			dialog.findViewById(R.id.message).setVisibility(View.GONE);
		} else
		{
			TextView txt = (TextView) dialog.findViewById(R.id.message);
			txt.setText(message);
		}
		// 按返回键是否取消
		dialog.setCancelable(cancelable);
		// 监听返回键处理
		dialog.setOnCancelListener(cancelListener);
		// 设置居中
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		// 设置背景层透明度
		lp.dimAmount = 0.16f;
		dialog.getWindow().setAttributes(lp);
		// dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		return dialog;
	}

}
