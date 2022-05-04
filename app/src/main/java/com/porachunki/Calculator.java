package com.porachunki;


import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;

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


    /* Oblicza saldo częściowe dla każdego rekordu
     // licząc salda początkowego (Wcześniej konieczne sortowanie tabeli)
     // Ostatnie (i=0) saldo częściowe jest saldem całkowitym.
     */
    void balance(){
        ArrayList<RowData> dataList = StartActivity.dataList;
        final String KEY_INITIAL_BALLANCE = "initial_ballance";
        SharedPreferences sh = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        float initialBallance = sh.getFloat(KEY_INITIAL_BALLANCE, 0);

        float balance = initialBallance;
        RowData rd = new RowData();
        for(int i = dataList.size()-1; i>=0; i--){
            float person1TransationBalance = dataList.get(i).getPerson1TransationBalance();
            float person2TransationBalance = dataList.get(i).getPerson2TransationBalance();

            if(i< dataList.size()-1){
                balance = dataList.get(i+1).getBalance() + person1TransationBalance-person2TransationBalance;
            }else{
                balance = initialBallance + person1TransationBalance-person2TransationBalance;
            }
            dataList.get(i).setBalance(balance);
        }
        StartActivity.totalBalance = balance;
    }


}
