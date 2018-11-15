package com.visitormanagement.gspe.scankartu.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.visitormanagement.gspe.scankartu.database.GenericCardDBSource;
import com.visitormanagement.gspe.scankartu.model.GenericCard;
import com.visitormanagement.gspe.scankartu.processing.GenericProcessing;
import com.visitormanagement.gspe.scankartu.R;
import com.visitormanagement.gspe.scankartu.utils.FileUtils;

import org.aviran.cookiebar2.CookieBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenericCardActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    public static final String KEY_IMAGE_URI = "image_uri";
    public static final String KEY_IMAGE_PATH = "image_path";

    ImageView imageview;
    TextView tvResult;
    Button btnDetectText, btnReset, btnSave;

    String currentImagePath;
    Bitmap mImageBitmap;
    Uri capturedImageURI = null;

    GenericCardDBSource dbSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_card);

        imageview = findViewById(R.id.imageview);
        tvResult = findViewById(R.id.textView);
        btnDetectText = findViewById(R.id.btn_extract);
        btnReset = findViewById(R.id.btn_reset);
        btnSave = findViewById(R.id.btn_save);

//        if (savedInstanceState != null) {
//            currentImagePath = savedInstanceState.getString(KEY_IMAGE_PATH);
//            mImageBitmap = FileUtils.getBitmap(currentImagePath);
//            imageview.setImageBitmap(mImageBitmap);
//        }

        if (savedInstanceState != null) {
            String imageUri = savedInstanceState.getString(KEY_IMAGE_URI);
            if (imageUri != null) {
                capturedImageURI = Uri.parse(imageUri);
            }
            currentImagePath = savedInstanceState.getString(KEY_IMAGE_URI);
        }

        if (currentImagePath != null) {
            btnDetectText.setEnabled(true);
        }

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        btnDetectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveResult();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (capturedImageURI != null) {
            outState.putString(KEY_IMAGE_URI, capturedImageURI.toString());
        }
        outState.putString(KEY_IMAGE_PATH, currentImagePath);
    }

    public void takePhoto(View view) {
        final File rootDir = new File(FileUtils.PICTURE_DIRECTORY);
        rootDir.mkdirs();
        File file = new File(rootDir, FileUtils.generateImageName("generic"));
        capturedImageURI = Uri.fromFile(file);
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        if (packageManager != null) {
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for(ResolveInfo res : listCam) {
                if (res.activityInfo != null) {
                    final String packageName = res.activityInfo.packageName;
                    final Intent intent = new Intent(captureIntent);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    intent.setPackage(packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                    cameraIntents.add(intent);
                }
            }
        }
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, REQUEST_TAKE_PHOTO);
    }

    public void takePicture(View view) {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File rootDir = new File(FileUtils.PICTURE_DIRECTORY);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }

        File file = new File(rootDir, FileUtils.generateImageName("generic"));
        capturedImageURI = Uri.fromFile(file);
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
            startActivityForResult(captureIntent, REQUEST_TAKE_PHOTO);
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

    public void reset() {
        mImageBitmap= null;
        imageview.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_driving_licence));
        btnReset.setVisibility(View.GONE);
    }

    public void detectText() {
        if(mImageBitmap!=null){
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mImageBitmap);
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    HashMap<String,String> dataMap = new GenericProcessing().processText(firebaseVisionText,getApplicationContext());
                    showMessage("Sukses mengekstrak data");
                    presentOutput(dataMap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GenericCardActivity.this, "Gagal mengekstrak informasi", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this,"Silakan ambil gambar dulu!",Toast.LENGTH_SHORT).show();
        }
    }

    private void presentOutput(HashMap<String ,String > dataMap){
        if(dataMap!=null){
            tvResult.setText(dataMap.get("text"));
        }
    }

    private void saveResult() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        final String data = String.valueOf(tvResult.getText().toString().trim());
        if (!data.isEmpty()) {
            GenericCard card = new GenericCard("Generic Card", data, currentImagePath);
            try {
                dbSource = new GenericCardDBSource(getBaseContext());
                dbSource.open();
                dbSource.addCard(card);
                dbSource.close();
                progressDialog.dismiss();
                Toast.makeText(this, "Berhasil menyimpan data", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HomeActivity.class);
                finish();
                startActivity(intent);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        } else {
            showMessage("Gagal menyimpan data");
        }
    }

    private void showMessage(String message) {
        CookieBar.build(this)
                .setBackgroundColor(R.color.color_gspe)
                .setTitle("Info")
                .setMessage(message)
                .show();
    }
}
