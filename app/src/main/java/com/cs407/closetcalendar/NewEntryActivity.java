package com.cs407.closetcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewEntryActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;

    private int viewID=-1;
    private int draftID=-1;
    private int year=-1;
    private int month=-1;
    private int day=-1;
    private String outfit=null;
    private String locationString=null;
    private String temps=null;
    private String weather=null;
    private String comment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle){

            }
            @Override
            public void onProviderEnabled(String S){

            }
            @Override
            public void onProviderDisabled(String s){

            }
        };
        if(Build.VERSION.SDK_INT <23){
            startListening();
        } else{
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location!=null){
                    locationString = updateLocationInfo(location);
                }
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        viewID = sharedPreferences.getInt("viewIDKey", -1); // extract viewID if exist from ViewActivity, otherwise viewID defaults to -1
        draftID = sharedPreferences.getInt("draftIDKey", -1); // extract draftID if exist, otherwise draftID defaults to -1
        year=sharedPreferences.getInt("yearKey", -1); // extract draftID (always exists), otherwise year defaults to -1
        month=sharedPreferences.getInt("monthKey", -1); // extract draftID (always exists), otherwise month defaults to -1
        day=sharedPreferences.getInt("dayKey", -1); // extract draftID (always exists), otherwise day defaults to -1

        // display draft Entry's data if from "Closet or Camera"
        if(draftID!=-1){
            //display draft entry's data
            displayEntryData(draftID);
        }
        // display database view Entry's data if no draft edits yet, from "ViewActivity"
        else if (viewID!=-1) {
            //display view entry's data
            displayEntryData(viewID);
        }
        /*else the xml displays default values onCreate if from "CalendarMain"
          (no edits so far except the date from sharedpref) */
        else{
            //display the selected date from Calendar Main
            TextView dateEntryTextView =findViewById(R.id.dateEntryTextView);
            dateEntryTextView.setText(month+"/"+day+"/"+year+" Entry");


            ImageView outfitImageView =findViewById(R.id.outfitImageView);
            //TODO change outfit image from passed string

            EditText locationDescEditView =findViewById(R.id.locationDescTextView);
            locationDescEditView.setText("");

            //TODO update the TextView with that day's temp (low|high) form
            String low = 50 + "";
            String high = 70 + "";
            String updatedTemps=low+"\u00B0|"+high+"\u00B0";

            TextView tempTextView =findViewById(R.id.tempTextView);
            tempTextView.setText(updatedTemps);

            EditText weatherDescEditView =findViewById(R.id.weatherDescTextView);
            weatherDescEditView.setText("");

            EditText commentEditText =findViewById(R.id.commentDescTextView);
            commentEditText.setText("");

        }
    }

    public void startListening(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requesCode, @NonNull String [] permissions, @NonNull int [] grantResults){
        super.onRequestPermissionsResult(requesCode,permissions,grantResults);

        if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    public String updateLocationInfo(Location location){
        Log.i("LocationInfo", location.toString());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try{
            String address = "Could not find address";
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

            if(listAddresses !=null && listAddresses.size() >0){
                Log.i("PleaceInfo", listAddresses.get(0).toString());
                address= "Address: \n";
                if(listAddresses.get(0).getThoroughfare()!=null){
                    address += listAddresses.get(0).getThoroughfare()+ "\n";
                }if(listAddresses.get(0).getLocality()!=null) {
                    address += listAddresses.get(0).getLocality() + "\n";
                }
            }
            return address;
        } catch(IOException e){
            e.printStackTrace();
        }
        return "";

    }

    public void displayEntryData(int id){

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        Entry entry=dbHelper.getEntryById(id);

        // update the Layout with the class variable (Entry's data)
        TextView dateEntryTextView =findViewById(R.id.dateEntryTextView);
        dateEntryTextView.setText(entry.getMonth()+"/"+entry.getDay()+"/"+entry.getYear()+" Entry");

        ImageView outfitImageView =findViewById(R.id.outfitImageView);
        //TODO change outfit image from passed string

        EditText locationDescEditView =findViewById(R.id.locationDescTextView);
        locationDescEditView.setText(entry.getLocation());

        TextView tempTextView =findViewById(R.id.tempTextView);
        tempTextView.setText(entry.getTemps());

        EditText weatherDescEditView =findViewById(R.id.weatherDescTextView);
        weatherDescEditView.setText(entry.getWeather());

        EditText commentEditText =findViewById(R.id.commentDescTextView);
        commentEditText.setText(entry.getComment());

    }


    public void onClickLocationImage(View view){
        //TODO update the EditText with the gps current location (City, ST) form
       // String city="Madison";
        //String state="WI";
        //String updatedLocation=city+", "+state;
        String updatedLocation = locationString;

        EditText locationDescEditView =findViewById(R.id.locationDescTextView);
        locationDescEditView.setText(updatedLocation);
    }

    public void onClickWeatherImage(View view){

        //TODO update the TextView with that day's temp (low|high) form
        String low = 51 + "";
        String high = 71 + "";
        String updatedTemps=low+"\u00B0|"+high+"\u00B0";

        TextView tempTextView =findViewById(R.id.tempTextView);
        tempTextView.setText(updatedTemps);
    }

    public void updateClassVariablesFromLayout(){

        //id from sharedpref
        //year from sharedpref
        //month from sharedpref
        //day from sharedpref

        //outfit from database (viewID,draftID, or no ID yet as default)
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        //if draftID exists, take outfit from draft (newest update)
        if(draftID!=-1){
            Entry entry=dbHelper.getEntryById(draftID);
            outfit=entry.getOutfit();
        }
        //draftID doesn't exist, but if viewID does, take outfit from viewID
        else if(viewID!=-1){
            Entry entry=dbHelper.getEntryById(viewID);
            outfit=entry.getOutfit();
        }
        //viewID or draftID doesn't exist, keep default image
        else{
            //TODO outfit=default image string (or it is -1)
        }

        EditText locationDescEditView =findViewById(R.id.locationDescTextView);
        locationString=locationDescEditView.getText().toString();

        TextView tempTextView =findViewById(R.id.tempTextView);
        temps=tempTextView.getText().toString();

        EditText weatherDescEditView =findViewById(R.id.weatherDescTextView);
        weather=weatherDescEditView.getText().toString();

        EditText commentEditText =findViewById(R.id.commentDescTextView);
        comment=commentEditText.getText().toString();

    }

    public void onClickOutfitImage(View view){
        Intent intent = new Intent(this, ChooseClosetActivity.class);

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        //save edit progress by storing/updating a draft entry to the database
        //if a draft entry already exist, update draft entry (updateEntry->same draftID)
        if(draftID!=-1){

            //update class variables before calling updateEntry
            updateClassVariablesFromLayout();

            //update the database with a new entry row with the values the user entered into the EditViews so far
            dbHelper.updateEntry(draftID, year, month, day, outfit, locationString, temps, weather, comment);

        }
        // draft entry doesn't exist, make a new draft entry as a placeholder (NewEntry->new draftID)
        else {

            //update class variables before calling newEntry
            updateClassVariablesFromLayout();

            //create in the database a new entry row with the values the user entered into the EditViews so far
            draftID= dbHelper.newEntry(year, month, day, outfit, locationString, temps, weather, comment);

            //update shared preferences with this new draftID
            SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt("draftIDKey", draftID).apply(); // set a key as the entry's draftID int
        }

        startActivity(intent);
    }


    public void onClickSaveButton(View view){

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        //Determine whether (newEntry-> new database ID) or  (updateEntry ->keep same viewID)

        //if viewID exists, then this is an Update Entry, and the draft row is deleted
        if(viewID!=-1){

            //update class variables before calling updateEntry
            updateClassVariablesFromLayout();

            //update viewID's entry with class values in layout
            dbHelper.updateEntry(viewID, year, month, day, outfit, locationString, temps, weather, comment);

            //delete draft entry from database if exists (was only placeholder when going to ChooseCloset)
            if(draftID!=-1){
                dbHelper.deleteEntry(draftID);
            }

        }
        // viewID doesn't exist, but draftID exists so it is kept as new Entry)
        else if(draftID!=-1){

            //update class variables before calling updateEntry
            updateClassVariablesFromLayout();

            //update draft's entry with class values in layout (this is now a permanent entry)
            dbHelper.updateEntry(draftID, year, month, day, outfit, locationString, temps, weather, comment);
        }
        //neither viewID or draftID exists
        else{
            //update class variables before calling newEntry
            updateClassVariablesFromLayout();

            //create new row in database of values in layout
            int newID= dbHelper.newEntry(year, month, day, outfit, locationString, temps, weather, comment);

            //newID is never used
        }

        goToCalendarMainActivity();

    }


    public void onClickExitButton(View view){

        //do not update/save draft entry to database, instead delete draftEntry (if exists) from database
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        if(draftID!=-1){
            dbHelper.deleteEntry(draftID);
        }

        goToCalendarMainActivity();

    }

    public void goToCalendarMainActivity() {

        Intent intent = new Intent(this, CalendarMainActivity.class);

        // delete sharedpreferences to set back to default when going back to CalendarMain
        SharedPreferences sharedPreferences = getSharedPreferences("<com.cs407.closetcalendar>", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("viewIDKey").apply();
        sharedPreferences.edit().remove("draftIDKey").apply();
        sharedPreferences.edit().remove("yearKey").apply();
        sharedPreferences.edit().remove("monthKey").apply();
        sharedPreferences.edit().remove("dayKey").apply();

        startActivity(intent);
    }
}