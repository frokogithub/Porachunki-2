package com.porachunki;

public class Calculator {


    public float[] bilans(float total, float robertPart, float paulinaPart, String payment){
        float[] array = new float[2];
        float xR = 0;
        float xP = 0;
        if(payment.matches("Paulina")){
            xR = (total+robertPart-paulinaPart)/2;
        }else{
            xP = (total+paulinaPart-robertPart)/2;
        }
        array[0] = xP;
        array[1] = xR;
        return array;
    }

}
