package id.kosan.mgmapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by ROOT on 2/22/2018.
 */

public class ApiHelper extends AsyncTask<String, Integer, String> {
    private final TaskListener listener;
    private String func;
    private String data;
    private Context context;

    public ApiHelper(Context context, TaskListener listener, String func, String data) {
        this.listener = listener;
        this.data = data;
        this.func = func;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        listener.onTaskStarted();
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onTaskFinished(result);
    }

    public static String httpPostRequest(Context context, String func, String data) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String response = "";
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        try {
            String uri = context.getString(id.kosan.mgmapp.R.string.api_uri);
            URL urlObj = new URL(uri + func);

            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            int responseCode = conn.getResponseCode();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            response = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception ex) {
            }
        }
        return response;
    }

    public static String encode(String txt){
        try {
            return URLEncoder.encode(txt, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return txt;
        }
    }

    @Override
    protected String doInBackground(String... params) {
        return httpPostRequest(context, func, data);
    }
}
