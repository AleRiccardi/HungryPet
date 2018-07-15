package com.aleric.hungrypet.schedule;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aleric.hungrypet.R;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Per dettagli vedere: server-connection-example, exercise-2 (laboratorio 1).
 */
public class DownloadFragment extends Fragment {

    public interface DownloadListener {
        void onFinishDownload();
    }

    private DownloadListener listener;
    private Activity mActivity = null;

    public DownloadFragment() {
        // Required empty public constructor
    }

    public static DownloadFragment newInstance() {
        return new DownloadFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_download, container, false);
        mActivity = getActivity();
        new DownloadData().execute();

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DownloadListener) {
            listener = (DownloadListener) context;
        } else {
            //Mettere un log
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    /**
     * TODO extract inner class and transform in a real class, then create another analog class for Upload data.
     */
    private class DownloadData extends AsyncTask<Void, Void, List<Schedule>> {

        private static final String JSON_SUCCESS = "success";
        private static final String JSON_DATA = "data";
        private static final String JSON_NAME = "nome";
        private static final String JSON_SURNAME = "cognome";
        private static final String JSON_AGE = "eta";
        private static final String JSON_EMAIL = "email";

        private static final String BASE_URL = "http://hungrypet.altervista.org/request_data.php?table=schedule&mac=";

        @Override
        protected List<Schedule> doInBackground(Void... params) {

            Station station = StationDirectory.getInstance().getStation();
            final String finalUrl = BASE_URL + station.getMac();
            Log.d("DownloadData", "urlToConnect: " + finalUrl);

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
                        Log.d("DownloadData", "success == true");
                        SimpleDateFormat format = new SimpleDateFormat(Schedule.PATTERN_DATE);

                        JSONArray array = responseJson.getJSONArray(JSON_DATA);

                        List<Schedule> schedules = new ArrayList<>(array.length());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject scheduleObject = array.getJSONObject(i);

                            String id = scheduleObject.optString(Schedule._ID);
                            String mac = scheduleObject.optString(Schedule.COLUMN_MAC);
                            int week_day = Integer.valueOf(scheduleObject.optString(Schedule.COLUMN_WEEK_DAY));
                            int hour = Integer.valueOf(scheduleObject.optString(Schedule.COLUMN_HOUR));

                            String stringCreate = scheduleObject.optString(Schedule.COLUMN_DATE_CREATE);
                            String stringUpdate = scheduleObject.optString(Schedule.COLUMN_DATE_UPDATE);
                            Date dateCreate = format.parse(stringCreate);
                            Date dateUpdate = format.parse(stringUpdate);

                            Schedule schedule = new Schedule(id, mac, week_day, hour, dateCreate, dateUpdate);
                            schedules.add(schedule);

                        }
                        return schedules;
                    }
                }

            } catch (MalformedURLException e) {
                Log.e("DownloadData", "L'url non è formattato correttamente", e);
            } catch (IOException e) {
                Log.e("DownloadData", "Errore durante la connessione con il server", e);
            } catch (JSONException e) {
                Log.e("DownloadData", "Errore durante la deserializzazioen della risposta", e);
            } catch (ParseException e) {
                Log.e("DownloadData", "Errore durante la coneversione in formato Data", e);
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
                Log.d("DownloadData", "Errore nel download dei dati");

                new AlertDialog.Builder(mActivity)
                        .setCancelable(false)
                        .setTitle("Attenzione")
                        .setMessage("Non è stato possibile ottenere i dati dal server, controlla la tua connessione e riprova più tardi")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (listener != null) {
                                    listener.onFinishDownload();
                                }
                            }
                        })
                        .show();

            } else {
                Log.d("DownloadData", "Download completato correttamente");

                DbScheduleManager dbSchedule = new DbScheduleManager(mActivity);
                List<Schedule> localSchedules = dbSchedule.getSchedules(StationDirectory.getInstance().getStation().getMac());
                List<Schedule> toUpload = new ArrayList<>();
                for (Schedule localSchedule : localSchedules) {

                    Boolean addLocalToRemote = true;
                    for (Schedule remoteSchedule : remoteSchedules) {
                        if (localSchedule.getId().equals(remoteSchedule.getId())) {
                            // Remote match with Local
                            addLocalToRemote = false;
                            int compareLocalRemote = localSchedule.getmDateUpdate().compareTo(remoteSchedule.getmDateUpdate());
                            if (compareLocalRemote > 0) {
                                // Local date > Remote date
                                toUpload.add(localSchedule);
                            } else if (compareLocalRemote < 0) {
                                // Local date < Remote date
                                dbSchedule.updateSchedule(remoteSchedule);
                            }
                        }
                    }

                    if(addLocalToRemote){
                        toUpload.add(localSchedule);
                    }
                }

                // TODO upload schedules contained in the list toUpload

                if (listener != null) {
                    listener.onFinishDownload();
                }
            }
        }
    }
}
