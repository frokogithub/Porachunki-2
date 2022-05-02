package com.porachunki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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

    // statyczne pola dostępne z innych klas
    public static float totalBallance = 0;
    public static ArrayList<RowData> dataList = new ArrayList<>();


    TextView tvTotalBallance;

    @Override
    protected void onResume() {
        super.onResume();

        // Hide status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();

        // aktualizuje saldo przy każdym powrocie do aktywności
        updateTotalBallance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalBallance = findViewById(R.id.tv_start_total_ballance);

        setButtons();
        setDataList();
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

    // Tworzy tablicę z danych zapisanych w pliku JSON
    private void setDataList(){

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

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30); //30 dni temu
        Date date = null;
        Date monthAgo = cal.getTime();

        boolean isInitialBallanceUpdated = false;

        dataList.clear(); //todo: czy potrzebne?
        JsonFileUtility jsonFileUtility = new JsonFileUtility(getApplicationContext());
        JSONObject loadedJsnObject = jsonFileUtility.loadJson();

        if(loadedJsnObject!=null){

            try {
                JSONArray jsonArray = loadedJsnObject.getJSONArray(KEY_ARRAY);

//                Log.d("kroko_JSONObject", loadedJsnObject.toString());

                RowData rd = new RowData();
                for(int i=0; i<jsonArray.length(); i++){
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
                    float saldo = (float)jsonArray.getJSONObject(i).getDouble(KEY_SALDO);
                    rd.setSaldo(saldo);

                    // Nie wpisuje do tabeli rekordów starszych niż miesiąc
                    if (date.after(monthAgo)){
//                        Log.d("kroko", date.toString()+"  w if");
                        dataList.add(rd);
                    }else{
                        // zapisuje saldo z pierwszego niewpisanego rekordu jako saldo początkowe (do funkcji "salda")
                        if( ! isInitialBallanceUpdated){
                            isInitialBallanceUpdated = true;
                            writeInitialBallance(saldo);
                        }
                    }

                    if(i==0){
                        totalBallance = (float)jsonArray.getJSONObject(i).getDouble(KEY_SALDO);
//                        totalBallance = saldo;
                    }

                }
            }catch (JSONException e){
                e.printStackTrace();
                Log.d("kroko_JSON e", "e");
            }
        }else{
            Log.d("kroko_JSON test", "JSON object NULL");
        }
    }


    // Przygotowuje String do wyświetlenie całkowitego salda
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

    private void writeInitialBallance(float initialBallance){
        final String KEY_INITIAL_BALLANCE = "initial_ballance";
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(KEY_INITIAL_BALLANCE, initialBallance);
        editor.apply();
    }
}
