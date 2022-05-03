package com.porachunki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ListView lv;
    private CustomAdapter customAdapter;
    //public static boolean forceSelection = true;
    public static int forceCheckedPosition = 0;

    public float finalSaldo;

    TextView tvDate;
    TextView tvTotal;
    TextView tvPerson2Part;
    TextView tvPerson1Part;
    TextView tvPayment;
    TextView tvBilans;
    TextView tvDescription;
    TextView tvSaldo;

    private ArrayList<RowData> dataList = StartActivity.dataList;

    // Starter Pattern
    public static void start(Context context, int justAddedPosition) {
        Intent starter = new Intent(context, HistoryActivity.class);
        starter.putExtra("justAddedPosition", justAddedPosition);
        context.startActivity(starter);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent();
        forceCheckedPosition = intent.getIntExtra("justAddedPosition", -1);

        setViews();
        if (!dataList.isEmpty()) fillDetails(forceCheckedPosition);

    }

    private void setViews(){
        tvDate = findViewById(R.id.tv_details_date);
        tvTotal = findViewById(R.id.tv_details_total);
        tvPerson2Part = findViewById(R.id.tv_details_person_2_part);
        tvPerson1Part = findViewById(R.id.tv_details_person_1_part);
        tvPayment = findViewById(R.id.tv_details_payment);
        tvBilans = findViewById(R.id.tv_details_bilans);
        tvDescription = findViewById(R.id.tv_details_description);
        tvSaldo = findViewById(R.id.tv_details_saldo);

        lv = (ListView) findViewById(R.id.listView);

        customAdapter = new CustomAdapter(this, dataList);
        if (!dataList.isEmpty()) lv.setAdapter(customAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                fillDetails(position);
                forceCheckedPosition = position;
                customAdapter.notifyDataSetChanged();

                // Get the selected item text from ListView
//                RowData im = (RowData)parent.getItemAtPosition(position);
//                String selectedName = im.getDate();
//
//                Toast.makeText(getApplication(), selectedName,
//                        Toast.LENGTH_SHORT).show();
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final DeleteDialog deleteDialog = new DeleteDialog(HistoryActivity.this, i);

                deleteDialog.setOnDeleteClickListener(new OnDeleteClickListener() {
                    @Override
                    public void onDeleteClick(int posiition) {
                        // TODO: jeśli kasowany jest ostatni (najstarszy) rekord, zapisujemy jego saldo jako saldo początkowe??
                        int lastPosition = dataList.size() - 1;
                        if(posiition==lastPosition){
//                            writeInitialBallance(dataList.get(posiition).getSaldo());
                        }
                        dataList.remove(posiition);
                        balance(readInitialBallance());
                        makeJsonFile();
                        forceCheckedPosition = 0;
                        if (!dataList.isEmpty()){
                            fillDetails(forceCheckedPosition);
                        }else{
                            clearDetails();
                        }


                        customAdapter.notifyDataSetChanged();
                    }
                });
                return false;
            }
        });
    }

    private void fillDetails (int position){
        RowData rd = dataList.get(position);

        String pln = " zł";
        String date = new DateHelper().dateToString(rd.getDate());
        float bill = rd.getBill();
        float person2Part = rd.getPerson2Part();
        float person1Part = rd.getPerson1Part();
        String whoPays = rd.getWhoPays();
        float person1TransationBalance = rd.getPerson1TransationBalance();
        float person2TransationBalance = rd.getPerson2TransationBalance();
        String description = rd.getDescription();

        String person1 = getString(R.string.person_1_name);
        String person2 = getString(R.string.person_2_name);
        //int saldo ;

        tvDate.setText(date);
        tvTotal.setText(String.format("%.02f",bill)+pln);
        if(person2Part==0){
            tvPerson2Part.setText("-");
        }else{
            tvPerson2Part.setText(String.format("%.02f",person2Part)+pln);
        }
        if(person1Part==0){
            tvPerson1Part.setText("-");
        }else{
            tvPerson1Part.setText(String.format("%.02f",person1Part)+pln);
        }
        tvPayment.setText(whoPays);

        String bilans = null;

        if(person1TransationBalance==0){
            bilans = person2+":   - "+String.format("%.02f",person2TransationBalance)+pln;
        }

        if(person2TransationBalance==0){
            bilans = person1+":   - "+String.format("%.02f",person1TransationBalance)+pln;
        }


        tvBilans.setText(bilans);
        tvDescription.setText(description);

        float saldo = dataList.get(position).getBalance();
        String saldoString;
        if(saldo>0){
            saldoString = "Saldo:    "+person1+"  -"+String.format("%.02f",saldo)+pln;//String.valueOf(saldo)+pln;
        }else if(saldo<0){
            saldoString = "Saldo:    "+person2+"  -"+String.format("%.02f",-saldo)+pln;
        }else{
            saldoString = "Zobowiązania uregulowane";
        }
        tvSaldo.setText(saldoString);
    }

    private void clearDetails(){
        String blank = "-";
        tvDate.setText(blank);
        tvTotal.setText(blank);
        tvPerson2Part.setText(blank);
        tvPerson1Part.setText(blank);
        tvPayment.setText(blank);
        tvBilans.setText(blank);
        tvDescription.setText(blank);
        tvSaldo.setText(blank);
    }

    /* Oblicza saldo częściowe dla każdego rekordu
     // licząc salda początkowego (Wcześniej konieczne sortowanie tabeli)
     // Ostatnie (i=0) saldo częściowe jest saldem całkowitym.
     */
    private void balance(float initialBallance){
        float ballance = initialBallance;
        RowData rd = new RowData();
        for(int i = dataList.size()-1; i>=0; i--){
            float person1TransationBalance = dataList.get(i).getPerson1TransationBalance();
            float person2TransationBalance = dataList.get(i).getPerson2TransationBalance();

            if(i<dataList.size()-1){
                ballance = dataList.get(i+1).getBalance() + person1TransationBalance-person2TransationBalance;
            }else{
                ballance =initialBallance + person1TransationBalance-person2TransationBalance;
            }
            dataList.get(i).setBalance(ballance);
        }
        StartActivity.totalBalance = ballance;
    }

    private float readInitialBallance(){
        final String KEY_INITIAL_BALLANCE = "initial_ballance";
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        return sh.getFloat(KEY_INITIAL_BALLANCE, 0);
    }

    private void writeInitialBallance(float initialBallance){
        final String KEY_INITIAL_BALLANCE = "initial_ballance";
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(KEY_INITIAL_BALLANCE, initialBallance);
        editor.apply();
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

//        boolean saveToTempFolder = filename.equals(TEMP_JSON_FILENAME);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        ArrayList<RowData> list = dataList;
        try {

            for (int i=0; i< list.size(); i++){
                JSONObject jsonRow = new JSONObject();
                jsonRow.put(KEY_DATE, new DateHelper().dateToString(list.get(i).getDate()));
                jsonRow.put(KEY_BILL, list.get(i).getBill());
                jsonRow.put(KEY_PERSON1_PART, list.get(i).getPerson1Part());
                jsonRow.put(KEY_PERSON2_PART, list.get(i).getPerson2Part());
                jsonRow.put(KEY_WHO_PAYS, list.get(i).getWhoPays());
                jsonRow.put(KEY_DESCRIPTION, list.get(i).getDescription());
                jsonRow.put(KEY_PERSON1_TRANSACTION_BALANCE, list.get(i).getPerson1TransationBalance());
                jsonRow.put(KEY_PERSON2_TRANSACTION_BALANCE, list.get(i).getPerson2TransationBalance());
                jsonRow.put(KEY_BALANCE, list.get(i).getBalance());
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
