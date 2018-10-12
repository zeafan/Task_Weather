package com.zeafan.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.zeafan.model.Weather;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class WeatherPresenter  implements IWeatherPresenet {
    IWeatherView IWeatherview;
    Activity activity;
    private final String FileSharePrference_key="historyFile";
    final static int REQUEST_IMAGE_CAPTURE=1001;
    public WeatherPresenter(WeatherActivity weatherActivity) {
        IWeatherview=weatherActivity;
        activity=weatherActivity;
    }

    @Override
    public void TakeImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void CreateInfoDialog(final Bitmap imageBitmap) {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(activity);
        dialog.setCancelable(false);
        final View view= LayoutInflater.from(activity).inflate(R.layout.dialog_info,null);
        final EditText edPlace=view.findViewById(R.id.edplace);
        final EditText edtemp=view.findViewById(R.id.edtemp);
        final EditText edcon=view.findViewById(R.id.con);
        Button btn=view.findViewById(R.id.btn_ok);
        dialog.setView(view);
        final AlertDialog ad=dialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edcon.getText().equals("")&&!edtemp.getText().equals("")&&!edPlace.getText().equals(""))
                {
                    // send data to activity

                    Weather weather=new Weather(edcon.getText().toString(),edtemp.getText().toString(),edPlace.getText().toString(),imageBitmap, UUID.randomUUID().toString(),"");
                    IWeatherview.onGetDataForDialog(weather);

                }else {
                    Toast.makeText(activity, ""+activity.getResources().getString(R.string.please), Toast.LENGTH_SHORT).show();
                }
                ad.dismiss();
            }
        });




    }

    @Override
    public void Share(Weather _weather, ShareDialog shareDialog,CallbackManager callbackManager) {
        SharePhoto sharePhoto=new SharePhoto.Builder().setBitmap(_weather.getBitmap()).build();
        if(ShareDialog.canShow(SharePhotoContent.class))
        {
            SharePhotoContent content=new SharePhotoContent.Builder().addPhoto(sharePhoto).build();
            shareDialog.show(content);
        }
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(activity, ""+activity.getResources().getString(R.string.seccussful), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(activity, ""+activity.getResources().getString(R.string.cancel), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(activity, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public String saveToInternalStorage(Bitmap bitmapImage,String id){
        FileOutputStream fos = null;
        File Direc=mkFolderImg();
        File fileImage=new File(Direc,id+".jpeg");
        try {
            fos = new FileOutputStream(fileImage);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileImage.getAbsolutePath();
    }

    @Override
    public void ShowChoiceDialog(final ArrayList<Weather> allWeather) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
        builderSingle.setTitle("Select One Name:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_singlechoice);
        for(Weather w:allWeather)
        {
            arrayAdapter.add(w.getGuid()+"-"+w.getGuid());
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Weather obj = allWeather.get(which);
                File imgFile = new  File(obj.getPthImg());

                if(imgFile.exists()){

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    obj.setBitmap(myBitmap);
                    IWeatherview.OnSetBitMap(obj);

                }
            }
        });
        builderSingle.show();
    }

    public File mkFolderImg()
    {
        File Folder =new File("sdcard/Image folder");
        if(!Folder.exists())
        {
            Folder.mkdir();
        }
        return Folder;
    }
    @Override
    public void SaveBitMap(Weather weather) {
        SharedPreferences shareP=activity.getSharedPreferences(FileSharePrference_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=shareP.edit();
        Gson gson = new Gson();

        Weather w=new Weather(weather.getPlace(),weather.getTemp(),weather.getCond(),null,weather.getGuid(),weather.getPthImg());
        String json = gson.toJson(w);
        edit.putString(weather.getGuid(), json);
        String Allkeys=shareP.getString("keys","");
        if(!Allkeys.equals(""))
        {
            Allkeys+="&&"+weather.getGuid();
        }else {
            Allkeys=weather.getGuid();
        }
        edit.putString("keys",Allkeys);
        edit.apply();
        edit.commit();
    }

    @Override
    public ArrayList<Weather> GetWeather() {

            ArrayList<Weather> Weathers = new ArrayList<>();
        try {
            SharedPreferences shareP = activity.getSharedPreferences(FileSharePrference_key, Context.MODE_PRIVATE);
            String Allkeys = shareP.getString("keys", "");
            String[] keys = Allkeys.split("&&");
            for (String k : keys) {
                Gson gson = new Gson();
                String json = shareP.getString(k, "");
                Weather obj = gson.fromJson(json, Weather.class);
                Weathers.add(obj);
            }
        } catch (Exception e)
        {
            Toast.makeText(activity, "There Error", Toast.LENGTH_SHORT).show();
        }
        return Weathers;
    }
}
