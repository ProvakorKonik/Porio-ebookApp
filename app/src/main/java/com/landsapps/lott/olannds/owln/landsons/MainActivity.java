package com.landsapps.lott.olannds.owln.landsons;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //public static final String KEY_EMAIL_GIVEN = "1109455935";
    public static final String KEY_EMAIL_GIVEN = "key_email_given";
    private final Callback<Void> mCallCallback = new Callback<Void>() {

        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()){
                Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_LONG)
                        .show();
                progressBar.setVisibility(View.GONE);
                SharedPrefUtil.setEmailGiven(MainActivity.this, KEY_EMAIL_GIVEN, true);
                Intent intent = new Intent(MainActivity.this, DrawerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(),"Response FAILED", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    };
    //com.awoonsapps.interw.eet.tennsports.ondveines
    EditText editText;
    ImageView button;
    ProgressBar progressBar;
    private SpreadsheetWebService mSpreadsheetWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("L0TT0LAND-App-Review");

        editText = findViewById(R.id.et_email);
        button = findViewById(R.id.bt_submit);
        progressBar = findViewById(R.id.pb);

        button.setOnClickListener(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://docs.google.com/forms/u/0/d/e/")
                //.baseUrl("https://docs.google.com/forms/u/1/d/e/1FAIpQLSfyomcfGSys0gncSBgucazq6t-27M78vEQXjWccPrIWMBvk_g/")
                //.baseUrl("https://docs.google.com/forms/d/e/1FAIpQLSfyomcfGSys0gncSBgucazq6t-27M78vEQXjWccPrIWMBvk_g/viewform/")
                .build();
         mSpreadsheetWebService = retrofit.create(SpreadsheetWebService.class);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_submit && isValidEmail(editText.getText())){

                if(mSpreadsheetWebService != null){
                    Call<Void> call = mSpreadsheetWebService
                            .sendEmail(editText.getText().toString().trim());
                    call.enqueue(mCallCallback);
                    progressBar.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(this, "Spreadsheet null", Toast.LENGTH_LONG).show();
                }

        }
        else
            Toast.makeText(this, "Enter your gmail", Toast.LENGTH_LONG).show();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}