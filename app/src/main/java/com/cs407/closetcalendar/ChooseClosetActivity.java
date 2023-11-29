package com.cs407.closetcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

public class ChooseClosetActivity extends AppCompatActivity {

    private int draftID=-1;
    private String outfit=null;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_closet);

        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        draftID = sharedPreferences.getInt("draftIDKey", -1); // extract draftID (should exist)

    }

    public void onClickAlbumButtonChoose(View view){
        //TODO inflate the photo picker
    }

    public void onClickCameraButtonChoose(View view){
        //TODO open the camera activity, and save capture to outfit string
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        //outfit=camera capture pathway
    }

    public void onClickExitButtonChoose(View view){
        Intent intent = new Intent(this, NewEntryActivity.class);

        //don't save outfit string to the draftID database

        startActivity(intent);
    }

    public void onClickSaveButtonChoose(View view){
        Intent intent = new Intent(this, NewEntryActivity.class);

        //save the outfit string to the draftID database
        if(outfit!=null){
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            dbHelper.updateOutfit(draftID, outfit);
        }

        startActivity(intent);
    }
}