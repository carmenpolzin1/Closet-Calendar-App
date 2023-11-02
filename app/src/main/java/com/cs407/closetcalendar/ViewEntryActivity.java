package com.cs407.closetcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ViewEntryActivity extends AppCompatActivity {

    private int entryID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        Intent intent=getIntent();
        entryID=intent.getIntExtra("entryID", -1);

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        // If Entry's ID does not get passed correctly, display error text
        if(entryID==-1){
            TextView dateEntryTextView=findViewById(R.id.dateEntryTextView);
            dateEntryTextView.setText("EntryID Error");
        }
        //otherwise display Entry's data
        else {
            Entry entry=dbHelper.getEntryById(entryID);
            /* TODO display entry's data */
        }


    }

    public void onClickExitButton(){
        Intent intent = new Intent(this, CalendarMainActivity.class);
        startActivity(intent);
    }

    /*TODO add a delete button*/

    public void onClickEditButton(){
        Intent intent = new Intent(this, NewEntryActivity.class);
        intent.putExtra("entryID", entryID);
        startActivity(intent);
    }
}