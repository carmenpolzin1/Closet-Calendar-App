package com.cs407.closetcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "entries.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the "entries" table
        db.execSQL("CREATE TABLE IF NOT EXISTS entries (" +
                "id INTEGER PRIMARY KEY, " +
                "year INTEGER, " +
                "month INTEGER, " +
                "day INTEGER, " +
                "outfit TEXT, " +
                "location TEXT, " +
                "temps TEXT, " +
                "weather TEXT, " +
                "comment TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    public Entry getEntryByDate(int year, int month, int day) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Specify the columns you want to retrieve
        String[] columns = {"id", "year", "month", "day", "outfit", "location", "temps", "weather", "comment"};

        // Specify the selection criteria
        String selection = "year = ? AND month = ? AND day = ?";

        // Arguments for the selection criteria
        String[] selectionArgs = {String.valueOf(year), String.valueOf(month), String.valueOf(day)};

        // Query the database
        Cursor cursor = db.query("entries", columns, selection, selectionArgs, null, null, null);

        Entry entry = null;

        // If a row is found, create an Entry object
        if (cursor != null && cursor.moveToFirst()) {
            entry = new Entry();
            entry.setId(cursor.getInt(cursor.getColumnIndex("id")));
            entry.setYear(cursor.getInt(cursor.getColumnIndex("year")));
            entry.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
            entry.setDay(cursor.getInt(cursor.getColumnIndex("day")));
            entry.setOutfit(cursor.getString(cursor.getColumnIndex("outfit")));
            entry.setLocation(cursor.getString(cursor.getColumnIndex("location")));
            entry.setTemps(cursor.getString(cursor.getColumnIndex("temps")));
            entry.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
            entry.setComment(cursor.getString(cursor.getColumnIndex("comment")));
        }

        // Close the cursor and database
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return entry;
    }

    public Entry getEntryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Specify the columns you want to retrieve
        String[] columns = {"id", "year", "month", "day", "outfit", "location", "temps", "weather", "comment"};

        // Specify the selection criteria
        String selection = "id = ?";

        // Arguments for the selection criteria
        String[] selectionArgs = {String.valueOf(id)};

        // Query the database
        Cursor cursor = db.query("entries", columns, selection, selectionArgs, null, null, null);

        Entry entry = null;

        // If a row is found, create an Entry object
        if (cursor != null && cursor.moveToFirst()) {
            entry = new Entry();
            entry.setId(cursor.getInt(cursor.getColumnIndex("id")));
            entry.setYear(cursor.getInt(cursor.getColumnIndex("year")));
            entry.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
            entry.setDay(cursor.getInt(cursor.getColumnIndex("day")));
            entry.setOutfit(cursor.getString(cursor.getColumnIndex("outfit")));
            entry.setLocation(cursor.getString(cursor.getColumnIndex("location")));
            entry.setTemps(cursor.getString(cursor.getColumnIndex("temps")));
            entry.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
            entry.setComment(cursor.getString(cursor.getColumnIndex("comment")));
        }

        // Close the cursor and database
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return entry;
    }

    public int newEntry(int year, int month, int day, String outfit, String location, String temps, String weather, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("outfit", outfit);
        values.put("location", location);
        values.put("temps", temps);
        values.put("weather", weather);
        values.put("comment", comment);

        // Insert the new row
        int newRowId = (int) db.insert("entries", null, values);

        // Close the database
        db.close();

        return newRowId;
    }

    public void updateEntry(int id, int year, int month, int day, String outfit, String location, String temps, String weather, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("outfit", outfit);
        values.put("location", location);
        values.put("temps", temps);
        values.put("weather", weather);
        values.put("comment", comment);

        // Specify the selection criteria
        String selection = "id = ?";

        // Arguments for the selection criteria
        String[] selectionArgs = {String.valueOf(id)};

        // Update the existing row
        int rowsAffected = db.update("entries", values, selection, selectionArgs);

        // Close the database
        db.close();

    }

    public void deleteEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Specify the selection criteria
        String selection = "id = ?";

        // Arguments for the selection criteria
        String[] selectionArgs = {String.valueOf(id)};

        // Delete the existing row
        int rowsDeleted = db.delete("entries", selection, selectionArgs);

        // Close the database
        db.close();
    }

    public void updateOutfit(int draftID, String outfit) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("outfit", outfit);

        // Specify the selection criteria
        String selection = "id = ?";

        // Arguments for the selection criteria
        String[] selectionArgs = {String.valueOf(draftID)};

        // Update the existing row
        int rowsAffected = db.update("entries", values, selection, selectionArgs);

        // Close the database
        db.close();
    }

    public Cursor getOutfitCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"outfit"};

        // retrieving all rows in the "outfit" column
        return db.query("entries", columns, null, null, null, null, null);
    }

}

