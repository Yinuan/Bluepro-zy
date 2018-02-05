package com.klcxkj.zqxy.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.widget.CustomProgressDialog;
import com.klcxkj.zqxy.widget.NiftyDialogBuilder;

public class BaseFragment extends Fragment{


	private CustomProgressDialog progressDialog = null;
	
	protected NiftyDialogBuilder dialogBuilder;
	protected SharedPreferences sp;
	protected UserInfo mUserInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
		sp = getActivity().getSharedPreferences("adminInfo", Context.MODE_PRIVATE);

	}

	@Override
	public void onDestroy() {
		
		stopProgressDialog();
		
		super.onDestroy();
	}

	protected void startProgressDialog() {
		if (getActivity() != null && !getActivity().isFinishing()) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(getActivity());
				progressDialog.setMessage(getString(R.string.loading));
				progressDialog.setCanceledOnTouchOutside(false);
			}

			progressDialog.show();
		}
		
	}

	protected void stopProgressDialog() {
		if (getActivity() != null && !getActivity().isFinishing()) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

}
