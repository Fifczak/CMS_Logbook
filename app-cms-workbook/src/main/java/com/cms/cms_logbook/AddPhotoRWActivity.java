package com.cms.cms_logbook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Base64;
import android.view.View;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * Activity that shows how to use the camera to take a pictures and record videos on a HMT-1 device.
 */
public class AddPhotoRWActivity extends Activity {

    public static final String PhotoResult = "";
    private static final String TAG = "AddPhotoRWActivity";

    // Request code for playing back videos.
    private static final int FILE_PLAYBACK_REQUEST_CODE = 5;

    //
    // Request codes for identifying camera events.
    //
    private static final int BITMAP_PHOTO_REQUEST_CODE = 1;
    private static final int FILE_PROVIDER_PHOTO_REQUEST_CODE = 2;
    private static final int BASIC_VIDEO_REQUEST_CODE = 3;
    private static final int FILE_PROVIDER_VIDEO_REQUEST_CODE = 4;

    //
    // Locations for store content provided images and videos.
    //
    private static final String DEFAULT_IMAGE_LOCATION = Environment.DIRECTORY_DCIM + "/Camera";
    private static final String DEFAULT_VIDEO_LOCATION = Environment.DIRECTORY_MOVIES + "/Camera";

    // Identifier for the image returned by the camera
    private static final String EXTRA_RESULT = "data";


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
        setContentView(R.layout.add_photo_rw_main);

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, BITMAP_PHOTO_REQUEST_CODE);
    }

    /**
     * Listener for when the bitmap photo button is clicked.
     *
     * @param view The button.
     */
    public void onLaunchBitmapPhoto(View view) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, BITMAP_PHOTO_REQUEST_CODE);
    }

    /**
     * Listener for when the file provider photo button is clicked.
     *
     * @param view The button.
     */
    public void onLaunchFileProviderPhoto(View view) {
        final String fileName = "devexamples-" + UUID.randomUUID() + ".jpg";

        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, DEFAULT_IMAGE_LOCATION);

        final Uri contentUri = getBaseContext().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        startActivityForResult(captureIntent, FILE_PROVIDER_PHOTO_REQUEST_CODE);
    }

    /**
     * Listener for when the bitmap photo button is clicked.
     *
     * @param view The button.
     */
    public void onLaunchBasicVideo(View view) {
        final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, BASIC_VIDEO_REQUEST_CODE);
    }

    /**
     * Listener for when the file provider video button is clicked.
     *
     * @param view The button.
     */
    public void onLaunchFileProviderVideo(View view) {
        final String fileName = "devexamples-" + UUID.randomUUID() + ".mp4";

        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, DEFAULT_VIDEO_LOCATION);

        final Uri contentUri = getBaseContext().getContentResolver().insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);

        final Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        startActivityForResult(captureIntent, FILE_PROVIDER_VIDEO_REQUEST_CODE);
    }

    /**
     * Listener for result from external activities. Receives image data from camera.
     *
     * @param requestCode See Android docs.
     * @param resultCode  See Android docs.
     * @param data        See Android docs.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            final Bitmap photo = data.getExtras().getParcelable(EXTRA_RESULT);
            final Intent intent = new Intent();
            String PhotoString = BitMapToString(photo);
            intent.putExtra(PhotoResult, PhotoString);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if(resultCode == 0){
            finish();
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
