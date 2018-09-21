package com.aleric.hungrypet._data.shedule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.aleric.hungrypet.DownloadListener;
import com.aleric.hungrypet._data.database.DbScheduleManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class UploadSchedule extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "UploadSchedule";
    private static final String JSON_SUCCESS = "success";
    private static final String JSON_ACTION = "action";
    private static final String ACTION = "upload";
    private static final String BASE_URL = "http://hungrypet.altervista.org/upload_data.php?" +
            "table=schedule&id=%s&mac=%s&week_day=%s&hour=%s&date_create=%s&date_update=%s&deleted=%d";

    private Context mContext = null;
    private List<Schedule> mSchedules = null;
    private DownloadListener mListener = null;

    public UploadSchedule(Context context, List<Schedule> schedules) {
        mContext = context;
        mSchedules = schedules;
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected Boolean doInBackground(Void... params) {

        BufferedReader bufferedReader = null;
        HttpURLConnection connection = null;
        DbScheduleManager dbScheduleManager = new DbScheduleManager(mContext);
        List<Schedule> errorUploadSchedules = new ArrayList<>();
        String urlString = "";
        boolean success = true;

        for (Schedule schedule : mSchedules) {

            urlString = String.format(
                    BASE_URL,
                    schedule.getId(),
                    schedule.getMac(),
                    schedule.getWeekDay(),
                    schedule.getHour(),
                    schedule.getDateCreateToString(),
                    schedule.getDateUpdateToString(),
                    schedule.getDeleted());

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
                        Log.e(TAG, "Upload of data with errors " + responseJson.getString("message"));
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
        }

        return success;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (!success) {

            if (mContext != null) {
                new AlertDialog.Builder(mContext)
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
