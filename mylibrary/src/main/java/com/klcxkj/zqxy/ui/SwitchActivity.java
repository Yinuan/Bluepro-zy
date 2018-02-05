package com.klcxkj.zqxy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.widget.OnViewChangeListener;
import com.klcxkj.zqxy.widget.SwitchLayout;

public class SwitchActivity extends Activity {
	/** Called when the activity is first created. */
	SwitchLayout switchLayout;// �Զ���Ŀؼ�
	LinearLayout linearLayout;
	int mViewCount;// �Զ���ؼ����ӿؼ��ĸ���
	ImageView mImageView[];// �ײ���imageView
	int mCurSel;// ��ǰѡ�е�imageView
	private LinearLayout jinru_layout;
	private Button jinru_btn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch);
		init();
	}

	private void init() {
		switchLayout = (SwitchLayout) findViewById(R.id.switchLayoutID);
		linearLayout = (LinearLayout) findViewById(R.id.linerLayoutID);

		jinru_layout = (LinearLayout)findViewById(R.id.jinru_layout);
		jinru_layout.setVisibility(View.GONE);
		jinru_btn = (Button)findViewById(R.id.jinru_btn);
		
		mViewCount = switchLayout.getChildCount();
		mImageView = new ImageView[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			mImageView[i] = (ImageView) linearLayout.getChildAt(i);
			mImageView[i].setEnabled(true);// �ؼ�����
			mImageView[i].setOnClickListener(new MOnClickListener());
			mImageView[i].setTag(i);// ������view��صı�ǩ
		}
		// ���õ�һ��imageView��������
		mCurSel = 0;
		mImageView[mCurSel].setEnabled(false);
		switchLayout.setOnViewChangeListener(new MOnViewChangeListener());
		jinru_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SwitchActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private class MOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			int pos = (Integer) v.getTag();
			setCurPoint(pos);
			switchLayout.snapToScreen(pos);
		}
	}

	/**
	 * ���õ�ǰ��ʾ��ImageView
	 * 
	 * @param pos
	 */
	private void setCurPoint(int pos) {
		if (pos < 0 || pos > mViewCount - 1 || mCurSel == pos)
			return;
		// ��ǰ��imgaeView�����Ա�����
		mImageView[mCurSel].setEnabled(true);
		// ��Ҫ��ת��ȥ���Ǹ�imageView��ɲ��ɼ���
		mImageView[pos].setEnabled(false);
		mCurSel = pos;
	}

	// �Զ���ؼ���View�ı���¼�����
	private class MOnViewChangeListener implements OnViewChangeListener {
		@Override
		public void onViewChange(int view) {
			if (view == 2) {
				jinru_layout.setVisibility(View.VISIBLE);
			}else {
				jinru_layout.setVisibility(View.GONE);
			}
			if (view < 0 || mCurSel == view) {
				return;
			} else if (view > mViewCount - 1) {
				// ���������������ʱ��activity�ᱻ�ر�
				
				Log.e("water", "finish");
				finish();
			}
			setCurPoint(view);
		}

	}

}