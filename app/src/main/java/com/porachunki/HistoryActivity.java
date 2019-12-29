package com.porachunki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
    public static boolean forceSelection = true;
    public static int forceCheckedPosition = 0;

    public float finalSaldo;

    TextView tvDate;
    TextView tvTotal;
    TextView tvRobertPart;
    TextView tvPaulinaPart;
    TextView tvPayment;
    TextView tvBilans;
    TextView tvDescription;
    TextView tvSaldo;



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
        if (!StartActivity.dataList.isEmpty()) fillDetails(forceCheckedPosition);

    }

    private void setViews(){
        tvDate = findViewById(R.id.tv_details_date);
        tvTotal = findViewById(R.id.tv_details_total);
        tvRobertPart = findViewById(R.id.tv_details_robert_part);
        tvPaulinaPart = findViewById(R.id.tv_details_paulina_part);
        tvPayment = findViewById(R.id.tv_details_payment);
        tvBilans = findViewById(R.id.tv_details_bilans);
        tvDescription = findViewById(R.id.tv_details_description);
        tvSaldo = findViewById(R.id.tv_details_saldo);

        lv = (ListView) findViewById(R.id.listView);

        customAdapter = new CustomAdapter(this, StartActivity.dataList);
        if (!StartActivity.dataList.isEmpty()) lv.setAdapter(customAdapter);

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
                        StartActivity.dataList.remove(posiition);
                        salda();
                        makeJsonFile();
                        forceCheckedPosition = 0;
                        if (!StartActivity.dataList.isEmpty()){
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
        RowData rd = StartActivity.dataList.get(position);

        String pln = " zł";
        String date = new DateHelper().dateToString(rd.getDate());
        float total = rd.getTotal();
        float robertPart = rd.getRobertPart();
        float paulinaPart = rd.getPaulinaPart();
        String payment = rd.getPayment();
        float bilansP = rd.getBilansP();
        float bilansR = rd.getBilansR();
        String description = rd.getDescription();
        //int saldo ;

        tvDate.setText(date);
        tvTotal.setText(String.format("%.02f",total)+pln);
        if(robertPart==0){
            tvRobertPart.setText("-");
        }else{
            tvRobertPart.setText(String.format("%.02f",robertPart)+pln);
        }
        if(paulinaPart==0){
            tvPaulinaPart.setText("-");
        }else{
            tvPaulinaPart.setText(String.format("%.02f",paulinaPart)+pln);
        }
        tvPayment.setText(payment);

        String bilans = null;

        if(bilansP==0){
            bilans = "Robert:   - "+String.format("%.02f",bilansR)+pln;
        }

        if(bilansR==0){
            bilans = "Paulina: - "+String.format("%.02f",bilansP)+pln;
        }


        tvBilans.setText(bilans);
        tvDescription.setText(description);

        float saldo = StartActivity.dataList.get(position).getSaldo();
        String saldoString;
        if(saldo>0){
            saldoString = "Saldo:    Paulina  -"+String.format("%.02f",saldo)+pln;//String.valueOf(saldo)+pln;
        }else if(saldo<0){
            saldoString = "Saldo:    Robert  -"+String.format("%.02f",-saldo)+pln;
        }else{
            saldoString = "Zobowiązania uregulowane";
        }
        tvSaldo.setText(saldoString);
    }

    private void clearDetails(){
        tvDate.setText("-");
        tvTotal.setText("-");
        tvRobertPart.setText("-");
        tvPaulinaPart.setText("-");
        tvPayment.setText("-");
        tvBilans.setText("-");
        tvDescription.setText("-");
        tvSaldo.setText("-");
    }

    private void salda(){
        float saldo = 0;
        RowData rd = new RowData();
        for(int i = StartActivity.dataList.size()-1; i>=0; i--){
            float bilansP = StartActivity.dataList.get(i).getBilansP();
            float bilansR = StartActivity.dataList.get(i).getBilansR();

            if(i<StartActivity.dataList.size()-1){
                saldo = StartActivity.dataList.get(i+1).getSaldo() + bilansP-bilansR;
            }else{
                saldo =bilansP-bilansR;
            }
            StartActivity.dataList.get(i).setSaldo(saldo);
        }
        StartActivity.totalBallance = saldo;
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

//        boolean saveToTempFolder = filename.equals(TEMP_JSON_FILENAME);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        ArrayList<RowData> list = StartActivity.dataList;
        try {

            for (int i=0; i< list.size(); i++){
                JSONObject jsonRow = new JSONObject();
                jsonRow.put(KEY_DATE, new DateHelper().dateToString(list.get(i).getDate()));
                jsonRow.put(KEY_TOTAL, list.get(i).getTotal());
                jsonRow.put(KEY_PPART, list.get(i).getPaulinaPart());
                jsonRow.put(KEY_RPART, list.get(i).getRobertPart());
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
