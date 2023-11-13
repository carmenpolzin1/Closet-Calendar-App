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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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
    private Entry existingEntry=null;

    BottomNavigationView bottomNavigationView;
    //BottomNavigationItemView bottomNavigationItemView;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cal_main);

        calendarView = findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();

        //onCreate,  set the date to today
        setTodayDate();

        // bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.calendar);

        // FOR FUTURE: should use fragments for bottom nav
        // https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.closet) {
                    startActivity(new Intent(getApplicationContext(), ClosetActivity.class));
                    return true;
                } else if (id == R.id.calendar){
                    return true;
                } else {
                    return false;
                }
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {

                //must increment month by 1 since zerobased from Listener
                selectedYear = year;
                selectedMonth = month+1;
                selectedDay = day;

//                Log.i("info", "this is year " + selectedYear);
//                Log.i("info", "this is month " + selectedMonth);
//                Log.i("info", "this is day " + selectedDay);

                //update which button shows for this date
                updateButtonSelection(selectedMonth,selectedDay,selectedYear);

            }
        });

        }



    public void updateButtonSelection(int month, int day, int year){

        // create instance of database
        DBHelper dbHelper = new DBHelper(getApplicationContext());

        // Check if there's already an entry for the selected date
        existingEntry = dbHelper.getEntryByDate(year, month, day);

        Button addButton = findViewById(R.id.addButton);
        Button viewButton = findViewById(R.id.viewButton);

        //Display the view or add button according to the selected date
        if (existingEntry != null) {
            // make the "+" Button disapear (can't make more than one entry on one day)
            addButton.setVisibility(View.GONE);
            // display the "View" Button (can only view the entry)
            viewButton.setVisibility(View.VISIBLE);
        } else {
            // make the "View" Button disapear (can't view a nonexisting entry)
            viewButton.setVisibility(View.GONE);
            // display the "+" Button (can only add an entry)
            addButton.setVisibility(View.VISIBLE);
        }
    }

    public void onClickViewButton(View view){

        // launch the ViewEntryActivity (update sharedPrefences with Calendar's selected date values and viewID)
        Intent intent = new Intent(this, ViewEntryActivity.class);

        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("viewIDKey", existingEntry.getID()).apply(); // set a key as the entry's viewID int
        sharedPreferences.edit().putInt("yearKey", selectedYear).apply(); // set a key as selected year
        sharedPreferences.edit().putInt("monthKey", selectedMonth).apply(); // set a key as selected month
        sharedPreferences.edit().putInt("dayKey", selectedDay).apply(); // set a key as selected day
//        Log.i("info", "onClickViewButton");
        startActivity(intent);
    }



    public void onClickAddButton(View view){

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
        // set the current day, month, and year on the Calendar
        setTodayDate();
    }

    public void setTodayDate(){
        //set the current day, month, and year on the Calendar
        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String selected_date = dateFormat.format(calendar2.getTime());
        String dates[] = selected_date.split("/");
//        Log.i("info", "this is month " + Integer.valueOf(dates[0]));//this is month
//        Log.i("info", "this is day " + Integer.valueOf(dates[1])); // this is day
//        Log.i("info", "this is year " + Integer.valueOf(dates[2])); // this is year
        setDate(Integer.valueOf(dates[0]),Integer.valueOf(dates[1]),Integer.valueOf(dates[2]));
    }

    public void setDate(int month, int day, int year){
        //set the specified date on the Calendar
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long milli = calendar.getTimeInMillis();
        calendarView.setDate(milli);

//        Log.i("info", "this is year " + year);
//        Log.i("info", "this is month " + month);
//        Log.i("info", "this is day " + day);

        //update class variables
        selectedYear = year;
        selectedMonth = month;
        selectedDay = day;

        //update which button shows for this date
        updateButtonSelection(month,day,year);

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