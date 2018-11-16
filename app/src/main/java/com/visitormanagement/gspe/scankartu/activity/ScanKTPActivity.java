package com.visitormanagement.gspe.scankartu.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.visitormanagement.gspe.scankartu.database.GenericCardDBSource;
import com.visitormanagement.gspe.scankartu.model.Constants;
import com.visitormanagement.gspe.scankartu.model.GenericCard;
import com.visitormanagement.gspe.scankartu.processing.KtpProcessing;
import com.visitormanagement.gspe.scankartu.R;
import com.visitormanagement.gspe.scankartu.utils.FileUtils;

import org.aviran.cookiebar2.CookieBar;

import java.io.File;
import java.util.HashMap;

public class ScanKTPActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;

    public static final String KEY_IMAGE_URI = "image_uri";
    public static final String KEY_IMAGE_PATH = "image_path";

    private Bitmap mImageBitmap;
    private String currentImagePath;
    private Uri capturedImageURI = null;

    private ImageView imageview;
    private Button btnReset, btnExtract, btnCancel, btnSave;
    private EditText etNik, etName, etDob, etGender, etAddress, etReligion, etMaritalStatus, etJob, etNationality;

    GenericCardDBSource dbSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ktp);

        imageview = findViewById(R.id.imageview);
        btnReset = findViewById(R.id.btn_reset);
        btnExtract = findViewById(R.id.btn_extract);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        etNik = findViewById(R.id.et_nik);
        etName = findViewById(R.id.et_name);
        etDob = findViewById(R.id.et_dob);
        etGender = findViewById(R.id.et_gender);
        etAddress = findViewById(R.id.et_address);
        etReligion = findViewById(R.id.et_religion);
        etMaritalStatus = findViewById(R.id.et_marital);
        etJob = findViewById(R.id.et_job);
        etNationality = findViewById(R.id.et_valid_date);

        if (savedInstanceState != null) {
            String imageUri = savedInstanceState.getString(KEY_IMAGE_URI);
            if (imageUri != null) {
                capturedImageURI = Uri.parse(imageUri);
            }
            currentImagePath = savedInstanceState.getString(KEY_IMAGE_PATH);
        }
        btnExtract.setOnClickListener(new View.OnClickListener() {
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

//        public void takePicture(View view) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = null;
//            try {
//                photoFile = CameraUtils.createImageFile(this);
//                currentImagePath = photoFile.getAbsolutePath();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
//            }
//            if (photoFile != null) {
//                Uri photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
//    }

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

    public void detectText() {
        if(mImageBitmap!=null){
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mImageBitmap);
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    HashMap<String, String> dataMap = new KtpProcessing().processingText(firebaseVisionText, getApplicationContext());
                    presentOutput(dataMap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ScanKTPActivity.this, "Gagal mendeteksi foto", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this,"Silakan ambil gambar terlebih dahulu!",Toast.LENGTH_SHORT).show();
        }
    }

    private void presentOutput(HashMap<String ,String > dataMap){
        if (dataMap != null) {
            etGender.setText(dataMap.get(Constants.gender));
            etMaritalStatus.setText(dataMap.get(Constants.marital));
            etDob.setText(dataMap.get(Constants.dob));
            etName.setText(dataMap.get(Constants.name));
        }
    }

    private void saveResult() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        final String data = etGender.getText().toString() + " " + etMaritalStatus.getText().toString();
        if (!data.isEmpty()) {
            try {
                GenericCard card = new GenericCard("Kartu Tanda Penduduk", data, currentImagePath);
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
            CookieBar.build(this)
                    .setBackgroundColor(R.color.color_gspe)
                    .setTitle("Info")
                    .setMessage("Gagal menyimpan data")
                    .show();
        }
    }
}
