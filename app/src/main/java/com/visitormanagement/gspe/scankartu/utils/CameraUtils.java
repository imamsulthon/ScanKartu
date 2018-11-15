package com.visitormanagement.gspe.scankartu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraUtils {

    public static final String PICTURE_DIRECTORY = Environment.getExternalStorageDirectory()
            + File.separator + "DCIM" + File.separator + "Scankartu" + File.separator;

    public static String generateImageName(String cardType) {
        // Append the current date and time to the image name.
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        return "expenseNotes_" + cardType + "_" + timeStamp + ".jpg";
    }

    public static File createImageFile( Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static Bitmap getBitmap(String path) {
        try {

            File f= new File(path);
            Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }}
}
