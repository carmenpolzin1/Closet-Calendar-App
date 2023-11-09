package com.cs407.closetcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarMainActivity extends AppCompatActivity {

    CalendarView calendarView;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    Calendar calendar;

    BottomNavigationItemView bottomNavigationItemView;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cal_main);

        calendarView = findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {

                // TODO When a date is selected, move the highlighted bubble and update selected year, date, and month
                //String[] dates = getDate();
                //Log.i("info", "this is year " + year);
                //Log.i("info", "this is month " + month);
                //Log.i("info", "this is day " + day);
                selectedYear = year;
                selectedMonth = month;
                selectedDay = day;

                // create instance of database
                DBHelper dbHelper = new DBHelper(getApplicationContext());

                // Check if there's already an entry for the selected date
                Entry existingEntry = dbHelper.getEntryByDate(year, month, day);

                if (existingEntry != null) {
                    // launch the ViewEntryActivity if entry exists (pass the viewID)  "EditMode"
                    Intent intent = new Intent(CalendarMainActivity.this, ViewEntryActivity.class);

                    SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putInt("viewIDKey", existingEntry.getID()).apply(); // set a key as the entry's viewID int
                    sharedPreferences.edit().putInt("yearKey", selectedYear).apply(); // set a key as selected year
                    sharedPreferences.edit().putInt("monthKey", selectedMonth).apply(); // set a key as selected month
                    sharedPreferences.edit().putInt("dayKey", selectedDay).apply(); // set a key as selected day

                    startActivity(intent);
                }

            }
        });

        /*
        bottomNavigationItemView = findViewById(R.id.bottomNav);
        bottomNavigationItemView.setSelectedItemId(R.id.calendar);
        bottomNavigationItemView.setOnNavigationItemSelectedListener(new BottomNavigationItemView.OnsetOnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int itemId = item.getItemId();
                if(itemId == R.id.closet){
                    Intent intent = new Intent(this, ClosetActivity.class);
                    startActivity(intent);
                    return true;
                }
                return super.onOptionsItemSelected(item);
            }
        });

         */
    }

    public void onClickAddButton(View view){
        /*TODO make sure to not make an intent if there is already an existing entry*/

        // launch the NewEntryActivity (update sharedPrefences with Calendar's selected date values)
        Intent intent = new Intent(this, NewEntryActivity.class);

        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("yearKey", selectedYear).apply(); // set a key as selected year
        sharedPreferences.edit().putInt("monthKey", selectedMonth).apply(); // set a key as selected month
        sharedPreferences.edit().putInt("dayKey", selectedDay).apply(); // set a key as selected day

        startActivity(intent);
    }

    public void goToCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //Button todayButton = findViewById(R.id.todayButton);


    public void todayClick(View view){
        // TODO get the current day, month, and year
        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String selected_date = dateFormat.format(calendar2.getTime());
        String dates[] = selected_date.split("/");
        //Log.i("info", "this is month " + Integer.valueOf(dates[0]));//this is month
        //Log.i("info", "this is day " + Integer.valueOf(dates[1])); // this is day
        //Log.i("info", "this is year " + Integer.valueOf(dates[2])); // this is year
        setDate(Integer.valueOf(dates[0]),Integer.valueOf(dates[1]),Integer.valueOf(dates[2]));

    }

    public void setDate(int month, int day, int year){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long milli = calendar.getTimeInMillis();
        calendarView.setDate(milli);

    }

    /*
    public String[] getDate(){
        long date = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/DD/YYYY", Locale.getDefault());
        calendar.setTimeInMillis(date);
        String selected_date = simpleDateFormat.format(calendar.getTime());
        //ArrayList<Integer> dates = new ArrayList<Integer>();
        String dates[] = selected_date.split("/");
        return dates;
        }

     */

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int itemId = item.getItemId();
        if(itemId == R.id.closet){
            Log.i("message", "here");
            Intent intent = new Intent(this, ClosetActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

     */


}