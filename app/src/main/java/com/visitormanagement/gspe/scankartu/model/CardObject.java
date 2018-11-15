package com.visitormanagement.gspe.scankartu.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class CardObject implements Parcelable {

    private int id;
    private String cardType;
    private String inputDate;
    private String contents;
    private String imagePath;

    public CardObject() {
    }

    public CardObject(String cardType, String inputDate, String contents, String imagePath) {
        this.cardType = cardType;
        this.inputDate = inputDate;
        this.contents = contents;
        this.imagePath = imagePath;
    }

    public CardObject(int id, String cardType, String inputDate, String contents, String imagePath) {
        this.id = id;
        this.cardType = cardType;
        this.inputDate = inputDate;
        this.contents = contents;
        this.imagePath = imagePath;
    }

    protected CardObject(Parcel in) {
        id = in.readInt();
        cardType = in.readString();
        inputDate = in.readString();
        contents = in.readString();
        imagePath = in.readString();
    }

    public static final Creator<CardObject> CREATOR = new Creator<CardObject>() {
        @Override
        public CardObject createFromParcel(Parcel in) {
            return new CardObject(in);
        }

        @Override
        public CardObject[] newArray(int size) {
            return new CardObject[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getInputDate() {
        return inputDate;
    }

    public void setInputDate(String inputDate) {
        this.inputDate = inputDate;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
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
        parcel.writeString(cardType);
        parcel.writeString(inputDate);
        parcel.writeString(contents);
        parcel.writeString(imagePath);
    }
}
