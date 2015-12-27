package ru.ifmo.android_2015.citycam;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import ru.ifmo.android_2015.citycam.model.City;

public class WebCam implements Parcelable {

    private Bitmap bitmap;
    private String title;
    private double rate;

    public WebCam(Bitmap bitmap, double rate, String title) {
        this.bitmap = bitmap;
        this.rate = rate;
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getTitle() {
        return title;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(bitmap, flags);
        dest.writeString(title);
        dest.writeDouble(rate);
    }

    protected WebCam(Parcel parcel) {
        bitmap = parcel.readParcelable(null);
        title = parcel.readString();
        rate = parcel.readDouble();
    }

    public static final Creator<WebCam> CREATOR = new Creator<WebCam>() {
        @Override
        public WebCam createFromParcel(Parcel source) {
            return new WebCam(source);
        }

        @Override
        public WebCam[] newArray(int size) {
            return new WebCam[size];
        }
    };
}

