package com.porachunki;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StartActivity extends AppCompatActivity {

    public static float totalBallance = 0;
    public static ArrayList<RowData> dataList = new ArrayList<>();
    private final static String KEY_DATE = "date";
    private final static String KEY_TOTAL = "total";
    private final static String KEY_PPART = "paulina_part";
    private final static String KEY_RPART = "robert_part";
    private final static String KEY_PAYMENT = "payment";
    private final static String KEY_DESCRIPTION = "description";
    private final static String KEY_PBILANS = "paulina_bilans";
    private final static String KEY_RBILANS = "robert_bilans";
    private final static String KEY_SALDO = "saldo";
    private final static String KEY_ARRAY = "all data";

    TextView tvTotalBallance;

    @Override
    protected void onResume() {
        super.onResume();

        // Hide status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();
        updateTotalBallance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalBallance = findViewById(R.id.tv_start_total_ballance);

        setButtons();
        setDataList();

//        setList_TEST();
//        setRowDataList();

    }

    private void setButtons(){
        Button btAddTransaction = findViewById(R.id.btn_add_transaction);
        Button btQuickTransaction = findViewById(R.id.btn_add_quick_transaction);
        Button btHistory = findViewById(R.id.btn_view_database);

        btAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TransactionActivity.start(getApplicationContext());
            }
        });

        btQuickTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuicTransactionActivity.start(getApplicationContext());
            }
        });

        btHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HistoryActivity.start(getApplicationContext(),0);
            }
        });
    }

    private void setDataList(){

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date date = null;
        Date monthAgo = cal.getTime();

        dataList.clear(); //todo: czy potrzebne?
        JsonFileUtility jsonFileUtility = new JsonFileUtility(getApplicationContext());
        JSONObject loadedJsnObject = jsonFileUtility.loadJson();

        if(loadedJsnObject!=null){

            try {
                JSONArray jsonArray = loadedJsnObject.getJSONArray(KEY_ARRAY);

                Log.d("kroko_JSONObject", loadedJsnObject.toString());

                for(int i=0; i<jsonArray.length(); i++){
                    RowData rd = new RowData();

                    rd.setTotal((float)jsonArray.getJSONObject(i).getDouble(KEY_TOTAL));
                    try{
                        date = new SimpleDateFormat("dd.MM.yyyy").parse(jsonArray.getJSONObject(i).getString(KEY_DATE));
                        rd.setDate(date);
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                    rd.setPaulinaPart((float)jsonArray.getJSONObject(i).getDouble(KEY_PPART));
                    rd.setRobertPart((float)jsonArray.getJSONObject(i).getDouble(KEY_RPART));
                    rd.setPayment(jsonArray.getJSONObject(i).getString(KEY_PAYMENT));
                    rd.setDescription(jsonArray.getJSONObject(i).getString(KEY_DESCRIPTION));
                    rd.setBilansP((float)jsonArray.getJSONObject(i).getDouble(KEY_PBILANS));
                    rd.setBilansR((float)jsonArray.getJSONObject(i).getDouble(KEY_RBILANS));
                    rd.setSaldo((float)jsonArray.getJSONObject(i).getDouble(KEY_SALDO));

                    if (date.after(monthAgo)) dataList.add(rd);

                    if(i==0){
                        float saldo = (float)jsonArray.getJSONObject(i).getDouble(KEY_SALDO);
                        totalBallance = saldo;
                    }

                }
            }catch (JSONException e){
                e.printStackTrace();
                Log.d("kroko_JSON e", "e");
            }
        }
    }

    private void updateTotalBallance(){
        String totalballanceString;
        String pln = " zł";

        if(totalBallance>0){
            totalballanceString = "Paulina  -"+String.format("%.02f",totalBallance)+pln;
        }else if(totalBallance<0){
            totalballanceString = "Robert  -"+String.format("%.02f",-totalBallance)+pln;
        }else{
            totalballanceString = "Zobowiązania uregulowane";
        }
        tvTotalBallance.setText(totalballanceString);
    }

    void test(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date d = cal.getTime();

        System.out.println("Date = "+ cal.getTime());
    }
}
