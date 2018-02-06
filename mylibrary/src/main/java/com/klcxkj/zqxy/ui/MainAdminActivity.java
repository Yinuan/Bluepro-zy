package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.fragment.AdminFragment;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.widget.LoadingDialogProgress;
import com.klcxkj.zqxy.widget.NiftyDialogBuilder;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;

public class MainAdminActivity extends FragmentActivity {

	// 定义FragmentTabHost对象
	private FragmentTabHost mTabHost;

	// 定义一个布局
	private LayoutInflater layoutInflater;

	// 定义数组来存放Fragment界面
	private Class fragmentArray[] = { AdminFragment.class };

	// 定义数组来存放按钮图片
	private int mImageViewArray[] = { R.drawable.tab_home_btn};

	// Tab选项卡的文字
	private int mTextviewArray[] = { R.string.tab_admin,  R.string.tab_my };

	private int currentTabIndex = 0;
	private LoadingDialogProgress loadingDialogProgress;
	protected NiftyDialogBuilder dialogBuilder;
	private String isOpUser ="1";
	private SharedPreferences sp;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_tab_layout);
		StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
		initView();
		initdata();

	}

	private void initdata() {
		Intent intent =getIntent();
		String bPhone =intent.getStringExtra("tellPhone");
		String prjid =intent.getStringExtra("PrjID");
		int num =bPhone.length();
		String tell ="";
		switch (num){
			case 1:
				tell ="1300000000"+bPhone;
				break;
			case 2:
				tell ="130000000"+bPhone;
				break;
			case 3:
				tell ="13000000"+bPhone;
				break;
			case 4:
				tell ="1300000"+bPhone;
				break;
			case 5:
				tell ="130000"+bPhone;
				break;
			case 6:
				tell ="13000"+bPhone;
				break;
			case 7:
				tell ="1300"+bPhone;
				break;
			case 8:
				tell ="130"+bPhone;
				break;
			case 9:
				tell ="13"+bPhone;
				break;
			case 10:
				tell ="1"+bPhone;
				break;
			case 11:
				tell =bPhone;
				break;
		}
		MyApp.versionCode=MyApp.getLocalVersionName(MainAdminActivity.this);
		login(tell,prjid,"0");
	}


	/**
	 * 初始化组件
	 */
	private void initView() {

		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);

		// 实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		// 得到fragment的个数
		int count = fragmentArray.length;
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals(getString(mTextviewArray[0]))) {
					currentTabIndex = 0;
				} else if (tabId.equals(getString(mTextviewArray[1]))) {
					currentTabIndex = 1;
				}

			}
		});

		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(getString(mTextviewArray[i]))
					.setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			// 设置Tab按钮的背景
			// mTabHost.getTabWidget().getChildAt(i)
			// .setBackgroundResource(R.drawable.selector_tab_background);
		}
	}

	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);
		view.setVisibility(View.GONE);
		return view;
	}


	protected void login(final String phonenum, String prjid, String checkcode) {
		if (Common.isNetWorkConnected(MainAdminActivity.this)) {
			if (TextUtils.isEmpty(phonenum)) {
				Common.showToast(MainAdminActivity.this, R.string.phonenum_null, Gravity.CENTER);
				return;
			}
			if (!Common.isPhoneNum(phonenum)) {
				Common.showToast(MainAdminActivity.this, R.string.phonenum_not_irregular, Gravity.CENTER);
				return;
			}

			loadingDialogProgress = GlobalTools.getInstance().showDailog(MainAdminActivity.this,"登入中.");
			sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("TelPhone", phonenum);
			ajaxParams.put("PrjID",prjid);
			ajaxParams.put("Code",checkcode);//
			ajaxParams.put("isOpUser",isOpUser);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);
		//	Log.d("MainAdminActivity", "ajaxParams:" + ajaxParams);
			new FinalHttp().get(Common.BASE_URL + "login", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							if (loadingDialogProgress !=null){
								loadingDialogProgress.dismiss();
							}
							String result = t.toString();
						//	Log.e("MainAdminActivity", "login result = " + result);
							PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
							if (publicGetData.error_code.equals("0") || publicGetData.error_code.equals("5")) {

								UserInfo userInfo = new Gson().fromJson(publicGetData.data, UserInfo.class);
								if (isOpUser.equals("1")){

									if (userInfo.GroupID!=1){
										Toast.makeText(MainAdminActivity.this, "登录异常,没有该管理员", Toast.LENGTH_SHORT).show();
										return;
									}
								}

								SharedPreferences.Editor editor = sp.edit();
								editor.putString(Common.USER_PHONE_NUM, phonenum);
								if (userInfo == null) {
									editor.putInt(Common.ACCOUNT_IS_USER, 2);//设定为学生用户

									editor.putString(Common.USER_INFO, "");
								} else {

									userInfo.TelPhone = Long.valueOf(phonenum);
									if (userInfo.GroupID == 1) {
										editor.putInt(Common.ACCOUNT_IS_USER, 1);

									} else {
										userInfo.GroupID = 2;
										editor.putInt(Common.ACCOUNT_IS_USER, 2);

									}
								}
								editor.putString(Common.USER_INFO, new Gson().toJson(userInfo));
								editor.commit();
								EventBus.getDefault().postSticky("login_success");
							} else if (publicGetData.error_code.equals("3")) {
								Common.showToast(MainAdminActivity.this, R.string.yanzhengma_error, Gravity.CENTER);
							} else {
								Common.showToast(MainAdminActivity.this, publicGetData.message, Gravity.CENTER);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);

							Toast.makeText(MainAdminActivity.this, "登录失败,请稍后重试", Toast.LENGTH_SHORT).show();
							if (loadingDialogProgress !=null){
								loadingDialogProgress.dismiss();
							}
						}
					});

		} else {
			Toast.makeText(MainAdminActivity.this, "服务器连接不上,请检查你的网络", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(Common.USER_PHONE_NUM);
		editor.remove(Common.USER_INFO);
		editor.remove(Common.ACCOUNT_IS_USER);
		editor.commit();

	}
}
