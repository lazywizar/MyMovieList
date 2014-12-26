package com.thinklazy.mymovielist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    TextView mEnterMovie;
    TextView mMyMovie;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String DB_CREATED = "db_created";
    public static final String DB_LOAD_PERCENT = "db_load_percent";

    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	mEnterMovie = (TextView) findViewById(R.id.enterMoviewBtn);
	mEnterMovie.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (!InitializeDbHelper.isCompleteLoading()) {
		    // myAutoComplete.setVisibility(0);
		    Toast toast = Toast.makeText(getApplicationContext(), 
			    "Loading awesome movies.. " + InitializeDbHelper.getLoadPercent() + "% \nPlease hold on \n(Don't worry, this happens only on first start!)", 
			    Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.CENTER, 0, 0);
		    toast.show();
		} else {
		    Intent i = new Intent(MainActivity.this,
			    EnterMovieActivity.class);
		    startActivity(i);
		}
	    }
	});

	mMyMovie = (TextView) findViewById(R.id.mymoviesBtn);
	mMyMovie.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		Intent i = new Intent(MainActivity.this,
			MyMoviesActivity.class);
		    startActivity(i);
	    }
	});
	
	SharedPreferences sharedpreferences = getSharedPreferences(
		MyPREFERENCES, this.MODE_PRIVATE);
	if (!sharedpreferences.contains(DB_CREATED)) {
	    // instantiate database handler

	    /*
	     * PrelodedDbHelper myDbHelper = new PrelodedDbHelper(this); try {
	     * myDbHelper.createDataBase(); } catch (IOException ioe) { throw
	     * new Error("Unable to create database"); } try {
	     * myDbHelper.openDataBase(); } catch (SQLException sqle) { throw
	     * sqle; }
	     */
	    dbHandler = new DatabaseHandler(MainActivity.this);
	    dbHandler.getWritableDatabase();

	    InitializeDbHelper task = new InitializeDbHelper();
	    task.execute(new Object[] { dbHandler, this });
	}
    }
}
