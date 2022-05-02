package com.porachunki;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TransactionActivity extends AppCompatActivity {

    ArrayList<RowData> dataList = StartActivity.dataList;

    TextView tvTotal;
    TextView tvPaulina;
    TextView tvRobert;
    TextView tvDate;
    TextView tvDescription;
    RadioButton rbPaulinaPays;
    RadioButton rbRobertPays;

    DateHelper dateHelper;
    private int TVday;
    private int TVmonth;
    private int TVyear;

    private int addedRowIndex;

    // Starter Pattern
    public static void start(Context context) {
        Intent starter = new Intent(context, TransactionActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        dateHelper = new DateHelper();
        TVday = dateHelper.getCurrentDay();
        TVmonth = dateHelper.getCurrentMouth()+1;
        TVyear = dateHelper.getCurrentYear();
        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Hide status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();
    }


    // **************** hide keyboard after click outside ***************************
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    //*******************************************************************************


    private void initControls(){
        tvTotal = findViewById(R.id.et_transaction_total);
        tvPaulina = findViewById(R.id.et_transaction_paulina);
        tvRobert = findViewById(R.id.et_transaction_robert);
        tvDate = findViewById(R.id.tv_transaction_date);
        Button btnOK = findViewById(R.id.bt_ok_transaction);
        Button btnCancel = findViewById(R.id.bt_cancel_transaction);
        rbPaulinaPays = findViewById(R.id.rb_paulina_pays);
        rbRobertPays = findViewById(R.id.rb_robert_pays);
        tvDescription = findViewById(R.id.et_transaction_description);
        tvDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        tvDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);

        tvDate.setText(dateHelper.getCurrentDateString());

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String date = dateHelper.dateToString(day,month+1,year);
                                tvDate.setText(date);
                                TVday = day;
                                TVmonth = month+1;
                                TVyear = year;

                            }
                        }, TVyear, TVmonth-1, TVday);

                datePickerDialog.show();
            }
        });

        rbPaulinaPays.setChecked(true);
        rbPaulinaPays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbPaulinaPays.setChecked(true);
                rbRobertPays.setChecked(false);
            }
        });
        rbRobertPays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbPaulinaPays.setChecked(false);
                rbRobertPays.setChecked(true);
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvTotal.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(),"Wypełnij pole Łącznie:", Toast.LENGTH_SHORT).show();
                }else{
                    updateDataList();
                    sortDatalist(dataList);
                    salda(readInitialBallance());
                    makeJsonFile();
                    addedRowIndex = findAddedRowIndex();
                    HistoryActivity.start(getApplicationContext(), addedRowIndex);
                    finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void updateDataList(){
        RowData rd = new RowData();
        Date date = new Date();
        date = new DateHelper().IntToDate(TVday, TVmonth, TVyear);
        Calculator calculator = new Calculator();

        rd.setJustAddedFlag(true);
        rd.setDate(date);

        float total = Float.valueOf(tvTotal.getText().toString());
        rd.setTotal(total);

        String rPartString = tvRobert.getText().toString();
        float robertPart;
        if(rPartString.matches("")){
            robertPart = 0;
        }else {
            robertPart = Float.valueOf(rPartString);
        }
        rd.setRobertPart(robertPart);

        String pPartString = tvPaulina.getText().toString();
        float paulinaPart;
        if(pPartString.matches("")){
            paulinaPart = 0;
        }else {
            paulinaPart = Float.valueOf(pPartString);
        }
        rd.setPaulinaPart(paulinaPart);

        String payment;
        if(rbPaulinaPays.isChecked()){
            payment = "Paulina";
        }else{
            payment = "Robert";
        }
        rd.setPayment(payment);

        rd.setDescription(tvDescription.getText().toString());

        rd.setBilansP(calculator.bilans(total, robertPart, paulinaPart, payment)[0]);
        rd.setBilansR(calculator.bilans(total, robertPart, paulinaPart, payment)[1]);

        dataList.add(0,rd);
    }

    private void sortDatalist(ArrayList list){
        Collections.sort(list, new Comparator<RowData>() {
            @Override
            public int compare(RowData rm1, RowData rm2) {
                return rm2.getDate().compareTo(rm1.getDate());
            }
        });
    }

    private int findAddedRowIndex(){
        for (int i = 0; i< dataList.size(); i++){
            if(dataList.get(i).isJustAddedFlag()){
                dataList.get(i).setJustAddedFlag(false);
                return i;
            }
        }
        return -1;
    }

    /* Oblicza saldo częściowe dla każdego rekordu
     // licząc salda początkowego (Wcześniej konieczne sortowanie tabeli)
     // Ostatnie (i=0) saldo częściowe jest saldem całkowitym.
     */
    private void salda(float initialBallance){
        float saldo = initialBallance;
        RowData rd = new RowData();
        for(int i = dataList.size()-1; i>=0; i--){
            float bilansP = dataList.get(i).getBilansP();
            float bilansR = dataList.get(i).getBilansR();

            if(i< dataList.size()-1){
                saldo = dataList.get(i+1).getSaldo() + bilansP-bilansR;
            }else{
                saldo =initialBallance + bilansP-bilansR;
            }
            dataList.get(i).setSaldo(saldo);
        }
        StartActivity.totalBallance = saldo;
    }

    private float readInitialBallance(){
        final String KEY_INITIAL_BALLANCE = "initial_ballance";
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        return sh.getFloat(KEY_INITIAL_BALLANCE, 0);
    }

    private void makeJsonFile(){

        final String KEY_DATE = "date";
        final String KEY_TOTAL = "total";
        final String KEY_PPART = "paulina_part";
        final String KEY_RPART = "robert_part";
        final String KEY_PAYMENT = "payment";
        final String KEY_DESCRIPTION = "description";
        final String KEY_PBILANS = "paulina_bilans";
        final String KEY_RBILANS = "robert_bilans";
        final String KEY_SALDO = "saldo";
        final String KEY_ARRAY = "all data";


        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            for (int i = 0; i< dataList.size(); i++){
                JSONObject jsonRow = new JSONObject();
                jsonRow.put(KEY_DATE, new DateHelper().dateToString(dataList.get(i).getDate()));
                jsonRow.put(KEY_TOTAL, dataList.get(i).getTotal());
                jsonRow.put(KEY_PPART, dataList.get(i).getPaulinaPart());
                jsonRow.put(KEY_RPART, dataList.get(i).getRobertPart());
                jsonRow.put(KEY_PAYMENT, dataList.get(i).getPayment());
                jsonRow.put(KEY_DESCRIPTION, dataList.get(i).getDescription());
                jsonRow.put(KEY_PBILANS, dataList.get(i).getBilansP());
                jsonRow.put(KEY_RBILANS, dataList.get(i).getBilansR());
                jsonRow.put(KEY_SALDO, dataList.get(i).getSaldo());
                jsonArray.put(jsonRow);
            }
            jsonObject.put(KEY_ARRAY, jsonArray);


        }catch (org.json.JSONException e){
            e.printStackTrace();
        }

        JsonFileUtility jfile = new JsonFileUtility(getApplicationContext());
        jfile.saveJson(jsonObject);
    }
}
