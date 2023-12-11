package com.cs407.closetcalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;


import com.bumptech.glide.Glide;



public class ClosetActivity extends AppCompatActivity {
    private DBHelper dbHelper;

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

        dbHelper = new DBHelper(getApplicationContext());
        Cursor cursor = dbHelper.getOutfitCursor();
        loadImageGallery(cursor);
    }


    /**
     * Loads all the outfit images from the database entries into one gallery
     * @param cursor Cursor for the outfit column of the database
     */
    public void loadImageGallery(Cursor cursor) {
        int columnCount = 2; // Number of columns in each row in the gallery
        int imageSizeInDp = 150; // set image size
        int imageSizeInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imageSizeInDp, getResources().getDisplayMetrics());
        TableLayout tableLayout = findViewById(R.id.imageTableLayout);

        // Parse through database and load outfit images in closet
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int colIdx = cursor.getColumnIndex("outfit");
                String outfitImagePath = cursor.getString(colIdx);

                ImageView imageView = new ImageView(this);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        imageSizeInPx,
                        imageSizeInPx
                );
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // Crop the image to fit

                // Load and display the image
                Glide.with(getApplicationContext())
                        .load(outfitImagePath)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // Handle failure
                                Log.d("GlideDebug", "Image failed at: " + outfitImagePath);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // Handle successful loading
                                Log.d("GlideDebug", "Image successfully loaded at: " + outfitImagePath);
                                return false;
                            }
                        })
                        .into(imageView);

                // Create a new TableRow after every 'columnCount' ImageView
                if (cursor.getPosition() % columnCount == 0) {
                    TableRow tableRow = new TableRow(this);
                    tableLayout.addView(tableRow);
                }

                // Add the ImageView to the current TableRow
                TableRow currentRow = (TableRow) tableLayout.getChildAt(tableLayout.getChildCount() - 1);
                currentRow.addView(imageView);

            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
