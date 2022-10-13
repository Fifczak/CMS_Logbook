package com.example.cms_logbook;

import static androidx.camera.core.CameraX.getContext;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;

import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

/**
 * Activity that shows how open a document on a HMT-1 device
 */
public class DocumentActivity extends Activity {

    private String mSampleFileName = "";
    private final String mSampleMimeType = "application/pdf";

    private File mSampleFile;

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
        setContentView(R.layout.document_main);

        mSampleFileName = this.getIntent().getStringExtra("device_id") + ".pdf";

        try {
            String path = this.getExternalFilesDir("CMSData") + "/" + mSampleFileName;
            mSampleFile = new File(path);
        } catch (Exception ex) {
            Toast.makeText(this, "Failed to copy sample file", Toast.LENGTH_LONG).show();
        }

        final Uri contentUri = FileProvider.getUriForFile(
                getApplicationContext(),
                getApplicationContext().getPackageName() + ".fileprovider",
                mSampleFile);


        if (mSampleFile == null) {
            Toast.makeText(getApplicationContext(),"Failed to find sample file",Toast.LENGTH_LONG).show();
            return;
        }

        final Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.addCategory(Intent.CATEGORY_DEFAULT);
        viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        viewIntent.setDataAndType(contentUri, mSampleMimeType);
        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        viewIntent.putExtra("page", "1"); // Open a specific page
        viewIntent.putExtra("zoom", "2"); // Open at a specific zoom level

        startActivity(viewIntent);
    }

}