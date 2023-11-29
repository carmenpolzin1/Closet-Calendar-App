package com.cs407.closetcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ViewEntryActivity extends AppCompatActivity {

    private int viewID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        Log.i("info", "ViewEntryActivity onCreate");

        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        viewID = sharedPreferences.getInt("viewIDKey", -1); // extract viewID if exist (ViewMode), otherwise viewID defaults to -1 (Error)

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        // Display the Entry stored in ViewID (if passed correctly)
        if(viewID!=-1){

            Entry entry=dbHelper.getEntryById(viewID);

            // update the Layout with the class variable (Entry's data)
            TextView dateEntryTextView =findViewById(R.id.dateEntryTextView);
            dateEntryTextView.setText(entry.getMonth()+"/"+entry.getDay()+"/"+entry.getYear()+" Entry");

            ImageView outfitImageView =findViewById(R.id.outfitImageView);
            Uri outfitUri = Uri.parse(entry.getOutfit());
            Glide.with(getApplicationContext()).load(outfitUri).into(outfitImageView);

            TextView locationDescTextView =findViewById(R.id.locationDescTextView);
            locationDescTextView.setText(entry.getLocation());

            TextView tempTextView =findViewById(R.id.tempTextView);
            tempTextView.setText(entry.getTemps());

            TextView weatherDescTextView =findViewById(R.id.weatherDescTextView);
            weatherDescTextView.setText(entry.getWeather());

            TextView commentDescTextView =findViewById(R.id.commentDescTextView);
            commentDescTextView.setText(entry.getComment());
        }
        // Entry's viewID did not get passed correctly, display error text
        else {
            TextView dateEntryTextView=findViewById(R.id.dateEntryTextView);
            dateEntryTextView.setText("ViewID Error");
        }

    }

    public void onClickExitButton(View view){
        Intent intent = new Intent(this, CalendarMainActivity.class);

        // delete sharedpreferences to set back to default when going back to CalendarMain
        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("viewIDKey").apply();
        sharedPreferences.edit().remove("yearKey").apply();
        sharedPreferences.edit().remove("monthKey").apply();
        sharedPreferences.edit().remove("dayKey").apply();

        startActivity(intent);
    }


    public void onClickDeleteButton(View view){
        // TODO add a dialog to verify deletion first


        //delete the current ViewID's entry from the database, if viewID exists for sure
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        if(viewID!=-1){
            dbHelper.deleteEntry(viewID);
        }

        //go back to Calendar Main
        Intent intent = new Intent(this, CalendarMainActivity.class);

        // delete sharedpreferences to set back to default when going back to CalendarMain
        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("viewIDKey").apply();
        sharedPreferences.edit().remove("yearKey").apply();
        sharedPreferences.edit().remove("monthKey").apply();
        sharedPreferences.edit().remove("dayKey").apply();

        startActivity(intent);

    }

    public void onClickEditButton(View view){

        // If if viewID is valid, go to NewEntryActivity
        if(viewID!=-1){
            Intent intent = new Intent(this, NewEntryActivity.class);

            //viewID,year,month,and day should still be stored in SharedPreferences under viewID

            startActivity(intent);
        }
        //otherwise Entry's viewID did not get passed correctly, dont go to NewEntryActivity
        else {
           //button does nothing
        }



    }
}