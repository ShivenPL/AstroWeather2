package com.example.astroweather2;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class MyPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            ListPreference listPreference = (ListPreference)findPreference("listOfCities");

            FavouriteCities dbHelper = new FavouriteCities(getActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String querys = "SELECT * FROM tableFavouriteCities";
            Cursor cursor = db.rawQuery(querys, null);
            if(cursor.moveToLast())
            {
                CharSequence[] entries = {"Brak wybranego miasta", cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4) };
                CharSequence[] entryValues = {"", cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4) };
                listPreference.setEntries(entries);
                listPreference.setEntryValues(entryValues);
                listPreference.setDefaultValue("");
            }
        }
    }

}
