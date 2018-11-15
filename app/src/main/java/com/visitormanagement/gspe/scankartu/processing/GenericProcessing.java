package com.visitormanagement.gspe.scankartu.processing;

import android.content.Context;
import android.graphics.Rect;
import android.widget.Toast;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class GenericProcessing {

    public HashMap<String,String> processText(FirebaseVisionText text, Context context) {

        List<FirebaseVisionText.Block> blocks = text.getBlocks();

        if (blocks.size() == 0) {
            Toast.makeText(context, "No text!", Toast.LENGTH_LONG).show();
            return null;
        }

        StringBuilder textBuilder = new StringBuilder();
        TreeMap<String,String> map = new TreeMap<>();

        for (FirebaseVisionText.Block block: text.getBlocks()) {
            for(FirebaseVisionText.Line line: block.getLines()){
                Rect rect = line.getBoundingBox();
                String y = String.valueOf(rect.exactCenterY());
                String lineTxt = line.getText();
                map.put(y,lineTxt);
            }
        }

        for(TreeMap.Entry<String ,String > entry:map.entrySet()){
            textBuilder.append(entry.getValue()+"\n");
        }

        HashMap<String ,String> dataMap = new HashMap<>();
        dataMap.put("text",textBuilder.toString());
        return dataMap;
    }

    public String getFullString(FirebaseVisionText text) {
        String fullText = String.valueOf(text);
        return fullText;
    }
}
