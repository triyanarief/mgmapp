package id.kosan.mgmapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.guna.libmultispinner.MultiSelectionSpinner;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener, TaskListener{

    ProgressDialog progressDialog;
    private static final int REQUEST_KTP = 0;
    private static final int REQUEST_CCBankLain = 1;
    private static final int REQUEST_NPWP = 2;
    private static final int REQUEST_KartuNama = 3;

    String[] items = {"Mega Travel Card",
            "Mega Visa Platinum",
            "Mega Visa Gold",
            "Mega Barca Card",
            "Metro Mega Card",
//            "TSM Ultima"
    };

    private Button btnKtp;
    private Button btnCCBankLain;
    private Button btnNPWP;
    private Button btnKartuNama;
    private Button btnSimpan;

    private ImageView imgKtp;
    private ImageView imgCCBankLain;
    private ImageView imgNPWP;
    private ImageView imgKartuNama;

    Bitmap[] imgBmp = new Bitmap[4];
    String[] tessTxt = new String[4];

    private EditText txtNamaPemberiReferensi;
//    private EditText txtNamaPerusahaan;
    private EditText txtNoPonsel;
    private EditText txtEmail;
    private EditText txtNama;
//    private EditText txtEmailPemohon;
    private EditText txtHpPemohon;
//    private EditText txtHpPemohon2;
//    private EditText txtTeleponPemohon;

    private MultiSelectionSpinner ddlJenisKartu;
    TextRecognizer txtRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar ab = this.getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        LayoutInflater li = LayoutInflater.from(this);
        View customView = li.inflate(R.layout.title_main, null);
        ab.setCustomView(customView);

        ImageButton btnProfile = customView.findViewById(R.id.btn_profile);
        ImageButton btnLogout = customView.findViewById(R.id.btn_logout);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor Ed = sp.edit();
                Ed.clear();
                Ed.commit();
                finish();
            }
        });

        txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        imgKtp = (ImageView) findViewById(R.id.imgKtp);
        imgCCBankLain = (ImageView) findViewById(R.id.imgCCBankLain);
        imgNPWP = (ImageView) findViewById(R.id.imgNPWP);
        imgKartuNama = (ImageView) findViewById(R.id.imgKartuNama);

        btnKtp = (Button) findViewById(R.id.btnKtp);
        btnCCBankLain = (Button) findViewById(R.id.btnCCBankLain);
        btnNPWP = (Button) findViewById(R.id.btnNPWP);
        btnKartuNama = (Button) findViewById(R.id.btnKartuNama);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);

        txtNamaPemberiReferensi = (EditText) findViewById(R.id.txtNamaPemberiReferensi);
//        txtNamaPerusahaan = (EditText) findViewById(R.id.txtNamaPerusahaan);
        txtNoPonsel = (EditText) findViewById(R.id.txtNoPonsel);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtNama = (EditText) findViewById(R.id.txtNama);
//        txtEmailPemohon = (EditText) findViewById(R.id.txtEmailPemohon);
        txtHpPemohon = (EditText) findViewById(R.id.txtHpPemohon);
//        txtHpPemohon2 = (EditText) findViewById(R.id.txtHpPemohon2);
//        txtTeleponPemohon = (EditText) findViewById(R.id.txtTeleponPemohon);

        ddlJenisKartu = (MultiSelectionSpinner) findViewById(R.id.ddlJenisKartu);
        ddlJenisKartu.setItems(items);
        ddlJenisKartu.setListener(this);

        btnKtp.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA, REQUEST_KTP));
        btnCCBankLain.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA, REQUEST_CCBankLain));
        btnNPWP.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA, REQUEST_NPWP));
        btnKartuNama.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA, REQUEST_KartuNama));

        SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
        String nama = sp1.getString("NAMA", null);
        String perusahaan = sp1.getString("PERUSAHAAN", "");
        String nopon = sp1.getString("NOPON", "");
        String email = sp1.getString("EMAIL", "");
        String cc = sp1.getString("CC", "");
        if (nama == null)
            finish();

        txtNamaPemberiReferensi.setText(nama);
//        txtNamaPerusahaan.setText(perusahaan);
        txtNoPonsel.setText(nopon);
        txtEmail.setText(email);
//
//        DATA_PATH = getFilesDir()+ "/tesseract/";
//        checkFile(new File(DATA_PATH));

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String error = "Error List:";
                EditText[] list = {txtNamaPemberiReferensi, /*txtNamaPerusahaan, */ txtNoPonsel, txtNama, /*txtEmailPemohon,*/ txtHpPemohon};
                String[] msg = {"Nama Pemberi Referensi", /* "Nama Perusahaan", */"No Ponsel", "Nama Pemohon", /*"Email Pemohon",*/ "No Ponsel Pemohon"};

                try {
                    for (int i = 0; i < list.length; i++) {
                        String txt = list[i].getText().toString();
                        if (txt.length() == 0)
                            error += "\n- " + msg[i] + " Harus Diisi";
                    }

                    if (imgBmp[REQUEST_KTP] == null)
                        error += "\n- KTP harus diupload";
                    if (imgBmp[REQUEST_CCBankLain] == null)
                        error += "\n- KK Bank Lain harus diupload";

                    if (error != "Error List:") {
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences sp1 = MainActivity.this.getSharedPreferences("Login", MODE_PRIVATE);
                    String cc = sp1.getString("CC", "");

                    String p1 = txtNamaPemberiReferensi.getText().toString();
//                    String p2 = txtNamaPerusahaan.getText().toString();
                    String p3 = txtNoPonsel.getText().toString();
                    String p4 = txtEmail.getText().toString();
                    String p5 = cc;
                    String p6 = txtNama.getText().toString();
//                    String p7 = txtEmailPemohon.getText().toString();
                    String p8 = txtHpPemohon.getText().toString();
//                    String p9 = txtHpPemohon2.getText().toString();
//                    String p10 = txtTeleponPemohon.getText().toString();
                    String p15 = ddlJenisKartu.getSelectedItemsAsString();

                    SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor Ed = sp.edit();
//                    Ed.putString("PERUSAHAAN", p2);
                    Ed.putString("NOPON", p3);
                    Ed.putString("EMAIL", p4);
                    Ed.commit();

                    String data = "";
                    data += "p1=" + encode(p1) + "&";
//                    data += "p2=" + encode(p2) + "&";
                    data += "p3=" + encode(p3) + "&";
                    data += "p4=" + encode(p4) + "&";
                    data += "p5=" + encode(p5) + "&";
                    data += "p6=" + encode(p6) + "&";
//                    data += "p7=" + encode(p7) + "&";
                    data += "p8=" + encode(p8) + "&";
//                    data += "p9=" + encode(p9) + "&";
//                    data += "p10=" + encode(p10) + "&";

                    for (int i = 0; i < tessTxt.length; i++) {
                        if (tessTxt[i] == null)
                            continue;
                        data += "p1" + (i + 6) + "=" + encode(tessTxt[i]) + "&";
                    }

                    for (int i = 0; i < imgBmp.length; i++) {
                        if (imgBmp[i] == null)
                            continue;
                        String bmpres = encode(getStringImage(imgBmp[i]));
                        data += "p1" + (i + 1) + "=" + bmpres + "&";
                    }

                    data += "p15=" + encode(p15);
                    ApiHelper doSave= new ApiHelper(getApplicationContext(), MainActivity.this, "upload", data);
                    doSave.execute();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.error_msg + "\n- " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onTaskStarted() {
        progressDialog = ProgressDialog.show(MainActivity.this, "Loading", "Mengunggah Data");
    }

    @Override
    public void onTaskFinished(String result) {
        if (result.trim().equals("sukses")) {
            resetForm();
            Toast.makeText(getApplicationContext(), "Terima Kasih!\n" + getString(R.string.sukses_simpan), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_msg, Toast.LENGTH_LONG).show();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private class ScanButtonClickListener implements View.OnClickListener {

        private int preference;
        private int request;

        public ScanButtonClickListener(int preference, int request) {
            this.preference = preference;
            this.request = request;
        }

        @Override
        public void onClick(View v) {
            startScan(preference, request);
        }
    }

    protected void startScan(int preference, int request) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            ImageView imgView = new ImageView(this);
            switch (requestCode){
                case REQUEST_KTP:
                    imgView = imgKtp;
                    break;
                case REQUEST_CCBankLain:
                    imgView = imgCCBankLain;
                    break;
                case REQUEST_NPWP:
                    imgView = imgNPWP;
                    break;
                case REQUEST_KartuNama:
                    imgView = imgKartuNama;
                    break;
            }
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);

                String txt = "";
                try {
                    txt = extractText(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tessTxt[requestCode] = txt;
                imgBmp[requestCode] = bitmap;
                imgView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void selectedIndices(List<Integer> indices) {

    }

    @Override
    public void selectedStrings(List<String> strings) {

    }

    String DATA_PATH;
    private String extractText(Bitmap bitmap) throws Exception {
        if (!txtRecognizer.isOperational())
            return "Detector dependencies are not yet available";
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray items = txtRecognizer.detect(frame);
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            TextBlock item = (TextBlock) items.valueAt(i);
            strBuilder.append(item.getValue());
            strBuilder.append("/");
        }
        return strBuilder.toString();
    }

    public void resetForm(){
        txtNama.setText("");
//        txtEmailPemohon.setText("");
        txtHpPemohon.setText("");
//        txtHpPemohon2.setText("");
//        txtTeleponPemohon.setText("");
        imgBmp = new Bitmap[4];
        tessTxt = new String[4];
        imgKtp.setImageBitmap(null);
        imgCCBankLain.setImageBitmap(null);
        imgNPWP.setImageBitmap(null);
        imgKartuNama.setImageBitmap(null);
        ddlJenisKartu.setItems(items);
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public static String encode(String txt){
        try {
            return URLEncoder.encode(txt, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return txt;
        }
    }


}