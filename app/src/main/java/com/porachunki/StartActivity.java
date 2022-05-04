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
    public static float totalBalance = 0;
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
        final String KEY_BILL = "bill";
        final String KEY_PERSON1_PART = "person1_part";
        final String KEY_PERSON2_PART = "person2_part";
        final String KEY_WHO_PAYS = "who_pays";
        final String KEY_DESCRIPTION = "description";
        final String KEY_PERSON1_TRANSACTION_BALANCE = "person1_transaction_balance";
        final String KEY_PERSON2_TRANSACTION_BALANCE = "person2_transaction_balance";
        final String KEY_BALANCE = "balance";
        final String KEY_JSON_ARRAY = "json_array";

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
                JSONArray jsonArray = loadedJsnObject.getJSONArray(KEY_JSON_ARRAY);

//                Log.d("kroko_JSONObject", loadedJsnObject.toString());

                RowData rd;
                for(int i=0; i<jsonArray.length(); i++){
                    rd = new RowData();
                    rd.setBill((float)jsonArray.getJSONObject(i).getDouble(KEY_BILL));
                    try{
                        date = new SimpleDateFormat("dd.MM.yyyy").parse(jsonArray.getJSONObject(i).getString(KEY_DATE));
                        rd.setDate(date);
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                    rd.setPerson1Part((float)jsonArray.getJSONObject(i).getDouble(KEY_PERSON1_PART));
                    rd.setPerson2Part((float)jsonArray.getJSONObject(i).getDouble(KEY_PERSON2_PART));
                    rd.setWhoPays(jsonArray.getJSONObject(i).getString(KEY_WHO_PAYS));
                    rd.setDescription(jsonArray.getJSONObject(i).getString(KEY_DESCRIPTION));
                    rd.setPerson1TransationBalance((float)jsonArray.getJSONObject(i).getDouble(KEY_PERSON1_TRANSACTION_BALANCE));
                    rd.setPerson2TransationBalance((float)jsonArray.getJSONObject(i).getDouble(KEY_PERSON2_TRANSACTION_BALANCE));
                    float balance = (float)jsonArray.getJSONObject(i).getDouble(KEY_BALANCE);
                    rd.setBalance(balance);

                    // Nie wpisuje do tabeli rekordów starszych niż miesiąc
                    if (date.after(monthAgo)){
//                        Log.d("kroko", date.toString()+"  w if");
                        dataList.add(rd);
                    }else{
                        // zapisuje saldo z pierwszego niewpisanego rekordu jako saldo początkowe (do funkcji "salda")
                        if( ! isInitialBallanceUpdated){
                            isInitialBallanceUpdated = true;
                            writeInitialBallance(balance);
                        }
                    }

                    if(i==0){
                        totalBalance = (float)jsonArray.getJSONObject(i).getDouble(KEY_BALANCE);
//                        totalBallance = balance;
                    }

                }
            }catch (JSONException e){
                e.printStackTrace();
                Log.d("kroko_JSON e", "e");
            }
        }else{
            Log.d("kroko_JSON test", "nie ma JSON");
        }
    }


    // Przygotowuje String do wyświetlenie całkowitego salda
    private void updateTotalBallance(){
        String totalballanceString;
        String pln = " zł";
        String person1 = getString(R.string.person_1_name);
        String person2 = getString(R.string.person_2_name);

        if(totalBalance >0){
            totalballanceString = person1+"  -"+String.format("%.02f", totalBalance)+pln;
        }else if(totalBalance <0){
            totalballanceString = person2+"  -"+String.format("%.02f",-totalBalance)+pln;
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
