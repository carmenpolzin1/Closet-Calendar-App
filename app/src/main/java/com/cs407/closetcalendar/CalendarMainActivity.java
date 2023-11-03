package com.cs407.closetcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

public class CalendarMainActivity extends AppCompatActivity {


    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cal_main);

        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {

                // TODO When a date is selected, move the highlighted bubble and update selected year, date, and month


                // create instance of database
                DBHelper dbHelper = new DBHelper(getApplicationContext());

                // Check if there's already an entry for the selected date
                Entry existingEntry = dbHelper.getEntryByDate(year, month, day);

                if (existingEntry != null) {
                    // launch the ViewEntryActivity if entry exists (pass the viewID)  "EditMode"
                    Intent intent = new Intent(CalendarMainActivity.this, ViewEntryActivity.class);

                    SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putInt("viewIDKey", existingEntry.getID()).apply(); // set a key as the entry's viewID int

                    startActivity(intent);
                }

            }
        });
    }

    public void onClickAddButton(View view){
        /*TODO make sure to not make an intent if there is already an existing entry*/

        // launch the NewEntryActivity (pass the Calendar's selected date values)
        Intent intent = new Intent(this, NewEntryActivity.class);
        intent.putExtra("year", selectedYear);
        intent.putExtra("month", selectedMonth);
        intent.putExtra("day", selectedDay);
        startActivity(intent);
    }

    public void goToCamera(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}