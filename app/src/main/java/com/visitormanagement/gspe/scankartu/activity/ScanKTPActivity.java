package com.visitormanagement.gspe.scankartu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
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
import com.visitormanagement.gspe.scankartu.utils.CameraUtils;

import org.aviran.cookiebar2.CookieBar;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ScanKTPActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;

    public static final String KEY_IMAGE_URI = "image_uri";
    public static final String KEY_IMAGE_PATH = "image_path";

    private Bitmap mImageBitmap;
    private String currentImagePath;

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
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mImageBitmap = CameraUtils.getBitmap(currentImagePath);
            imageview.setImageBitmap(mImageBitmap);
            btnReset.setVisibility(View.VISIBLE);
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
