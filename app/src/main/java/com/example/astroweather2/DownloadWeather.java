package com.example.astroweather2;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Locale;

public class DownloadWeather extends AsyncTask< String, Void, String > {
    private static DownloadWeather instance = null;
    String city, detail, currentTemperature, pressure, updated, weatherIcon, windSpeed, windDeg, humidity, visible;
    String OPEN_WEATHER_MAP_API = "e14888cedb31aa303da59a843fe82e51";
    private DownloadWeather(){
        city = "";
        detail = "";
        currentTemperature = "";
        pressure = "";
        updated = "";
        weatherIcon = "";
        windSpeed = "";
        windDeg = "";
        humidity = "";
        visible = "";
    }

    public static DownloadWeather getInstance() {
        if (instance == null)
            instance = new DownloadWeather();
        return  instance;
    }

    public DownloadWeather getClone() {
        DownloadWeather downloadWeather = new DownloadWeather();
        downloadWeather.city = city;
        downloadWeather.detail = detail;
        downloadWeather.currentTemperature = currentTemperature;
        downloadWeather.pressure = pressure;
        downloadWeather.updated = updated;
        downloadWeather.weatherIcon = weatherIcon;
        downloadWeather.windSpeed = windSpeed;
        downloadWeather.windDeg = windDeg;
        downloadWeather.humidity = humidity;
        downloadWeather.visible = visible;
        return downloadWeather;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


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
                JSONObject wind = json.getJSONObject("wind");
                JSONObject clouds = json.getJSONObject("clouds");
                DateFormat df = DateFormat.getDateTimeInstance();


                city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
                detail = details.getString("description").toUpperCase(Locale.US);
                currentTemperature = main.getDouble("temp") + "";
                pressure = main.getString("pressure");

                updated = df.format(new Date(json.getLong("dt") * 1000));
                weatherIcon = (Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                        json.getJSONObject("sys").getLong("sunrise") * 1000,
                        json.getJSONObject("sys").getLong("sunset") * 1000))).toString();

                windSpeed = wind.getString("speed");
                windDeg = wind.getString("deg");
                humidity =  main.getString("humidity");
                visible = clouds.getString("all");
                instance = instance.getClone();
            }
        } catch (JSONException e) {
            Log.println(1, "df", e.getMessage());
        }

    }


    public String getCity() {
        return city;
    }

    public String getOPEN_WEATHER_MAP_API() {
        return OPEN_WEATHER_MAP_API;
    }

    public String getCurrentTemperature() {
        return currentTemperature;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getDetail() {
        return detail;
    }

    public String getPressure() {
        return pressure;
    }

    public String getUpdated() {
        return updated;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWindDeg() {
        return windDeg;
    }

    public String getVisible() {
        return visible;
    }
}
