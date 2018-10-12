package com.zeafan.model;

import android.graphics.Bitmap;

public class Weather {
    String Guid;
   private String place;
    private String temp;
    private String cond;
    private Bitmap bitmap;
    private String PthImg;

    public Weather(String place, String temp, String cond,Bitmap bitmap,String Guid,String Path) {
        this.place = place;
        this.temp = temp;
        this.cond = cond;
        this.bitmap=bitmap;
        this.Guid=Guid;
        this.PthImg=Path;
    }

    public String getGuid() {
        return Guid;
    }

    public void setGuid(String guid) {
        Guid = guid;
    }

    public String getPthImg() {
        return PthImg;
    }

    public void setPthImg(String pthImg) {
        PthImg = pthImg;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPlace() {
        return place;
    }

    public String getTemp() {
        return temp;
    }

    public String getCond() {
        return cond;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}
