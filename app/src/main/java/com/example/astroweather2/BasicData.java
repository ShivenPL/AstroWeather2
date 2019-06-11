package com.example.astroweather2;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Locale;

import static java.lang.Integer.parseInt;

public class BasicData extends Fragment {

    TextView cityField, detailsField, currentTemperatureField, pressure_field, weatherIcon, updatedField;
    ProgressBar loader;
    Typeface weatherFont;
    String city;
    String OPEN_WEATHER_MAP_API = "e14888cedb31aa303da59a843fe82e51";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.basicdata_layout, container, false);

        final SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipe);
        loader = rootView.findViewById(R.id.loader);
        cityField = rootView.findViewById(R.id.city_field);
        updatedField = rootView.findViewById(R.id.updated_field);
        detailsField = rootView.findViewById(R.id.details_field);
        currentTemperatureField = rootView.findViewById(R.id.current_temperature_field);
        pressure_field = rootView.findViewById(R.id.pressure_field);
        weatherIcon = rootView.findViewById(R.id.weather_icon);
        weatherFont = ResourcesCompat.getFont(getContext(), R.font.weathericons_regular_webfont);
        weatherIcon.setTypeface(weatherFont);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        city = sharedPreferences.getString("city", "Lodz");
        city = city + ", PL";
        taskLoadUp(city);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        city = sharedPreferences.getString("city", "Lodz");
                        city = city + ", PL";
                        taskLoadUp(city);
                    }
                }, 1500);

            }
        });



        return rootView;
    }

    @Override
    public void onResume() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String city2 = sharedPreferences.getString("city", "Lodz") + ", PL";
        if(city.equals(city2)){}
        else {taskLoadUp(city2); city = city2;}
        super.onResume();
    }

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getActivity())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }



    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);

        }
        protected String doInBackground(String...args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


                    cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(details.getString("description").toUpperCase(Locale.US));
                    String temperatureChoice = sharedPreferences.getString("temperature", "C");
                    if(temperatureChoice.equals("C"))
                        currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + "°C");
                    else
                        currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp") * 1.8 + 32) + "°F");
                    String pressureChoice = sharedPreferences.getString("pressure", "hpa");
                    if(pressureChoice.equals("hpa"))
                        pressure_field.setText("Ciśnienie: " + main.getString("pressure") + " hPa");
                    else
                        pressure_field.setText("Ciśnienie: " + String.format("%.2f",(parseInt(main.getString("pressure")) / 33.86)) + " in. Hg");
                    updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));

                    loader.setVisibility(View.GONE);

                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Podano złe miasto!", Toast.LENGTH_SHORT).show();
            }


        }



    }
}
