package com.example.astroweather2;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.Double.parseDouble;


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
        if(sharedPreferences.getString("city", "Lodz,PL") != "" && sharedPreferences.getString("listOfCities", "") == "")
        {
            city = sharedPreferences.getString("city", "Lodz,PL");
            //sharedPreferences.edit().putString("listOfCities", "").commit();
        }
        else if(sharedPreferences.getString("listOfCities", "") != "" && sharedPreferences.getString("city", "Lodz,PL") == "")
        {
            city = sharedPreferences.getString("listOfCities", "");
            //sharedPreferences.edit().putString("city", "").commit();
        }
        else if(sharedPreferences.getString("listOfCities", "") != "" && sharedPreferences.getString("city", "Lodz,PL") != "")
        {
            city = sharedPreferences.getString("listOfCities", "");
            sharedPreferences.edit().putString("city", "").commit();
        }
        else
            city = "";
        DataBaseThreeDays dbHelper = new DataBaseThreeDays(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String querys = "SELECT * FROM tableThreeDays";
        Cursor cursor = db.rawQuery(querys, null);
        if(cursor.moveToLast())
        {
            onstart();
        }
        else
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
                        if(sharedPreferences.getString("city", "Lodz,PL") != "" && sharedPreferences.getString("listOfCities", "") == "")
                        {
                            city = sharedPreferences.getString("city", "Lodz,PL");
                            //sharedPreferences.edit().putString("listOfCities", "").commit();
                        }
                        else if(sharedPreferences.getString("listOfCities", "") != "" && sharedPreferences.getString("city", "Lodz,PL") == "")
                        {
                            city = sharedPreferences.getString("listOfCities", "");
                            //sharedPreferences.edit().putString("city", "").commit();
                        }
                        else if(sharedPreferences.getString("listOfCities", "") != "" && sharedPreferences.getString("city", "Lodz,PL") != "")
                        {
                            city = sharedPreferences.getString("listOfCities", "");
                            sharedPreferences.edit().putString("city", "").commit();
                        }
                        else
                            city = "";
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
        String city2;
        if(sharedPreferences.getString("city", "Lodz,PL") != "" && sharedPreferences.getString("listOfCities", "") == "")
        {
            city2 = sharedPreferences.getString("city", "Lodz,PL");
            //sharedPreferences.edit().putString("listOfCities", "").commit();
        }
        else if(sharedPreferences.getString("listOfCities", "") != "" && sharedPreferences.getString("city", "Lodz,PL") == "")
        {
            city2 = sharedPreferences.getString("listOfCities", "");
            //sharedPreferences.edit().putString("city", "").commit();
        }
        else if(sharedPreferences.getString("listOfCities", "") != "" && sharedPreferences.getString("city", "Lodz,PL") != "")
        {
            city2 = sharedPreferences.getString("listOfCities", "");
            sharedPreferences.edit().putString("city", "").commit();
        }
        else
            city2 = "";
        if(city.equals(city2)){}
        else {taskLoadUp(city2); city = city2;}
        super.onResume();
    }

    public void onstart() {
        DataBaseThreeDays dbHelper = new DataBaseThreeDays(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String querys = "SELECT * FROM tableThreeDays";
        Cursor cursor = db.rawQuery(querys, null);

        if(cursor.moveToLast())
        {
            String getDay, destDay;

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            cityField.setText(cursor.getString(1));

            String temperatureChoice = sharedPreferences.getString("temperature", "C");


            dayFirstDate.setText(cursor.getString(2));
            weatherIcon1.setText(cursor.getString(3));
            if(temperatureChoice.equals("C"))
            {
                temperatureDayOne.setText(String.format("%.2f", parseDouble(cursor.getString(4))) + "°C");
            }
            else
            {
                temperatureDayOne.setText(String.format("%.2f", parseDouble(cursor.getString(4)) * 1.8 + 32) + "°F");
            }

            daySecondDate.setText(cursor.getString(5));
            weatherIcon2.setText(cursor.getString(6));
            if(temperatureChoice.equals("C"))
            {
                temperatureDayTwo.setText(String.format("%.2f", parseDouble(cursor.getString(7))) + "°C");
            }
            else
            {
                temperatureDayTwo.setText(String.format("%.2f", parseDouble(cursor.getString(7)) * 1.8 + 32) + "°F");
            }

            dayThirdDate.setText( cursor.getString(8));
            weatherIcon3.setText( cursor.getString(9));
            if(temperatureChoice.equals("C"))
            {
                temperatureDayThree.setText(String.format("%.2f", parseDouble(cursor.getString(10))) + "°C");
            }
            else
            {
                temperatureDayThree.setText(String.format("%.2f", parseDouble(cursor.getString(10)) * 1.8 + 32) + "°F");
            }

        }
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

                    DataBaseThreeDays dbHelper = new DataBaseThreeDays(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();


                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    cityField.setText(json.getJSONObject("city").getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("city").getString("country"));
                    values.put("cities", json.getJSONObject("city").getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("city").getString("country"));

                    String temperatureChoice = sharedPreferences.getString("temperature", "C");

                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                    for(int i = 0; i < 40; i++) {
                        days = list.getJSONObject(i);
                        getDay = days.getString("dt_txt");
                        destDay = calendar.get(Calendar.YEAR) + "-0" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH) + 1) + " " + "15" + ":" + "00" + ":" + "00";
                        if(getDay.equals(destDay)) {
                            weatherIcon1.setText(Html.fromHtml(Function.setWeatherIconThreeDays(days.getJSONArray("weather").getJSONObject(0).getInt("id"),
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
                            values.put("dayDateOne", days.getString("dt_txt").substring(0,10));
                            values.put("weatherIOne", (Html.fromHtml(Function.setWeatherIconThreeDays(days.getJSONArray("weather").getJSONObject(0).getInt("id"),
                                    1560219842 * 1000,
                                    1560279571 * 1000))).toString());
                            values.put("temperatureOne", days.getJSONObject("main").getDouble("temp"));
                        }
                        else if(days.getString("dt_txt").equals(calendar.get(Calendar.YEAR) + "-0" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH) + 2) + " " + "15" + ":" + "00" + ":" + "00")) {
                            weatherIcon2.setText(Html.fromHtml(Function.setWeatherIconThreeDays(days.getJSONArray("weather").getJSONObject(0).getInt("id"),
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
                            values.put("dayDateTwo", days.getString("dt_txt").substring(0,10));
                            values.put("weatherITwo", (Html.fromHtml(Function.setWeatherIconThreeDays(days.getJSONArray("weather").getJSONObject(0).getInt("id"),
                                    1560219842 * 1000,
                                    1560279571 * 1000))).toString());
                            values.put("temperatureTwo", days.getJSONObject("main").getDouble("temp"));
                        }
                        else if(days.getString("dt_txt").equals(calendar.get(Calendar.YEAR) + "-0" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH) + 3) + " " + "15" + ":" + "00" + ":" + "00")) {
                            weatherIcon3.setText(Html.fromHtml(Function.setWeatherIconThreeDays(days.getJSONArray("weather").getJSONObject(0).getInt("id"),
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
                            values.put("dayDateThree", days.getString("dt_txt").substring(0,10));
                            values.put("weatherIThree", (Html.fromHtml(Function.setWeatherIconThreeDays(days.getJSONArray("weather").getJSONObject(0).getInt("id"),
                                    1560219842 * 1000,
                                    1560279571 * 1000))).toString());
                            values.put("temperatureThree", days.getJSONObject("main").getDouble("temp"));
                        }
                        else{}

                    }

                    long newRowId = db.insert("tableThreeDays", null, values);

                    if(newRowId == -1)
                    {}



                    loader.setVisibility(View.GONE);

                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Podano złe miasto!", Toast.LENGTH_SHORT).show();
            }


        }



    }
}


class DataBaseThreeDays extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "tableThreeDays";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, cities TEXT, dayDateOne TEXT, weatherIOne TEXT, temperatureOne TEXT, dayDateTwo TEXT, weatherITwo TEXT, temperatureTwo TEXT, dayDateThree TEXT, weatherIThree TEXT, temperatureThree TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public DataBaseThreeDays(Context context) {
        super(context, TABLE_NAME, null, 1);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Cursor getItem(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM tableThreeDays";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}