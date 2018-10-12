package com.zeafan.task;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.zeafan.model.Weather;

import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity implements IWeatherView {
Button btnTakeImg;
     TextView tvPlace;
     TextView tvtemp;
     TextView tvcon;
     ImageView img;
     Weather _weather;
    CallbackManager callbackManager;

    ShareDialog shareDialog;
     RelativeLayout Relative_ScreenShot;
IWeatherPresenet presenet;
   final  int Perimession_Key=1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IniliazeUI();

        btnTakeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=23) {
                    if (ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,

                        }, Perimession_Key);
                    }else {
                        presenet.TakeImage();
                    }
                }else {
                    presenet.TakeImage();
                }
            }
        });
    }
void IniliazeUI()
{
    FacebookSdk.sdkInitialize(this.getApplicationContext());
    btnTakeImg=findViewById(R.id.btn_takeImage);
    presenet=new WeatherPresenter(this);
     tvPlace=findViewById(R.id.tv_place);
     tvtemp=findViewById(R.id.tv_temp);
     tvcon=findViewById(R.id.condition);
     img=findViewById(R.id.image);
     Relative_ScreenShot=findViewById(R.id.Relative_ScreenShot);
     callbackManager=CallbackManager.Factory.create();
     shareDialog=new ShareDialog(this);
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == WeatherPresenter.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if(imageBitmap!=null)
            {
                presenet.CreateInfoDialog(imageBitmap);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_menu:
                if(_weather==null)
                {
                    return false;
                }
                presenet.Share(_weather,shareDialog,callbackManager);
                break;
            case R.id.history_menu:
                ArrayList<Weather> AllWeather=presenet.GetWeather();
               presenet.ShowChoiceDialog(AllWeather);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==Perimession_Key)
        {
           presenet.TakeImage();

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onGetDataForDialog(Weather weather) {
        tvPlace.setText(getResources().getString(R.string.place)+ weather.getPlace());
        tvtemp.setText(getResources().getString(R.string.temp)+weather.getTemp());
        tvcon.setText(getResources().getString(R.string.condition)+weather.getCond());
        img.setImageBitmap(weather.getBitmap());
        Bitmap bitmap=Bitmap.createBitmap(Relative_ScreenShot.getMeasuredWidth(),Relative_ScreenShot.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c=new Canvas(bitmap);
        Relative_ScreenShot.draw(c);
        weather.setBitmap(bitmap);
        String path= presenet.saveToInternalStorage(bitmap,weather.getGuid());
        weather.setPthImg(path);
        presenet.SaveBitMap(weather);
        _weather=weather;
    }

    @Override
    public void OnSetBitMap(Weather weather) {
        _weather=weather;
        tvPlace.setText(getResources().getString(R.string.place)+ weather.getPlace());
        tvtemp.setText(getResources().getString(R.string.temp)+weather.getTemp());
        tvcon.setText(getResources().getString(R.string.condition)+weather.getCond());
        img.setImageBitmap(weather.getBitmap());
    }
}
