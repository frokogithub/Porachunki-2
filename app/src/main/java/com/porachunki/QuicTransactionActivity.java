package com.porachunki;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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

public class QuicTransactionActivity extends AppCompatActivity {


    TextView tvDate;
    TextView tvKwota;
    RadioButton rbPerson1ToPerson2;
    RadioButton rbPerson2ToPerson1;
    TextView tvDescription;
    DateHelper dateHelper;
    private int TVday;
    private int TVmonth;
    private int TVyear;

    private int addedRowIndex;


    // Starter Pattern
    public static void start(Context context) {
        Intent starter = new Intent(context, QuicTransactionActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quic_transaction);

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
        tvDate = findViewById(R.id.tv_quick_transaction_date);
        tvKwota = findViewById(R.id.et_quick_transaction_kwota);
        Button btnOK = findViewById(R.id.bt_ok_quick_transaction);
        Button btnCancel = findViewById(R.id.bt_cancel_quick_transaction);
        rbPerson1ToPerson2 = findViewById(R.id.rb_person_1_to_person_2);
        rbPerson2ToPerson1 = findViewById(R.id.rb_person_2_to_person_1);
        tvDescription = findViewById(R.id.et_quick_transaction_description);
        tvDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        tvDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);


        tvDate.setText(dateHelper.getCurrentDateString());

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(QuicTransactionActivity.this,
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


        rbPerson1ToPerson2.setChecked(true);
        rbPerson1ToPerson2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbPerson1ToPerson2.setChecked(true);
                rbPerson2ToPerson1.setChecked(false);
            }
        });
        rbPerson2ToPerson1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbPerson1ToPerson2.setChecked(false);
                rbPerson2ToPerson1.setChecked(true);
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvKwota.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(),"Wypełnij pole Kwota", Toast.LENGTH_SHORT).show();
                }else{
                    // Wpisuje nową paczkę danych to tabeli
                    // Segreguje po dacie
                    // oblicza częściowe salda (konieczne po kazdym sortowaniu)
                    // Tworzy plik JSON
                    // Informuje następną aktywność o pozycji stworzonego wpisu (potrzebne do zaznaczenia go i wyświetlenia szczegółów transakcji)
                    updateDataList();
                    sortDatalist(StartActivity.dataList);
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

    /* Dane z ekranu wpisuje do paczki danych wiersza i dodaje ją do tablicy */
    private void updateDataList(){
        RowData rd = new RowData();
        Date date = new Date();
        date = new DateHelper().IntToDate(TVday, TVmonth, TVyear);
        Calculator calculator = new Calculator();

        // flaguje wpis żeby odnależć go po sortowaniu
        rd.setJustAddedFlag(true);
        rd.setDate(date);

        float total = Float.valueOf(tvKwota.getText().toString());
        rd.setTotal(total);

        float person2Part = 0;
        float person1Part = 0;

        String payment;
        if(rbPerson1ToPerson2.isChecked()){
            payment = "Paulina";
            person2Part = total;
        }else{
            payment = "Robert";
            person1Part = total;
        }
        rd.setPayment(payment);
        rd.setPerson2Part(person2Part);
        rd.setPerson1Part(person1Part);

        rd.setDescription(tvDescription.getText().toString());

        rd.setBilansP(calculator.bilans(total, person2Part, person1Part, payment)[0]);
        rd.setBilansR(calculator.bilans(total, person2Part, person1Part, payment)[1]);

        // wymusza zapis danych na pierwszej pozycji
        StartActivity.dataList.add(0,rd);
    }

    /* Sortuje tabelę od najmłodszego do najstarszego rekordu*/
    private void sortDatalist(ArrayList list){
        Collections.sort(list, new Comparator<RowData>() {
            @Override
            public int compare(RowData rm1, RowData rm2) {
                return rm2.getDate().compareTo(rm1.getDate());
            }
        });
    }

    /* Znajduje pozycję oflagowanego wcześniej rekordu */
    private int findAddedRowIndex(){
        for (int i=0;i<StartActivity.dataList.size(); i++){
            if(StartActivity.dataList.get(i).isJustAddedFlag()){
                StartActivity.dataList.get(i).setJustAddedFlag(false);
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
        Log.d("kroko initialBallance", String.valueOf(initialBallance));
        float saldo = initialBallance;
        RowData rd = new RowData();
        for(int i = StartActivity.dataList.size()-1; i>=0; i--){
            float bilansP = StartActivity.dataList.get(i).getBilansP();
            float bilansR = StartActivity.dataList.get(i).getBilansR();

            if(i<StartActivity.dataList.size()-1){
                saldo = StartActivity.dataList.get(i+1).getSaldo() + bilansP-bilansR;
            }else{
                saldo =initialBallance + bilansP-bilansR;
            }
            StartActivity.dataList.get(i).setSaldo(saldo);
        }
        StartActivity.totalBallance = saldo;
    }

    private float readInitialBallance(){
        final String KEY_INITIAL_BALLANCE = "initial_ballance";
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        return sh.getFloat(KEY_INITIAL_BALLANCE, 0);
    }

    // Z danych tabeli tworzy plik JSON i przekazuje do zapisania
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

//        boolean saveToTempFolder = filename.equals(TEMP_JSON_FILENAME);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        ArrayList<RowData> list = StartActivity.dataList;
        try {

            for (int i=0; i< list.size(); i++){
                JSONObject jsonRow = new JSONObject();
                jsonRow.put(KEY_DATE, new DateHelper().dateToString(list.get(i).getDate()));
                jsonRow.put(KEY_TOTAL, list.get(i).getTotal());
                jsonRow.put(KEY_PPART, list.get(i).getPerson1Part());
                jsonRow.put(KEY_RPART, list.get(i).getPerson2Part());
                jsonRow.put(KEY_PAYMENT, list.get(i).getPayment());
                jsonRow.put(KEY_DESCRIPTION, list.get(i).getDescription());
                jsonRow.put(KEY_PBILANS, list.get(i).getBilansP());
                jsonRow.put(KEY_RBILANS, list.get(i).getBilansR());
                jsonRow.put(KEY_SALDO, list.get(i).getSaldo());
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
