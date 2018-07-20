package com.aleric.hungrypet._data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.aleric.hungrypet.DownloadListener;
import com.aleric.hungrypet._data.database.DbScheduleManager;
import com.aleric.hungrypet._data.shedule.Schedule;
import com.aleric.hungrypet._data.station.StationDirectory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class UploadInstantFood extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "UploadSchedule";
    private static final String JSON_SUCCESS = "success";
    private static final String JSON_ACTION = "action";
    private static final String ACTION = "upload";
    private static final String BASE_URL = "http://hungrypet.altervista.org/upload_data.php?" +
            "table=instant_food&mac=%s&date_create=%s&date_update=%s";
    public static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat FORMAT_DATA = new SimpleDateFormat(PATTERN_DATE);

    private Activity mActivity = null;

    public UploadInstantFood(Activity activity) {
        mActivity = activity;
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected Boolean doInBackground(Void... params) {

        BufferedReader bufferedReader = null;
        HttpURLConnection connection = null;
        DbScheduleManager dbScheduleManager = new DbScheduleManager(mActivity);
        List<Schedule> errorUploadSchedules = new ArrayList<>();
        boolean success = true;
        String mac = StationDirectory.getInstance().getStation().getMac();
        Date dateNow = Calendar.getInstance().getTime();
        String dateNowString = FORMAT_DATA.format(dateNow);
        String urlString = String.format(
                BASE_URL,
                mac,
                dateNowString,
                dateNowString);

        Log.d(TAG, "urlToConnect: " + urlString);
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(60 * 1000);
            connection.setConnectTimeout(60 * 1000);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                InputStream inputStream = connection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject responseJson = new JSONObject(response.toString());
                if (responseJson.getString(JSON_ACTION).equals(ACTION) && responseJson.getBoolean(JSON_SUCCESS)) {
                    Log.d(TAG, "success == true");
                } else {
                    Log.e(TAG, "Upload of instant feed with errors ");
                    success = false;
                }
            }


        } catch (MalformedURLException e) {
            Log.e(TAG, "L'url non è formattato correttamente", e);
        } catch (IOException e) {
            Log.e(TAG, "Errore durante la connessione con il server", e);
        } catch (JSONException e) {
            Log.e(TAG, "Errore durante la deserializzazioen della risposta", e);
        } finally {
            if (connection != null)
                connection.disconnect();
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (Exception ignored) {
            }
        }


        return success;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (!success) {

            if (mActivity != null) {
                new AlertDialog.Builder(mActivity)
                        .setCancelable(false)
                        .setTitle("Attention")
                        .setMessage("Upload data with errors, check your connection and try again later")
                        .setPositiveButton("OK", null)
                        .show();
            }

        } else {
            Log.d(TAG, "Upload completed");
        }
    }
}
