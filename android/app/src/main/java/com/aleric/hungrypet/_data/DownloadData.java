package com.aleric.hungrypet._data;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.aleric.hungrypet.DownloadListener;
import com.aleric.hungrypet._data.database.DbScheduleManager;
import com.aleric.hungrypet._data.shedule.Schedule;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DownloadData extends AsyncTask<Void, Void, List<Schedule>> {

    private static final String TAG = "DownloadData";
    private static final String JSON_SUCCESS = "success";
    private static final String JSON_DATA = "data";
    private static final String JSON_NAME = "nome";
    private static final String JSON_SURNAME = "cognome";
    private static final String JSON_AGE = "eta";
    private static final String JSON_EMAIL = "email";

    private static final String BASE_URL = "http://hungrypet.altervista.org/request_data.php?table=schedule&mac=";

    private Activity mActivity;
    private DownloadListener mListener;

    public DownloadData(Activity activity, DownloadListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    @Override
    protected List<Schedule> doInBackground(Void... params) {

        Station station = StationDirectory.getInstance().getStation();
        final String finalUrl = BASE_URL + station.getMac();
        Log.d(TAG, "urlToConnect: " + finalUrl);

        BufferedReader bufferedReader = null;
        HttpURLConnection connection = null;
        try {
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

                    List<Schedule> schedules = new ArrayList<>(array.length());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject scheduleObject = array.getJSONObject(i);

                        String id = scheduleObject.optString(Schedule._ID);
                        String mac = scheduleObject.optString(Schedule.COLUMN_MAC);
                        int week_day = Integer.valueOf(scheduleObject.optString(Schedule.COLUMN_WEEK_DAY));
                        int hour = Integer.valueOf(scheduleObject.optString(Schedule.COLUMN_HOUR));
                        int deleted = scheduleObject.optInt(Schedule.COLUMN_DELETED);

                        String stringCreate = scheduleObject.optString(Schedule.COLUMN_DATE_CREATE);
                        String stringUpdate = scheduleObject.optString(Schedule.COLUMN_DATE_UPDATE);
                        Date dateCreate = Schedule.FORMAT_DATA.parse(stringCreate);
                        Date dateUpdate = Schedule.FORMAT_DATA.parse(stringUpdate);

                        Schedule schedule = new Schedule(id, mac, week_day, hour, dateCreate, dateUpdate, deleted);
                        schedules.add(schedule);
                    }
                    return schedules;
                }
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, "L'url non è formattato correttamente", e);
        } catch (IOException e) {
            Log.e(TAG, "Errore durante la connessione con il server", e);
        } catch (JSONException e) {
            Log.e(TAG, "Errore durante la deserializzazioen della risposta", e);
        } catch (ParseException e) {
            Log.e(TAG, "Errore durante la coneversione in formato Data", e);
        } finally {
            if (connection != null)
                connection.disconnect();
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (Exception ignored) {
            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(List<Schedule> remoteSchedules) {
        super.onPostExecute(remoteSchedules);
        if (remoteSchedules == null) {
            Log.d(TAG, "Errore nel download dei dati");

            new AlertDialog.Builder(mActivity)
                    .setCancelable(false)
                    .setTitle("Attenzione")
                    .setMessage("Non è stato possibile ottenere i dati dal server, controlla la tua connessione e riprova più tardi")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mListener != null) {
                                mListener.onFinishDownload();
                            }
                        }
                    })
                    .show();

        } else {
            Log.d(TAG, "Download completato correttamente");

            DbScheduleManager dbSchedule = new DbScheduleManager(mActivity);
            List<Schedule> localSchedules = dbSchedule.getSchedules(StationDirectory.getInstance().getStation().getMac());
            List<Schedule> toRemote = new ArrayList<>();
            for (Schedule localSchedule : localSchedules) {
                // Local Available
                int index = remoteSchedules.indexOf(localSchedule);
                if (index > -1) {
                    // Get the remote schedule
                    Schedule remoteSchedule = remoteSchedules.get(index);
                    // Remove to be able to understand witch doesn't exist in local
                    remoteSchedules.remove(index);
                    // Compare the schedules
                    int compareLocalRemote =
                            localSchedule.getDateUpdate().compareTo(remoteSchedule.getDateUpdate());
                    if (compareLocalRemote > 0) {
                        // Local date > Remote date
                        toRemote.add(localSchedule);
                    } else if (compareLocalRemote < 0) {
                        // Local date < Remote date
                        dbSchedule.updateSchedule(remoteSchedule);
                    }
                } else {
                    // Local schedule do not exist in remote or it has to be deleted
                    toRemote.add(localSchedule);
                }
            }

            for (Schedule toLocal : remoteSchedules) {
                dbSchedule.addSchedule(toLocal);
            }

            new UploadData(mActivity, toRemote).execute();

            if (mListener != null) {
                mListener.onFinishDownload();
            }
        }
    }
}
