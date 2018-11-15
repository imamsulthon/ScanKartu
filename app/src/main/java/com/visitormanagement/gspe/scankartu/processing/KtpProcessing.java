package com.visitormanagement.gspe.scankartu.processing;

import android.content.Context;
import android.graphics.Rect;
import android.widget.Toast;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.visitormanagement.gspe.scankartu.model.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class KtpProcessing {

    public HashMap<String, String> processText(FirebaseVisionText firebaseVisionText, Context context) {
        List<FirebaseVisionText.Block> blocks = firebaseVisionText.getBlocks();

        if (blocks.size() == 0) {
            Toast.makeText(context, "No text!", Toast.LENGTH_LONG).show();
        }

        TreeMap<String, String> map = new TreeMap<>();
        for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks()) {
            for (FirebaseVisionText.Line line: block.getLines()) {
                Rect rect = line.getBoundingBox();
                String y = String.valueOf(rect.exactCenterY());
                String lineText = line.getText();
                map.put(y, lineText);
            }
        }

        HashMap<String, String> hashMap = new HashMap<>();
        List<String> orderedData = new ArrayList<>(map.values());
        int i = 0;
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains("/")) {
                hashMap.put("dob", orderedData.get(i));
                break;
            }
        }

        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains("-")) {
                hashMap.put("ttl", orderedData.get(i));
            }
        }

        if (i - 1 > 1) {
            hashMap.put("fatherName", orderedData.get((i-1)));
        }

        if (i - 2 > -1) {
            hashMap.put("name", orderedData.get(i-2));
        }

        String regex="\\w\\w\\w\\w\\w\\d\\d\\d\\d.*";
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).matches(regex)) {
                hashMap.put("pan", orderedData.get(i));
            }
        }
        return hashMap;
    }

    public HashMap<String, String> processingText(FirebaseVisionText firebaseVisionText, Context context) {
        List<FirebaseVisionText.Block> blocks = firebaseVisionText.getBlocks();

        if (blocks.size() == 0) {
            Toast.makeText(context, "No text!", Toast.LENGTH_LONG).show();
        }

        TreeMap<String, String> map = new TreeMap<>();
        for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks()) {
            for (FirebaseVisionText.Line line: block.getLines()) {
                Rect rect = line.getBoundingBox();
                String y = String.valueOf(rect.exactCenterY());
                String lineText = line.getText();
                map.put(y, lineText);
            }
        }

        HashMap<String, String> hashMap = new HashMap<>();
        List<String> orderedData = new ArrayList<>(map.values());

        hashMap.put(Constants.name, orderedData.get(0));
        hashMap.put(Constants.id, orderedData.get(orderedData.size() - 1));

        for (int i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains("LAKI-LAKI")) {
                hashMap.put(Constants.gender, "Laki-laki");
                break;
            } else if (orderedData.get(i).contains("PEREMPUAN")) {
                hashMap.put(Constants.gender, "Perempuan");
                break;
            }
        }

        for (int i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains("KAWIN")) {
                hashMap.put(Constants.marital, "Kawin");
                break;
            } else if (orderedData.get(i).contains("BELUM KAWIN")) {
                hashMap.put(Constants.marital, "Belum Kawin");
                break;
            }
        }

        for (int i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains("Berlaku")) {
                hashMap.put(Constants.dob, orderedData.get(i));
            }
        }

        for (int i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains("Pekerjaan")) {
                hashMap.put(Constants.job, orderedData.get(i));
            }
        }

        return hashMap;
    }
}
