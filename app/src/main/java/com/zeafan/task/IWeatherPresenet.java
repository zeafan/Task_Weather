package com.zeafan.task;

import android.graphics.Bitmap;

import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.zeafan.model.Weather;

import java.util.ArrayList;

public interface IWeatherPresenet {
    void TakeImage();

    void CreateInfoDialog(Bitmap imageBitmap);

    void Share(Weather _weather, ShareDialog shareDialog,CallbackManager callbackManager);

    void SaveBitMap(Weather weather);

    ArrayList<Weather> GetWeather();

   String saveToInternalStorage(Bitmap bitmapImage,String id);

    void ShowChoiceDialog(ArrayList<Weather> allWeather);
}
