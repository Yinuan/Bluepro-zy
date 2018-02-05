package com.klcxkj.zqxy.utils;

import android.content.Context;
import android.view.Gravity;

import com.example.jooronjar.utils.DigitalTrans;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;

public class Toolsutils {

	
	public static final byte[] handleResult(Context context , String callbackString){
		byte[] resultbyte = DigitalTrans.hexStringToBytes(callbackString);
		byte[] databyte = null;
		if (resultbyte != null && resultbyte.length >= 9) {
			int length = resultbyte[2];
			int data_length = length - 3;
			if (data_length >0) {
				int jiaoyanvalue = 0;
				
				databyte = new byte[data_length];
				for (int i = 0; i < databyte.length; i++) {
					databyte[i] = resultbyte[i+6];
					jiaoyanvalue = jiaoyanvalue + resultbyte[i+6];
				}
				jiaoyanvalue = jiaoyanvalue + resultbyte[3]+resultbyte[4]+resultbyte[5];
				
//				if (databyte.length >0 && jiaoyanvalue == resultbyte[resultbyte.length-2]) {
//					if (databyte[0] == 0X80) {
//						Common.showToast(context, R.string.bluetooth_callback_success, Gravity.CENTER);
//					}else {
//						Common.showToast(context, R.string.bluetooth_callback_fail, Gravity.CENTER);
//					}
//				}
			}else {
				databyte = new byte[0];
			}
			
		}
		return databyte;
	}
}
