package com.cs407.closetcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewEntryActivity extends AppCompatActivity {

    private int viewID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        viewID = sharedPreferences.getInt("viewIDKey", -1); // extract viewID if exist (ViewMode), otherwise viewID defaults to -1 (Error)

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        // If Entry's viewID does not get passed correctly, display error text
        if(viewID==-1){
            TextView dateEntryTextView=findViewById(R.id.dateEntryTextView);
            dateEntryTextView.setText("ViewID Error");
        }
        //otherwise display Entry's data
        else {
            Entry entry=dbHelper.getEntryById(viewID);

            // update the Layout with the class variable (Entry's data)
            TextView dateEntryTextView =findViewById(R.id.dateEntryTextView);
            dateEntryTextView.setText(entry.getMonth()+"/"+entry.getDay()+"/"+entry.getYear()+" Entry");

            ImageView outfitImageView =findViewById(R.id.outfitImageView);
            //TODO change outfit image from passed string

            EditText locationDescEditView =findViewById(R.id.locationDescEditView);
            locationDescEditView.setText(entry.getLocation());

            TextView tempTextView =findViewById(R.id.tempTextView);
            tempTextView.setText(entry.getTemps());

            EditText weatherDescEditView =findViewById(R.id.weatherDescEditView);
            weatherDescEditView.setText(entry.getWeather());

            EditText commentEditText =findViewById(R.id.commentEditText);
            commentEditText.setText(entry.getComment());

        }

    }

    public void onClickExitButton(View view){
        Intent intent = new Intent(this, CalendarMainActivity.class);

        // delete sharedpreferences to set back to default when going back to CalendarMain
        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("viewIDKey").apply();

        startActivity(intent);
    }

    /*TODO add a deleteEntry button*/

    public void onClickEditButton(View view){

        // If if viewID is valid, go to NewEntryActivity
        if(viewID!=-1){
            Intent intent = new Intent(this, NewEntryActivity.class);

            //viewID should still be stored in SharedPreferences under viewID

            startActivity(intent);
        }
        //otherwise Entry's viewID did not get passed correctly, dont go to NewEntryActivity
        else {
           //button does nothing
        }



    }
}