package com.visitormanagement.gspe.scankartu.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GenericCard implements Parcelable {

    private int id;
    private String type;
    private String content;
    private String imagePath;

    public GenericCard() {
    }

    public GenericCard(String content) {
        this.content = content;
    }

    public GenericCard(String type, String content, String imagePath) {
        this.type = type;
        this.content = content;
        this.imagePath = imagePath;
    }

    private GenericCard(Parcel in) {
        id = in.readInt();
        type = in.readString();
        content = in.readString();
        imagePath = in.readString();
    }

    public static final Creator<GenericCard> CREATOR = new Creator<GenericCard>() {
        @Override
        public GenericCard createFromParcel(Parcel in) {
            return new GenericCard(in);
        }

        @Override
        public GenericCard[] newArray(int size) {
            return new GenericCard[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(type);
        parcel.writeString(content);
        parcel.writeString(imagePath);
    }
}
