package com.zeafan.task;

import com.zeafan.model.Weather;

public interface IWeatherView {
    void onGetDataForDialog(Weather weather);

    void OnSetBitMap(Weather myBitmap);
}
