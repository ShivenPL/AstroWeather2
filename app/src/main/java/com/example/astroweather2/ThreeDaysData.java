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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class ThreeDaysData extends Fragment {

    TextView cityField, temperatureDayOne, temperatureDayTwo, temperatureDayThree, weatherIcon1, weatherIcon2, weatherIcon3, dayFirstDate, daySecondDate, dayThirdDate;
    ProgressBar loader;
    Typeface weatherFont;
    String city;
    String OPEN_WEATHER_MAP_API = "e14888cedb31aa303da59a843fe82e51";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.threedaysdata_layout, container, false);

        final SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipe);
        loader = rootView.findViewById(R.id.loader);
        cityField = rootView.findViewById(R.id.city_field);
        weatherFont = ResourcesCompat.getFont(getContext(), R.font.weathericons_regular_webfont);

        weatherIcon1 = rootView.findViewById(R.id.weather_icon1);
        weatherIcon1.setTypeface(weatherFont);
        weatherIcon2 = rootView.findViewById(R.id.weather_icon2);
        weatherIcon2.setTypeface(weatherFont);
        weatherIcon3 = rootView.findViewById(R.id.weather_icon3);
        weatherIcon3.setTypeface(weatherFont);

        temperatureDayOne = rootView.findViewById(R.id.dayfirst);
        temperatureDayTwo = rootView.findViewById(R.id.daysecond);
        temperatureDayThree = rootView.findViewById(R.id.daythird);

        dayFirstDate  = rootView.findViewById(R.id.dayfirstDate);
        daySecondDate = rootView.findViewById(R.id.daysecondDate);
        dayThirdDate = rootView.findViewById(R.id.daythirdDate);

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
            ThreeDaysData.DownloadWeather task = new ThreeDaysData.DownloadWeather();
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
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/forecast?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONArray list = json.getJSONArray("list");
                    JSONObject days;

                    String getDay, destDay;


                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    cityField.setText(json.getJSONObject("city").getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("city").getString("country"));

                   // updatedField.setText(df.format(new Date(json.getLong("dt") * 1000))); //dzień.miesiąc.rok hh:mm:ss

                    String temperatureChoice = sharedPreferences.getString("temperature", "C");

                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                    for(int i = 0; i < 40; i++) {
                        days = list.getJSONObject(i);
                        getDay = days.getString("dt_txt");
                        destDay = calendar.get(Calendar.YEAR) + "-0" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH) + 1) + " " + "12" + ":" + "00" + ":" + "00";
                        if(getDay.equals(destDay)) {
                            weatherIcon1.setText(Html.fromHtml(Function.setWeatherIcon(days.getJSONArray("weather").getJSONObject(0).getInt("id"),
                                    1560219842 * 1000,
                                    1560279571 * 1000)));
                            dayFirstDate.setText(days.getString("dt_txt").substring(0,10));
                            if(temperatureChoice.equals("C"))
                            {
                                temperatureDayOne.setText(String.format("%.2f", days.getJSONObject("main").getDouble("temp")) + "°C");
                            }
                            else
                            {
                                temperatureDayOne.setText(String.format("%.2f", days.getJSONObject("main").getDouble("temp") * 1.8 + 32) + "°F");
                            }
                        }
                        else if(days.getString("dt_txt").equals(calendar.get(Calendar.YEAR) + "-0" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH) + 2) + " " + "12" + ":" + "00" + ":" + "00")) {
                            weatherIcon2.setText(Html.fromHtml(Function.setWeatherIcon(days.getJSONArray("weather").getJSONObject(0).getInt("id"),
                                    1560219842 * 1000,
                                    1560279571 * 1000)));
                            daySecondDate.setText(days.getString("dt_txt").substring(0,10));
                            if(temperatureChoice.equals("C"))
                            {
                                temperatureDayTwo.setText(String.format("%.2f",days.getJSONObject("main").getDouble("temp")) + "°C");
                            }
                            else
                            {
                                temperatureDayTwo.setText(String.format("%.2f",days.getJSONObject("main").getDouble("temp") * 1.8 + 32) + "°F");
                            }
                        }
                        else if(days.getString("dt_txt").equals(calendar.get(Calendar.YEAR) + "-0" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH) + 3) + " " + "12" + ":" + "00" + ":" + "00")) {
                            weatherIcon3.setText(Html.fromHtml(Function.setWeatherIcon(days.getJSONArray("weather").getJSONObject(0).getInt("id"),
                                    1560219842 * 1000,
                                    1560279571 * 1000)));
                            dayThirdDate.setText(days.getString("dt_txt").substring(0,10));
                            if(temperatureChoice.equals("C"))
                            {
                                temperatureDayThree.setText(String.format("%.2f",days.getJSONObject("main").getDouble("temp")) + "°C");
                            }
                            else
                            {
                                temperatureDayThree.setText(String.format("%.2f", days.getJSONObject("main").getDouble("temp") * 1.8 + 32) + "°F");
                            }
                        }
                        else{}

                    }

                    loader.setVisibility(View.GONE);

                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Podano złe miasto!", Toast.LENGTH_SHORT).show();
            }


        }



    }
}
