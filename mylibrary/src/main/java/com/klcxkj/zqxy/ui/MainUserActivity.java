package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
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
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.UpdateInfo;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.download.DownLoadUtils;
import com.klcxkj.zqxy.download.DownloadApk;
import com.klcxkj.zqxy.fragment.OneFragment;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.widget.Effectstype;
import com.klcxkj.zqxy.widget.LoadingDialogProgress;
import com.klcxkj.zqxy.widget.NiftyDialogBuilder;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MainUserActivity extends FragmentActivity {

	// 定义FragmentTabHost对象
	private FragmentTabHost mTabHost;

	// 定义一个布局
	private LayoutInflater layoutInflater;

	// 定义数组来存放Fragment界面
	private Class fragmentArray[] = {OneFragment.class};

	// 定义数组来存放按钮图片
	private int mImageViewArray[] = { R.drawable.tab_home_btn };

	// Tab选项卡的文字
	private int mTextviewArray[] = { R.string.tab_usewater};

	private int currentTabIndex = 0;
	protected NiftyDialogBuilder dialogBuilder;
	private LoadingDialogProgress loadingDialogProgress;
	private String isOpUser ="0";
	private SharedPreferences sp;

	public static String pName ="蓝牙项目";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_layout);
		StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		initView();
		initdata();
	}

	private void initdata() {
		Intent intent =getIntent();
		String bPhone =intent.getStringExtra("tellPhone");
		String prjid =intent.getStringExtra("PrjID");
		Common.BASE_URL=intent.getStringExtra("app_url");
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
		MyApp.versionCode=MyApp.getLocalVersionName(MainUserActivity.this);
		login(bPhone,prjid,"0");
	}


	protected void login(final String phonenum, String prjid, String checkcode) {
		if (Common.isNetWorkConnected(MainUserActivity.this)) {
			/*if (TextUtils.isEmpty(phonenum)) {
				Common.showToast(MainUserActivity.this, R.string.phonenum_null, Gravity.CENTER);
				return;
			}*/
			/*if (!Common.isPhoneNum(phonenum)) {
				Common.showToast(MainUserActivity.this, R.string.phonenum_not_irregular, Gravity.CENTER);
				return;
			}*/
			loadingDialogProgress = GlobalTools.getInstance().showDailog(MainUserActivity.this,"登入中.");
			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("TelPhone", phonenum);
			ajaxParams.put("PrjID", prjid);
			ajaxParams.put("Code",checkcode);//
			ajaxParams.put("isOpUser",isOpUser);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);

			//Log.d("MainUserActivity", "ajaxParams:" + ajaxParams);
			new FinalHttp().get(Common.BASE_URL + "login2", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							if (loadingDialogProgress !=null){
								loadingDialogProgress.dismiss();
							}
							String result = t.toString();
							Log.e("MainUserActivity", "login result = " + result);
							PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
							if (publicGetData.error_code.equals("0") || publicGetData.error_code.equals("5")) {

								UserInfo userInfo = new Gson().fromJson(publicGetData.data, UserInfo.class);
								if (isOpUser.equals("1")){

									if (userInfo.GroupID!=1){
										Toast.makeText(MainUserActivity.this, "登录异常,没有该管理员", Toast.LENGTH_SHORT).show();
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
								pName=userInfo.PrjName;
								EventBus.getDefault().postSticky("login_success");
							} else if (publicGetData.error_code.equals("3")) {
								Common.showToast(MainUserActivity.this, R.string.yanzhengma_error, Gravity.CENTER);
							} else {
								Common.showToast(MainUserActivity.this, publicGetData.message, Gravity.CENTER);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);

							Toast.makeText(MainUserActivity.this, "登录失败,请稍后重试", Toast.LENGTH_SHORT).show();
							if (loadingDialogProgress !=null){
								loadingDialogProgress.dismiss();
							}
						}
					});

		} else {
			Toast.makeText(MainUserActivity.this, "服务器连接不上,请检查你的网络", Toast.LENGTH_SHORT).show();
		}

	}
	private UpdateInfo info;
	private void updateApp() {


		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("phoneSystem", "Android");
		ajaxParams.put("version", MyApp.versionCode);

		new FinalHttp().get(Common.BASE_URL + "versionCheck", ajaxParams,new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				//Log.d("MainUserActivity", "onSuccess"+t.toString());
				info =new Gson().fromJson(t.toString(), UpdateInfo.class);
				if(info.getError_code().equals("-1")){

					showPop();
				}else {
					//DownloadApk.removeFile(MainUserActivity.this);
					//showPop2();
				}
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				//Log.d("MainUserActivity", "onFailure"+strMsg);
			}
		});

	}
	protected void showPop() {
		dialogBuilder.withTitle("提示")
				.withMessage(info.getMessage())
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton1Text(getString(R.string.cancel))
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						MainUserActivity.this.finish();
					}
				})
				.withButton2Text(getString(R.string.sure))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						//String msg ="下载已开始，您可以点击查看当前下载进度条";
						//downApk(msg);
						intit_getClick();
						MainUserActivity.this.finish();
					}
				})
				.show();

	}
	protected void showPop2() {
		dialogBuilder.withTitle("提示")
				.withMessage(info.getMessage())
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton1Text(getString(R.string.cancel))
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				})
				.withButton2Text(getString(R.string.sure))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						//String msg ="下载已开始，您可以点击查看当前下载进度条";
						//downApk(msg);
						intit_getClick();
					}
				})
				.show();

	}
	/**
	 * 下载更新
	 */
	private void downApk(String msg){
		if (DownLoadUtils.getInstance(getApplicationContext()).canDownload()) {
			DownloadApk.downloadApk(getApplicationContext(),info.getUrl(), "趣智校园更新", "quzhixiaoyuan");
			Log.d("MainUserActivity", "zoumeizou"+msg);
		} else {
			DownLoadUtils.getInstance(getApplicationContext()).skipToDownloadManager();

		}
	}



	/**
	 * 获取本地软件版本号名称
	 */
	public static String getLocalVersionName(Context ctx) {
		String localVersion = "";
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			localVersion = packageInfo.versionName;

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return localVersion;
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
				} else if (tabId.equals(getString(mTextviewArray[2]))) {
					currentTabIndex = 2;
				} else if (tabId.equals(getString(mTextviewArray[3]))) {
					currentTabIndex = 3;
				}

			}
		});

		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(getString(mTextviewArray[i])).setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			// 设置Tab按钮的背景
			// mTabHost.getTabWidget().getChildAt(i)
			// .setBackgroundResource(R.drawable.selector_tab_background);
	        
		}
	}


	public void setCurrentTab(int index){
		if (mTabHost != null) {
			mTabHost.setCurrentTab(index);
		}
	}
	
	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);
		view.setVisibility(View.GONE);
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);

		return view;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DownloadApk.unregisterBroadcast(this);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(Common.USER_PHONE_NUM);
		editor.remove(Common.USER_INFO);
		editor.remove(Common.ACCOUNT_IS_USER);
		editor.remove(Common.USER_BRATHE+Common.getUserPhone(sp));
		editor.remove(Common.USER_WASHING+Common.getUserPhone(sp));
		editor.commit();
	}

	/**
	 * 判断应用的包名是否存在
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAvilible(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
		// 从pinfo中将包名字逐一取出，压入pName list中
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
	}

	/**
	 * 启动外面的应用程序
	 * @param context
	 * @param appPkg
	 * @param marketPkg
	 */
	public static void launchAppDetail(Context context, String appPkg, String marketPkg) {
		try {
			if (TextUtils.isEmpty(appPkg))
				return;
			Uri uri = Uri.parse("market://details?id=" + appPkg);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if (!TextUtils.isEmpty(marketPkg))
				intent.setPackage(marketPkg);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 版本更新跳转到腾讯应用下载
	 */
	private void intit_getClick() {
		if (isAvilible(this, "com.tencent.android.qqdownloader")) {
		// 市场存在
			launchAppDetail(getApplicationContext(), "com.klcxkj.zqxy", "com.tencent.android.qqdownloader");
		} else {
			Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.klcxkj.zqxy");
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(it);
		}
	}
}
