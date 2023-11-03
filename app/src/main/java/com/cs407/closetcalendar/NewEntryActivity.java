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

public class NewEntryActivity extends AppCompatActivity {

    private int viewID=-1;
    private int draftID=-1;
    private int year=-1;
    private int month=-1;
    private int day=-1;
    private String outfit=null;
    private String location=null;
    private String temps=null;
    private String weather=null;
    private String comment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        //get selected date from CalendarMain upon NewEntry  (no draft ID or view ID)
        Intent intent = getIntent();
        year=intent.getIntExtra("year", -1);
        month=intent.getIntExtra("month", -1);
        day=intent.getIntExtra("day", -1);


        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        viewID = sharedPreferences.getInt("viewIDKey", -1); // extract viewID if exist from ViewActivity, otherwise viewID defaults to -1 (NewMode)
        draftID = sharedPreferences.getInt("draftIDKey", -1); // extract draftID if exist (EditMode), otherwise draftID defaults to -1 (NewMode)


        // display draft Entry's data  "From Closet or Camera"
        if(draftID!=-1){
            //display draft entry's data
            displayEntryData(draftID);
        }
        // display database view Entry's data (no draft edits yet) "From ViewActivity"
        else if (viewID!=-1) {
            //display view entry's data
            displayEntryData(viewID);
        }
        //else the xml displays default values onCreate (no edits made so far) "From CalendarMain"

    }

    public void displayEntryData(int id){

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        Entry entry=dbHelper.getEntryById(id);

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


    /*TODO add a current location button to icon*/
    /*TODO add a current temp button to icon*/


    public void onClickOutfitImage(View view){
        Intent intent = new Intent(this, ChooseClosetActivity.class);

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        //save edit progress by storing/updating a draft entry to the database
        //if a draft entry doesn't exist, make a new draft entry (NewEntry->new draftID)
        if(draftID==-1){

            //TODO update class variables based on the all ID cases before calling newEntry

            //update the database with a new entry row with the values the user entered into the EditViews so far
            draftID= dbHelper.newEntry(year, month, day, outfit, location, temps, weather, comment);

            //update shared preferences with this new draftID
            SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt("draftIDKey", draftID).apply(); // set a key as the entry's draftID int

        }
        // draft entry already exists, so update draft entry (updateEntry-same draftID)
        else {
            //TODO update class variables based on the all ID cases before calling updateEntry
            // TODO dbHelper.updateEntry(draftID, year, month, day, outfit, location, temps, weather, comment);
        }

        startActivity(intent);
    }


    public void onClickSaveButton(View view){ //TODO whole function not finished yet

        //Determine whether (newEntry-> new database ID) o  (updateEntry ->keep same viewID)


        goToCalendarMainActivity();

    }


    public void onClickExitButton(View view){

        //do not update/save draft entry to database, instead delete draftEntry (if exists) from database
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        //TODO dbHelper.deleteEntry(draftID);

        goToCalendarMainActivity();

    }

    public void goToCalendarMainActivity() {

        Intent intent = new Intent(this, CalendarMainActivity.class);

        // delete sharedpreferences to set back to default when going back to CalendarMain
        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("viewIDKey").apply();
        sharedPreferences.edit().remove("draftIDKey").apply();

        startActivity(intent);
    }
}