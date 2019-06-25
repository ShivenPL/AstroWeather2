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

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Double.parseDouble;
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
        DataBaseBasic dbHelper = new DataBaseBasic(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String querys = "SELECT * FROM tableBasic";
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
        if(city.equals(city2)){
            DataBaseBasic dbHelper = new DataBaseBasic(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String querys = "SELECT * FROM tableBasic";
            Cursor cursor = db.rawQuery(querys, null);

            if(cursor.moveToLast())
            {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                cityField.setText(cursor.getString(1));
                detailsField.setText(cursor.getString(2));
                String temperatureChoice = sharedPreferences.getString("temperature", "C");
                if(temperatureChoice.equals("C"))
                    currentTemperatureField.setText(String.format("%.2f", parseDouble(cursor.getString(3))) + "°C");
                else
                    currentTemperatureField.setText(String.format("%.2f", parseDouble(cursor.getString(3)) * 1.8 + 32) + "°F");
                String pressureChoice = sharedPreferences.getString("pressure", "hpa");
                if(pressureChoice.equals("hpa"))
                    pressure_field.setText("Ciśnienie: " + cursor.getString(4) + " hPa");
                else
                    pressure_field.setText("Ciśnienie: " + String.format("%.2f",(parseInt(cursor.getString(4)) / 33.86)) + " in. Hg");
                updatedField.setText(cursor.getString(5));
                weatherIcon.setText(cursor.getString(6));
            }
        }
        else {taskLoadUp(city2); city = city2;}
        super.onResume();
    }

    public void onstart() {
        DataBaseBasic dbHelper = new DataBaseBasic(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String querys = "SELECT * FROM tableBasic";
        Cursor cursor = db.rawQuery(querys, null);

        if(cursor.moveToLast())
        {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            cityField.setText(cursor.getString(1));
            detailsField.setText(cursor.getString(2));
            String temperatureChoice = sharedPreferences.getString("temperature", "C");
            if(temperatureChoice.equals("C"))
                currentTemperatureField.setText(String.format("%.2f", parseDouble(cursor.getString(3))) + "°C");
            else
                currentTemperatureField.setText(String.format("%.2f", parseDouble(cursor.getString(3)) * 1.8 + 32) + "°F");
            String pressureChoice = sharedPreferences.getString("pressure", "hpa");
            if(pressureChoice.equals("hpa"))
                pressure_field.setText("Ciśnienie: " + cursor.getString(4) + " hPa");
            else
                pressure_field.setText("Ciśnienie: " + String.format("%.2f",(parseInt(cursor.getString(4)) / 33.86)) + " in. Hg");
            updatedField.setText(cursor.getString(5));
            weatherIcon.setText(cursor.getString(6));
        }
    }

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getActivity())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            onstart();
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

                    DataBaseBasic dbHelper = new DataBaseBasic(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    values.put("cities", json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    values.put("details", details.getString("description").toUpperCase(Locale.US));
                    values.put("temperature", main.getDouble("temp"));
                    values.put("pressure", main.getString("pressure"));
                    values.put("updateF", df.format(new Date(json.getLong("dt") * 1000)));
                    values.put("weatherI", (Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000))).toString());
                    long newRowId = db.insert("tableBasic", null, values);

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

class DataBaseBasic extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "tableBasic";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, cities TEXT, details TEXT, temperature TEXT, pressure TEXT, updateF TEXT, weatherI TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public DataBaseBasic(Context context) {
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
        String query = "SELECT * FROM tableBasic";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}

