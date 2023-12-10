package com.cs407.closetcalendar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ChooseClosetActivity extends AppCompatActivity {

    private int draftID=-1;
    private String outfit=null;


    private ImageView imageView; // ImageView for the outfit
    private Uri pickedImageUri; // Uri of the image of the outfit chosen
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;

    private ImageButton cameraButton; // button to open camera

    // ActivityResultLauncher for handling camera result
    private final ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(), result -> {
                if (result != null) {
                    imageView.setImageBitmap(result);
                }
            }
    );

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
        cameraButton = findViewById(R.id.cameraButtonChoose);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ChooseClosetActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(ChooseClosetActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });

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

//    public void onClickCameraButtonChoose(View view){
//        //TODO open the camera activity, and save capture to outfit string
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//        //outfit=camera capture pathway
//    }

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


    // camera functionality
    public void openCamera() {
        // Using the new cameraLauncher to start the camera activity
        cameraLauncher.launch(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }
}