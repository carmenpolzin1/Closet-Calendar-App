package com.cs407.closetcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;

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

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        // TODO Used database to get users saved images to display

        // Handle Add from Camera Roll Button
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
     * Adds each outfit image to the Table in the Closet View
     * Todo - adds these to the database
     *
     * @param pickedImageUri Uri of the image file to be uploaded to the Closet gallery
     */
    private void handlePickedImage(Uri pickedImageUri) {
        // Check if the current row is full or doesn't exist
        if (currentRow.getChildCount() == 3 || currentRow.getParent() == null) {
            // Create a new row for the next set of images and dd the new row to the TableLayout
            currentRow = new TableRow(this);
            imageContainer.addView(currentRow);
        }

        // create new imageview for the outfit image picked
        ImageView imageView = new ImageView(this);
        Glide.with(this).load(pickedImageUri).into(imageView);

        // Set layout parameters for the image TODO make this a class variable
        TableRow.LayoutParams imageParams = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1
        );
        imageView.setLayoutParams(imageParams);

        // Add the ImageView to the current row
        currentRow.addView(imageView);
    }

    public void loadImageGallery(Cursor cursor) {
        // TODO Parse through database and load outfit images in closet
        // need to dynamically create ImageView for each outfit
        /**
        // Assuming you have an ImageView in your layout
        ImageView imageView = findViewById(R.id.imageView);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int colIdx = cursor.getColumnIndex("image_path");
                String imagePath = cursor.getString(colIdx);

                // Load and display the image using a library like Glide
                Glide.with(this).load(imagePath).into(imageView);

                // You may need to handle multiple images in your gallery
                // For simplicity, this example uses a single ImageView
            } while (cursor.moveToNext());

            cursor.close();
        }
         **/

    }
}