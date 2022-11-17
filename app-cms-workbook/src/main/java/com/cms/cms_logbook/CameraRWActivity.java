package com.cms.cms_logbook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class CameraRWActivity extends Activity implements QRCodeImageAnalysis.QrCodeAnalysisCallback {

    // Request code identifying the barcode scanner events
    private static final int BARCODE_REQUEST_CODE = 1984;
    public static final String QR_SCAN_RESULT = "SCAN_RESULT";
    // Barcode scanner intent action
    private static final String SCAN_BARCODE = "com.realwear.barcodereader.intent.action.SCAN_BARCODE";

    // Identifier for the result string returned by the barcode scanner
    private static final String EXTRA_RESULT = "com.realwear.barcodereader.intent.extra.RESULT";

    //
    // Available barcode symbologies
    //
    private static final String EXTRA_CODE_128 = "com.realwear.barcodereader.intent.extra.CODE_128";
    private static final String EXTRA_CODE_DM = "com.realwear.barcodereader.intent.extra.CODE_DM";
    private static final String EXTRA_CODE_EAN_UPC = "com.realwear.barcodereader.intent.extra.CODE_EAN_UPC";
    private static final String EXTRA_CODE_QR = "com.realwear.barcodereader.intent.extra.CODE_QR";

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState See Android docs
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.barcode_main);

        onLaunchBarcode();
    }


    public void onLaunchBarcode() {
        Intent intent = new Intent(SCAN_BARCODE);
        intent.putExtra(EXTRA_CODE_128, false);
        intent.putExtra(EXTRA_CODE_DM, true);
        intent.putExtra(EXTRA_CODE_EAN_UPC, true);
        intent.putExtra(EXTRA_CODE_QR, true);

        startActivityForResult(intent, BARCODE_REQUEST_CODE);
    }

    @Override
    public void onQrCodeDetected(String result) {
        final Intent intent = new Intent();
        intent.putExtra(QR_SCAN_RESULT, result);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * Listener for result from external activities. Receives barcode data from scanner.
     *
     * @param requestCode See Android docs
     * @param resultCode  See Android docs
     * @param data        See Android docs
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == BARCODE_REQUEST_CODE) {
            String result = "[No Barcode]";
            if (data != null) {
                result = data.getStringExtra(EXTRA_RESULT);
                onQrCodeDetected(result);
            }
        } else if(resultCode == 0){
            finish();
        }
    }

}
