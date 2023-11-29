package com.cs407.closetcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.List;

public class ClosetActivity extends AppCompatActivity {
    private DBHelper dbHelper;

    // Variables to hold the gallery images in activity_closet.xml
    private TableLayout imageContainer;
    private TableRow currentRow;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    //Glide.with(getApplicationContext()).load(uri).into(imageView);
                    handlePickedImage(uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);

        // bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.closet);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.calendar) {
                    startActivity(new Intent(getApplicationContext(), CalendarMainActivity.class));
                    return true;
                } else if (id == R.id.closet){
                    return true;
                } else {
                    return false;
                }
            }
        });
        Log.i("info", "ClosetActivity onCreate");

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        // TODO Used database to get users saved images to display

        // Handle Add from Camera Button
        currentRow = new TableRow(this);
        imageContainer = findViewById(R.id.imageTableLayout);
        Button pickImage = findViewById(R.id.camRollButton);

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("info", "Camera Button Clicked!");
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
    }

    /**
     *
     *
     * @param pickedImageUri Uri of the image file to be uploaded to the Closet gallery
     */
    private void handlePickedImage(Uri pickedImageUri) {
        // Check if the current row is full or doesn't exist
        if (currentRow.getChildCount() == 3 || currentRow.getParent() == null) {
            // Create a new row for the next set of images
            currentRow = new TableRow(this);

            // Add the new row to the TableLayout
            imageContainer.addView(currentRow);
        }

        // Create a new ImageView for the picked image
        ImageView imageView = new ImageView(this);

        // Use Glide to load the image into the ImageView
        Glide.with(this).load(pickedImageUri).into(imageView);

        // Set layout parameters for the image
        TableRow.LayoutParams imageParams = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1
        );
        imageView.setLayoutParams(imageParams);

        // Add the ImageView to the current row
        currentRow.addView(imageView);
    }

    public void loadImages(View view) {

    }

    public void addFromCameraRoll(View view) {
//        Log.i("info", "INFO: CameraButton clicked!");
        // Launch the photo picker and let the user choose only images.
//        pickMedia.launch(new PickVisualMediaRequest.Builder()
//                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
//                .build());
    }
}