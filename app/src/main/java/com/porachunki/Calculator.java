package com.porachunki;


import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class Calculator{
    Context context;
    public Calculator(Context context){
        this.context = context;
    }



    /*
    transactionBalance oblicza bilans tranzakcji. Bilans niepłacącego to wartość jego "długu",
    bilans płacącego jest równy 0. Zwraca wynik w postaci dwuelementowej tabeli
    */
    public float[] transactionBalance(float bill, float person2Part, float person1Part, String whoPays){
        String person1 = context.getString(R.string.person_1_name);
        float[] array = new float[2];
        float person2TransactionBalance = 0;
        float person1TransactionBalance = 0;
        if(whoPays.matches(person1)){
            person2TransactionBalance = (bill+person2Part-person1Part)/2;
        }else{
            person1TransactionBalance = (bill+person1Part-person2Part)/2;
        }
        array[0] = person1TransactionBalance;
        array[1] = person2TransactionBalance;
        return array;

    }// transactionBalance() END
}
