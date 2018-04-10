package id.kosan.mgmapp;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ProfileActivity extends AppCompatActivity implements TaskListener {

    TextView txtNama;
    TextView txtCC;
    TextView txtCount;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtNama = (TextView)  findViewById(R.id.txtNama);
        txtCC = (TextView)  findViewById(R.id.txtCC);
        txtCount = (TextView)  findViewById(R.id.txtCount);

        SharedPreferences sp1=this.getSharedPreferences("Login", MODE_PRIVATE);
        String nama = sp1.getString("NAMA", null);
        String cc = sp1.getString("CC", null);

        if(nama == null)
            finish();

        txtNama.setText(nama);
        txtCC.setText(cc);

        String data = "p1="+encode(nama)+"&p2="+encode(cc);
        ApiHelper gc = new ApiHelper(getApplicationContext(), this, "count", data);
        gc.execute();

        ActionBar ab = this.getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        LayoutInflater li = LayoutInflater.from(this);
        View customView = li.inflate(R.layout.title_profile, null);
        ab.setCustomView(customView);

        ImageButton btnBack = customView.findViewById(R.id.btn_back);
        ImageButton btnLogout = customView.findViewById(R.id.btn_logout);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor Ed=sp.edit();
                Ed.clear();
                Ed.commit();
                finish();
            }
        });
    }

    @Override
    public void onTaskStarted() {
        progressDialog = ProgressDialog.show(ProfileActivity.this, "Loading", "Mengunduh data");
    }

    @Override
    public void onTaskFinished(String result) {
        txtCount.setText(result);
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