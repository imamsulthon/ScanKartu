package com.visitormanagement.gspe.scankartu.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.visitormanagement.gspe.scankartu.R;
import com.visitormanagement.gspe.scankartu.utils.CameraUtils;
import com.visitormanagement.gspe.scankartu.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class ScanSIMActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;

    public static final String KEY_IMAGE_URI = "image_uri";
    public static final String KEY_IMAGE_PATH = "image_path";

    private Bitmap mImageBitmap;
    private String currentImagePath;
    private Uri capturedImageURI = null;

    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_sim);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (capturedImageURI != null) {
            outState.putString(KEY_IMAGE_URI, capturedImageURI.toString());
        }
        outState.putString(KEY_IMAGE_PATH, currentImagePath);
    }

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = CameraUtils.createImageFile(this);
                currentImagePath = photoFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            final Uri selectedImageUri = (data == null) ? capturedImageURI : data.getData();
            if (selectedImageUri != null) {
                if (requestCode == REQUEST_TAKE_PHOTO) {
                    final Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(selectedImageUri);
                    this.sendBroadcast(mediaScanIntent);
                }
                currentImagePath = FileUtils.getPath(getApplicationContext(), selectedImageUri);
                if (currentImagePath != null && !currentImagePath.isEmpty()) {
                    mImageBitmap = FileUtils.getBitmap(currentImagePath);
                    imageview.setImageBitmap(mImageBitmap);
                }
            }
        }
    }
}
