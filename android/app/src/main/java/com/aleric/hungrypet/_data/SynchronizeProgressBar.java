package com.aleric.hungrypet._data;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ProgressBar;

import com.aleric.hungrypet.R;
import com.aleric.hungrypet._data.station.Station;
import com.aleric.hungrypet._data.station.StationDirectory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SynchronizeProgressBar extends Thread{

    private static final String TAG = "SynchronizeSchedule";
    private static final String JSON_SUCCESS = "success";
    private static final String JSON_DATA = "data";
    private static final String JSON_TYPE = "type";
    private static final String JSON_LEVEL = "level";
    private static final String TYPE_CONT = "container";
    private static final String TYPE_BOWL = "bowl";

    private static final String BASE_URL = "http://hungrypet.altervista.org/request_data.php?table=food_level&mac=";

    private Activity mActivity;
    private ProgressBar mPgbLevelContainer;
    private ProgressBar mPgbLevelBowl;
    private Boolean loop = true;

    public SynchronizeProgressBar(Activity activity, ProgressBar pgbLevelContainer, ProgressBar pgbLevelBowl) {
        mActivity = activity;
        mPgbLevelContainer = pgbLevelContainer;
        mPgbLevelBowl = pgbLevelBowl;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run() {
        Station station = StationDirectory.getInstance().getStation();
        final String finalUrl = BASE_URL + station.getMac();
        Log.d(TAG, "urlToConnect: " + finalUrl);

        BufferedReader bufferedReader = null;
        HttpURLConnection connection = null;


        try {
            while (this.loop) {
                URL url = new URL(finalUrl);
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

                    if (responseJson.getBoolean(JSON_SUCCESS)) {
                        JSONArray array = responseJson.getJSONArray(JSON_DATA);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject scheduleObject = array.getJSONObject(i);

                            String type = scheduleObject.optString(this.JSON_TYPE);
                            int level = Integer.valueOf(scheduleObject.optString(this.JSON_LEVEL));

                            if (type.equals(this.TYPE_CONT)) {
                                mPgbLevelContainer.setMax(100);
                                mPgbLevelContainer.setProgress(0, true);
                                mPgbLevelContainer.setProgress(level, true);
                                //Set color
                                if (level < 25) {
                                    mPgbLevelContainer.setProgressTintList(ColorStateList.valueOf(
                                            mActivity.getResources().getColor(R.color.colorRed)));
                                } else if (level < 50) {
                                    mPgbLevelContainer.setProgressTintList(ColorStateList.valueOf(
                                            mActivity.getResources().getColor(R.color.colorYellow)));
                                } else if (level < 75) {
                                    mPgbLevelContainer.setProgressTintList(ColorStateList.valueOf(
                                            mActivity.getResources().getColor(R.color.colorGreen)));
                                } else if (level > 75) {
                                    mPgbLevelContainer.setProgressTintList(ColorStateList.valueOf(
                                            mActivity.getResources().getColor(R.color.colorBlue)));
                                }
                            } else if (type.equals(this.TYPE_BOWL)) {
                                mPgbLevelBowl.setMax(100);
                                mPgbLevelBowl.setProgress(0, true);
                                mPgbLevelBowl.setProgress(level, true);
                                //Set color
                                if (level < 25) {
                                    mPgbLevelBowl.setProgressTintList(ColorStateList.valueOf(
                                            mActivity.getResources().getColor(R.color.colorRed)));
                                } else if (level < 50) {
                                    mPgbLevelBowl.setProgressTintList(ColorStateList.valueOf(
                                            mActivity.getResources().getColor(R.color.colorYellow)));
                                } else if (level < 75) {
                                    mPgbLevelBowl.setProgressTintList(ColorStateList.valueOf(
                                            mActivity.getResources().getColor(R.color.colorGreen)));
                                } else if (level > 75) {
                                    mPgbLevelBowl.setProgressTintList(ColorStateList.valueOf(
                                            mActivity.getResources().getColor(R.color.colorBlue)));
                                }
                            }
                        }

                    }
                }
                SystemClock.sleep(10000);
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, "L'url non è formattato correttamente", e);
        } catch (IOException e) {
            Log.e(TAG, "Errore durante la connessione con il server", e);
        } catch (JSONException e) {
            Log.e(TAG, "Errore durante la deserializzazioen della risposta", e);
        } catch (Exception e) {
            e.printStackTrace();
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

    public void cancel() {
        this.loop = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setProgress(ProgressBar progressBar, int level) {
        // Set level
        progressBar.setProgress(level, true);
        //Set color
        if (level < (25)) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else if (level < (50)) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        } else if (level < (75)) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        } else if (level < (100)) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        }
    }
}
