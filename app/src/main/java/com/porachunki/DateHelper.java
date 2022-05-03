package com.porachunki;

import android.text.format.DateFormat;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper{
//    Calendar c = Calendar.getInstance();
    Calendar c;

    public DateHelper() {
        c = Calendar.getInstance();
//            Date today = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//            String currentDateandTime = sdf.format(today);
    }

    public String dateToString(int day, int month, int year){
        StringBuilder sb = new StringBuilder();
        sb.append(day+"."+month+"."+year);
        return sb.toString();
    }

    public String dateToString(Date date){
        String day = (String)DateFormat.format("dd", date);
        String month = (String)DateFormat.format("MM", date);
        String year = (String)DateFormat.format("yyyy", date);
        StringBuilder sb = new StringBuilder();
        sb.append(day+"."+month+"."+year);
        return sb.toString();
    }

    public String dateToCharString(Date date){
        String day = (String)DateFormat.format("dd", date);
        String month = (String)DateFormat.format("MMMM", date);
        StringBuilder sb = new StringBuilder();
        sb.append(day+" "+month);
        return sb.toString();
    }

    public Date IntToDate (int day, int month, int year){
        Date date = null;
        String dayString = String.valueOf(day);
        String monthString = String.valueOf(month);
        String yearString = String.valueOf(year);
        String dateString = dayString+"."+monthString+"."+yearString;
        SimpleDateFormat sdf=new SimpleDateFormat("dd.MM.yyyy");
        try{
            date =  sdf.parse(dateString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

    public int getCurrentDay(){
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public int getCurrentMouth(){
        return c.get(Calendar.MONTH);
    }

    public int getCurrentYear(){
        return c.get(Calendar.YEAR);
    }

    public String getCurrentDateString(){
        return dateToString(getCurrentDay(),getCurrentMouth()+1,getCurrentYear());
    }
}