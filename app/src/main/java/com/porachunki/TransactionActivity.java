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
    TextView tvPerson1;
    TextView tvPerson2;
    TextView tvDate;
    TextView tvDescription;
    RadioButton rbPerson1Pays;
    RadioButton rbPerson2Pays;

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
        tvPerson1 = findViewById(R.id.et_transaction_person_1);
        tvPerson2 = findViewById(R.id.et_transaction_person_2);
        tvDate = findViewById(R.id.tv_transaction_date);
        Button btnOK = findViewById(R.id.bt_ok_transaction);
        Button btnCancel = findViewById(R.id.bt_cancel_transaction);
        rbPerson1Pays = findViewById(R.id.rb_person_1_pays);
        rbPerson2Pays = findViewById(R.id.rb_person_2_pays);
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

        rbPerson1Pays.setChecked(true);
        rbPerson1Pays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbPerson1Pays.setChecked(true);
                rbPerson2Pays.setChecked(false);
            }
        });
        rbPerson2Pays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbPerson1Pays.setChecked(false);
                rbPerson2Pays.setChecked(true);
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
        Calculator calculator = new Calculator(getApplicationContext());

        rd.setJustAddedFlag(true);
        rd.setDate(date);

        float total = Float.valueOf(tvTotal.getText().toString());
        rd.setBill(total);

        String rPartString = tvPerson2.getText().toString();
        float person2Part;
        if(rPartString.matches("")){
            person2Part = 0;
        }else {
            person2Part = Float.valueOf(rPartString);
        }
        rd.setPerson2Part(person2Part);

        String pPartString = tvPerson1.getText().toString();
        float person1Part;
        if(pPartString.matches("")){
            person1Part = 0;
        }else {
            person1Part = Float.valueOf(pPartString);
        }
        rd.setPerson1Part(person1Part);

        String whoPays;
        String person1 = getString(R.string.person_1_name);
        String person2 = getString(R.string.person_2_name);
        if(rbPerson1Pays.isChecked()){
            whoPays = person1;
        }else{
            whoPays = person2;
        }
        rd.setWhoPays(whoPays);

        rd.setDescription(tvDescription.getText().toString());

        rd.setPerson1TransationBalance(calculator.bilans(total, person2Part, person1Part, whoPays)[0]);
        rd.setPerson2TransationBalance(calculator.bilans(total, person2Part, person1Part, whoPays)[1]);

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
            float bilansP = dataList.get(i).getPerson1TransationBalance();
            float bilansR = dataList.get(i).getPerson2TransationBalance();

            if(i< dataList.size()-1){
                saldo = dataList.get(i+1).getBalance() + bilansP-bilansR;
            }else{
                saldo =initialBallance + bilansP-bilansR;
            }
            dataList.get(i).setBalance(saldo);
        }
        StartActivity.totalBalance = saldo;
    }

    private float readInitialBallance(){
        final String KEY_INITIAL_BALLANCE = "initial_ballance";
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        return sh.getFloat(KEY_INITIAL_BALLANCE, 0);
    }

    private void makeJsonFile(){

        final String KEY_DATE = "date";
        final String KEY_BILL = "bill";
        final String KEY_PERSON1_PART = "person1_part";
        final String KEY_PERSON2_PART = "person2_part";
        final String KEY_WHO_PAYS = "who_pays";
        final String KEY_DESCRIPTION = "description";
        final String KEY_PERSON1_TRANSACTION_BALANCE = "person1_transaction_balance";
        final String KEY_PERSON2_TRANSACTION_BALANCE = "person2_transaction_balance";
        final String KEY_BALANCE = "balance";
        final String KEY_JSON_ARRAY = "json_array";


        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {

            for (int i = 0; i< dataList.size(); i++){
                JSONObject jsonRow = new JSONObject();
                jsonRow.put(KEY_DATE, new DateHelper().dateToString(dataList.get(i).getDate()));
                jsonRow.put(KEY_BILL, dataList.get(i).getBill());
                jsonRow.put(KEY_PERSON1_PART, dataList.get(i).getPerson1Part());
                jsonRow.put(KEY_PERSON2_PART, dataList.get(i).getPerson2Part());
                jsonRow.put(KEY_WHO_PAYS, dataList.get(i).getWhoPays());
                jsonRow.put(KEY_DESCRIPTION, dataList.get(i).getDescription());
                jsonRow.put(KEY_PERSON1_TRANSACTION_BALANCE, dataList.get(i).getPerson1TransationBalance());
                jsonRow.put(KEY_PERSON2_TRANSACTION_BALANCE, dataList.get(i).getPerson2TransationBalance());
                jsonRow.put(KEY_BALANCE, dataList.get(i).getBalance());
                jsonArray.put(jsonRow);
            }
            jsonObject.put(KEY_JSON_ARRAY, jsonArray);


        }catch (org.json.JSONException e){
            e.printStackTrace();
        }

        JsonFileUtility jfile = new JsonFileUtility(getApplicationContext());
        jfile.saveJson(jsonObject);
    }
}
