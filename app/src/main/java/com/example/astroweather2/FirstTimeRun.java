package com.example.astroweather2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirstTimeRun extends AppCompatActivity {

    private EditText cityOne, cityTwo, cityThree, cityFour;
    private Button potwierdz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firsttimerun_layout);

        final FavouriteCities dbHelper = new FavouriteCities(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();

        cityOne = findViewById(R.id.cityOne);
        cityTwo = findViewById(R.id.cityTwo);
        cityThree = findViewById(R.id.cityThree);
        cityFour = findViewById(R.id.cityFour);
        potwierdz = findViewById(R.id.buttonOk);

        potwierdz.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                values.put("cityOne", cityOne.getText().toString());
                values.put("cityTwo", cityTwo.getText().toString());
                values.put("cityThree", cityThree.getText().toString());
                values.put("cityFour", cityFour.getText().toString());

                long newRowId = db.insert("tableFavouriteCities", null, values);

                if(newRowId == -1)
                {}
                else
                    Toast.makeText(FirstTimeRun.this,"Dodano ulubione miasta!" ,Toast.LENGTH_SHORT).show();
            }
        });


    }
}

class FavouriteCities extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "tableFavouriteCities";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, cityOne, cityTwo, cityThree, cityFour)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public FavouriteCities(Context context) {
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
        String query = "SELECT * FROM tableFavouriteCities";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}