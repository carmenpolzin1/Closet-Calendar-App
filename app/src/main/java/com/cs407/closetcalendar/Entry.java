package com.cs407.closetcalendar;

public class Entry {
    private int id;
    private int year;
    private int month;
    private int day;
    private String outfit;
    private String location;
    private String temps;
    private String weather;
    private String comment;

    public Entry(int id, int year, int month, int day, String outfit, String location, String temps, String weather, String comment){
        this.id=id;
        this.year=year;
        this.month=month;
        this.day=day;
        this.outfit=outfit;
        this.location=location;
        this.temps=temps;
        this.weather=weather;
        this.comment=comment;
    }

    //class constructor (default values null or -1)
    public Entry(){
        this.id=-1;
        this.year=-1;
        this.month=-1;
        this.day=-1;
        this.outfit=null;
        this.location=null;
        this.temps=null;
        this.weather=null;
        this.comment=null;
    }

    public int getID() { return id; }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
    public String getOutfit() { return outfit; }
    public String getLocation() { return location; }
    public String getTemps() { return temps; }
    public String getWeather() { return weather; }
    public String getComment() { return comment; }


    public void setId(int id) {
        this.id=id;
    }

    public void setYear(int year) {
        this.year=year;
    }

    public void setMonth(int month) {
        this.month=month;
    }

    public void setDay(int day) {
        this.day=day;
    }

    public void setOutfit(String outfit) {
        this.outfit=outfit;
    }

    public void setLocation(String location) {
        this.location=location;
    }

    public void setTemps(String temps) {
        this.temps=temps;
    }

    public void setWeather(String weather) {
        this.weather=weather;
    }

    public void setComment(String comment) {
        this.comment=comment;
    }
}
