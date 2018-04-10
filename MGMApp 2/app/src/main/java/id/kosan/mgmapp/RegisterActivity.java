package id.kosan.mgmapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity implements TaskListener {

    ProgressDialog progressDialog;
    Button btnRegister;
    EditText txtNama;
    EditText txtNomorKartu;
    String[] validator = {"431226",
            "420194",
            "420192",
            "420191",
            "524261",
            "489087",
            "489087",
            "478487"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        txtNama = (EditText)  findViewById(R.id.txtNama);
        txtNomorKartu = (EditText)  findViewById(R.id.txtNomorKartu);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            boolean isValid = false;
            String nama = txtNama.getText().toString();
            String nomor = txtNomorKartu.getText().toString();
            String cekValid = nomor.substring(0,6);
            for (int i = 0 ;i<validator.length;i++){
                if(cekValid.equals(validator[i])){
                    isValid = true;
                    break;
                }
            }

            if(isValid) {
                String data = "p1=" + encode(nama) + "&p2=" + encode(nomor);
                ApiHelper api = new ApiHelper(getApplicationContext(), RegisterActivity.this, "register", data);
                api.execute();
            } else {
                Toast.makeText(getApplicationContext(), "Anda hanya dapat mendaftar menggunakan kartu kredit Bank Mega.", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    @Override
    public void onTaskStarted() {
        progressDialog = ProgressDialog.show(RegisterActivity.this, "Loading", "Mengunggah data");
    }

    @Override
    public void onTaskFinished(String result) {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.putExtra("NAMA", result.trim());
        startActivity(i);
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