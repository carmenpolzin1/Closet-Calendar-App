package com.cs407.closetcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class NewEntryActivity extends AppCompatActivity {

    private int entryID=-1;
    private int year=-1;
    private int month=-1;
    private int day=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        Intent intent=getIntent();
        entryID=intent.getIntExtra("entryID", -1); //for edit mode
        year=intent.getIntExtra("year", -1); //for new mode
        month=intent.getIntExtra("month", -1); //for new mode
        day=intent.getIntExtra("day", -1); //for new mode

        /*TODO chooseclosetactivity and cameraActivity must recieve and send entryID in intents*/

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        // display Entry's data if in EditMode
        if(entryID!=-1){
            Entry entry=dbHelper.getEntryById(entryID);
            /* TODO display entry's data */
        } //else the xml has default values onCreate for NewMode

        //set OnClickListener for saveButton
        ImageButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                int year = -1; /*TODO this will change between modes group dates together in if*/
                int month = -1; /*TODO this will change between modes group dates together in if*/
                int day = -1; /*TODO this will change between modes group dates together in if*/
                String outfit = null; /*TODO this needs to take from closet?*/

                EditText locationDescEditView = findViewById(R.id.locationDescEditView);
                String location = locationDescEditView.getText().toString(); /*TODO this will change between modes*/


                TextView tempTextView = findViewById(R.id.tempTextView);
                String temps = tempTextView.getText().toString(); /*TODO this will change between modes*/

                EditText weatherDescEditView = findViewById(R.id.weatherDescEditView);
                String weather = weatherDescEditView.getText().toString();


                EditText notesEditView = findViewById(R.id.notesEditView);
                String comment = notesEditView.getText().toString();


                //update the database with the values the user entered into the EditViews
                entryID= dbHelper.saveEntry(year, month, day, outfit, location, temps, weather, comment);

                /*TODO may not need to update entryID from save entry aka fix dbhelper method of it*/

                goToCalendarMainActivity();
            }
        });
    }

    public void onClickExitButton(){
        Intent intent = new Intent(this, CalendarMainActivity.class);
        startActivity(intent);
    }

    /*TODO add a delete button*/

    /*TODO add a current location button to icon*/
    /*TODO add a current temp button to icon*/

    public void onClickOutfitImage(){
        Intent intent = new Intent(this, ChooseClosetActivity.class);
        intent.putExtra("entryID",entryID);
        startActivity(intent);
    }

    public void goToCalendarMainActivity() {
        Intent intent = new Intent(this, CalendarMainActivity.class);
        startActivity(intent);
    }
}