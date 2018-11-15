package com.visitormanagement.gspe.scankartu.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.visitormanagement.gspe.scankartu.R;

import org.aviran.cookiebar2.CookieBar;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkPermission();
    }

    public void openActivity(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.scan_KTP:
                Intent intentScanKtp = new Intent(this, ScanKTPActivity.class);
                startActivity(intentScanKtp);
                break;
            case R.id.scan_SIM:
                Intent intentScanSIM = new Intent(this, ScanSIMActivity.class);
                startActivity(intentScanSIM);
                break;
            case R.id.scan_business_card:
                CookieBar.build(this)
                        .setTitle("Business Card")
                        .setMessage("Fitur ini belum tersedia")
                        .setBackgroundColor(R.color.colorPrimary)
                        .setLayoutGravity(Gravity.BOTTOM)
                        .show();
                break;
            case R.id.scan_generic_card:
                Intent intentScanGenericCard = new Intent(this, GenericCardActivity.class);
                startActivity(intentScanGenericCard);
                break;
            case R.id.input_manual:
                Intent intentInputManual = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intentInputManual);
                break;
            case R.id.guest_archieve:
                Intent intentGuestArchive = new Intent(this, GuestArchiveActivity.class);
                startActivity(intentGuestArchive);
                break;
            case R.id.settings:
                CookieBar.build(this)
                        .setTitle("Menu: Pengaturan")
                        .setMessage("Fitur ini belum tersedia")
                        .setBackgroundColor(R.color.colorPrimary)
                        .setLayoutGravity(Gravity.BOTTOM)
                        .show();
                break;
            case R.id.about:
                CookieBar.build(this)
                        .setTitle("Menu: Informasi")
                        .setMessage("Fitur ini belum tersedia")
                        .setBackgroundColor(R.color.colorPrimary)
                        .setLayoutGravity(Gravity.BOTTOM)
                        .show();
                break;
        }
    }

    public void checkPermission() {
        // Storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
    }

}
