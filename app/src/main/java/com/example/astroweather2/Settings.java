package com.example.astroweather2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


public class Settings extends AppCompatActivity {

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Ustawienia");


        if(findViewById(R.id.fragmentConteiner) != null)
        {
            if(savedInstanceState != null)
            {
                return;
            }
            getFragmentManager().beginTransaction().add(R.id.fragmentConteiner, new MyPreferencesActivity.MyPreferenceFragment()).commit();
        }

    }

}
