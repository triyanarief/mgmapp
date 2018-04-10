package id.kosan.mgmapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity implements TaskListener {

    Button btnLogin;
    EditText txtNama;
    EditText txtNomorKartu;
    ProgressBar spinner;
    String nomor;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtNama = (EditText)  findViewById(R.id.txtNama);
        txtNomorKartu = (EditText)  findViewById(R.id.txtNomorKartu);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        String nama = getIntent().getStringExtra("NAMA");
        txtNama.setText(nama);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = txtNama.getText().toString();
                nomor = txtNomorKartu.getText().toString();

                spinner.setVisibility(View.VISIBLE);
                String data = "p1="+encode(nama)+"&p2="+encode(nomor);
                ApiHelper doLogin = new ApiHelper(getApplicationContext(), LoginActivity.this, "login", data);
                doLogin.execute();
            }
        });
    }

    @Override
    public void onTaskStarted() {
        progressDialog = ProgressDialog.show(LoginActivity.this, "Loading", "Logging In . . .");
    }

    @Override
    public void onTaskFinished(String result) {
        result = result.trim();
        if(!result.equals("Anda belum terdaftar.") && result.length() > 0) {
            SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
            SharedPreferences.Editor Ed=sp.edit();
            Ed.putString("NAMA", result);
            Ed.putString("CC", nomor);
            Ed.commit();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static String encode(String txt){
        try {
            return URLEncoder.encode(txt, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return txt;
        }
    }

}
