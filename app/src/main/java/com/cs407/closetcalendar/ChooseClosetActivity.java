package com.cs407.closetcalendar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ChooseClosetActivity extends AppCompatActivity {

    private int draftID=-1;
    private String outfit=null;


    private ImageView imageView; // ImageView for the outfit
    private Uri pickedImageUri; // Uri of the image of the outfit chosen

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    Glide.with(getApplicationContext()).load(uri).into(imageView);
                    this.pickedImageUri = uri;
                    this.outfit = uri.toString();
                } else {
                    Log.d("PhotoPicker", "No outfit selected");
                }
            });
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_closet);

        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        draftID = sharedPreferences.getInt("draftIDKey", -1); // extract draftID (should exist)

        imageView = findViewById(R.id.outfitImageViewChoose);
    }


    /**
     * Launches the Android Photo Picker where the user can select 1 photo to be uploaded.
     *
     * @param view
     */
    public void onClickAlbumButtonChoose(View view){
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
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