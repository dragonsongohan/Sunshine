package com.example.duynguyen.demoshunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by drago on 10/24/2015.
 */
public class ForecastFrament extends Fragment {

    public ForecastFrament() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        List<String> weekForecast = new ArrayList<String>();
        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast);

        View view = inflater.inflate(R.layout.frament_main, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String forecast = mForecastAdapter.getItem(position);
//
//                Toast toast = Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
//                toast.show();
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);

                //Demo intent filter
//                Intent test = new Intent();
//                test.setAction(Intent.ACTION_SEND);
//                test.putExtra(Intent.EXTRA_TEXT, "ok");
//                test.setType("text/plain");
//                String s = "Chon di...";
//                Intent choiser = Intent.createChooser(test, s);
//                if (test.resolveActivity(getActivity().getPackageManager()) != null) {
//                    startActivity(choiser);
//                }

                //Custom Toast
//                View toast = inflater.inflate(R.layout.layout_demo_toast, (ViewGroup) view.findViewById(R.id.toast_layout));
//                TextView textView = (TextView) toast.findViewById(R.id.textView);
//                textView.setText("This is my name");
//
//                Toast demo = new Toast(getActivity());
//                demo.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                demo.setDuration(Toast.LENGTH_LONG);
//                demo.setView(toast);
//                demo.show();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastframent, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            //If there's no zip code, there's nothing to look up,. Verify  size of parzms.
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            String forecastJsonStr = null;

            //Fake data default
            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                //Create the request to OpenWeatherMap, and open the connection
                Uri builtUri = Uri.parse(FORECAST_URL_BASE).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, String.valueOf(numDays))
                        .appendQueryParameter(APPID, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());
//                Log.e(LOGCAT, "Built URL : " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer strBuffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    strBuffer.append(line + "\n");
                }

                if (strBuffer.length() == 0) {
                    return null;
                }


                forecastJsonStr = strBuffer.toString();
//                Double max = getMaxTemperatureForDay(forecastJsonStr, 3);

                Log.e(LOGCAT, "Forecast JSON String: " + forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOGCAT, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(LOGCAT, "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOGCAT, "ERROR CLOSING STRAM", e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                mForecastAdapter.clear();
                mForecastAdapter.addAll(strings);
            }
        }

        private final String LOGCAT = FetchWeatherTask.class.getSimpleName();
        private final String FORECAST_URL_BASE = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        private final String QUERY_PARAM = "q";
        private final String FORMAT_PARAM = "mode";
        private final String UNITS_PARAM = "units";
        private final String DAYS_PARAM = "cnt";
        private final String APPID = "appid";

        //Ham lay max nhiet do trong ngay cu the
        private double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex) {
            try {
                JSONObject weather = new JSONObject(weatherJsonStr);
                JSONArray days = weather.getJSONArray("list");
                JSONObject dayInfo = days.getJSONObject(dayIndex);
                JSONObject temperature = dayInfo.getJSONObject("temp");
                return temperature.getDouble("max");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return -1;
        }

        //Ham format ngay thang nam
        private String getReadableDateString(long time) {
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        //Ham format nhiet do max va min thanh String
        private String formatHighLows(double high, double low, String unitType) {

            if (unitType.equals(getString(R.string.pref_units_imperial))) {
                high = (high * 1.8) + 32;
                low = (low * 1.8) +32;
            } else if (!unitType.equals(getString(R.string.pref_units_metric))) {
                Log.d(LOGCAT, "Unit type not found : " + unitType);
            }

            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);
            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        private String[] getWeatherDataFromJson(String forecastJSonStr, int numDays) throws JSONException {
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJSon = new JSONObject(forecastJSonStr);
            JSONArray weatherArray = forecastJSon.getJSONArray(OWM_LIST);

            Time dayTime = new Time();
            dayTime.setToNow();

            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for (int i = 0; i < weatherArray.length(); i++) {
                String day;
                String description;
                String highAndLow;

                JSONObject dayForecast = weatherArray.getJSONObject(i);
                long dateTime;

                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                //get value in settings
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String units = preferences.getString(getString(R.string.pref_units_key),
                        getString(R.string.pref_units_metric));

                highAndLow = formatHighLows(high, low, units);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            //Log xem ket qua
//            for (String s : resultStrs) {
//                Log.e(LOGCAT, "Forecast entry : " + s);
//            }

            return resultStrs;
        }
    }

    private ArrayAdapter<String> mForecastAdapter;

    private void updateWeather() {
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        fetchWeatherTask.execute(location);
    }
}
