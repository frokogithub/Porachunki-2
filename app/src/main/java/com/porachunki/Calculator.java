package com.porachunki;


import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class Calculator{
    Context context;
    public Calculator(Context context){
        this.context = context;
    }



    public float[] bilans(float total, float person2Part, float person1Part, String whoPays){
        String person1 = context.getString(R.string.person_1_name);
        float[] array = new float[2];
        float xR = 0;
        float xP = 0;
        if(whoPays.matches(person1)){
            xR = (total+person2Part-person1Part)/2;
        }else{
            xP = (total+person1Part-person2Part)/2;
        }
        array[0] = xP;
        array[1] = xR;
        return array;

    }
}
//  String mystring = getResources().getString(R.string.mystring);