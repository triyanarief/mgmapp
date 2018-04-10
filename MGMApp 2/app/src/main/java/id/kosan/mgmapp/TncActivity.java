package id.kosan.mgmapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TncActivity extends Activity {

    Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AfterHomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        SharedPreferences sp1=this.getSharedPreferences("Login", MODE_PRIVATE);
        String nama = sp1.getString("NAMA", null);
        if(nama != null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            this.finish();
            startActivity(i);
        }
    }
}
