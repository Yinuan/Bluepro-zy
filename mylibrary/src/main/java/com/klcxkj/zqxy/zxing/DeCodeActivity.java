package com.klcxkj.zqxy.zxing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResultType;
import com.klcxkj.zqxy.deco.common.Scanner;
import com.klcxkj.zqxy.deco.decode.QRDecode;
import com.klcxkj.zqxy.zxing.result.AddressBookActivity;
import com.klcxkj.zqxy.zxing.result.BarcodeActivity;
import com.klcxkj.zqxy.zxing.result.UriActivity;


/**
 * 单击解析图片
 */
public class DeCodeActivity extends BasicScannerActivity {
    @Override
    void onResultActivity(Result result, ParsedResultType type, Bundle bundle) {
        switch (type) {
            case ADDRESSBOOK:
                AddressBookActivity.gotoActivity(DeCodeActivity.this, bundle);
                break;
            case PRODUCT:
                BarcodeActivity.gotoActivity(DeCodeActivity.this, bundle);
                break;
            case ISBN:
                BarcodeActivity.gotoActivity(DeCodeActivity.this, bundle);
                break;
            case URI:
                UriActivity.gotoActivity(DeCodeActivity.this, bundle);
                break;
            case TEXT:
              //  TextActivity.gotoActivity(DeCodeActivity.this, bundle);
                String str =bundle.getString(Scanner.Scan.RESULT);
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                break;
            case GEO:
                break;
            case TEL:
                break;
            case SMS:
                break;
        }
        dismissProgressDialog();
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            byte[] bytes = extras.getByteArray("bytes");
            if (bytes != null && bytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showProgressDialog();
                    QRDecode.decodeQR(bitmap, this);
                }
            }
        }
    }

    public static void gotoActivity(Activity activity, byte[] bytes) {
        activity.startActivity(new Intent(activity, DeCodeActivity.class).putExtra("bytes", bytes));
    }
}
