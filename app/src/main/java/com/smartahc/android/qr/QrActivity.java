package com.smartahc.android.qr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import com.smartahc.android.core_qr_lib.ZXingScannerView;
import com.smartahc.android.core_qr_lib.ZXingType;

import java.util.ArrayList;


/**
 * Created by yuan on 2017/10/19.
 * 扫码界面
 */

public class QrActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private ArrayList<String> results = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this, ZXingType.QR_READER);
        setContentView(mScannerView);
        results.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void handleResult(Result... results) {
        if (results.length > 0) {
            for (int i = 0; i < results.length; i++) {
                Result result = results[i];
                printLog(result.getText());
            }
            //
            mScannerView.resumeCameraPreview(this);
        }
    }

    private void printLog(String result) {
        if (!results.contains(result)) {
            Log.v("new result :", result);
            results.add(result);
            Log.v("multi results : " + results.size(), results.toString());
            if (results.size() == 30) {
                Toast.makeText(this, "总数:" + results.size(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
