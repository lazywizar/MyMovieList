package com.thinklazy.mymovielist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    TextView mEnterMovie;
    TextView mMyMovie;
    TextView mAddToWish;
    TextView mMyWish;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String DB_CREATED = "db_created";
    public static final String DB_LOAD_PERCENT = "db_load_percent";

    private DatabaseHandler dbHandler;
    private SharedPreferences sharedpreferences;

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
	if (exit) {
	    finish(); // finish activity
	} else {
	    Toast.makeText(this, "Press Back again to Exit.",
		    Toast.LENGTH_SHORT).show();
	    exit = true;
	    new Handler().postDelayed(new Runnable() {
		@Override
		public void run() {
		    exit = false;
		}
	    }, 3 * 1000);

	}
    }

    @Override
    protected void onResume() {
	super.onResume();

	Drawable listActivityBackground = findViewById(R.id.RelativeLayout1)
		.getBackground();
	listActivityBackground.setAlpha(1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	sharedpreferences = getSharedPreferences(MyPREFERENCES,
		this.MODE_PRIVATE);
	mAddToWish = (TextView) findViewById(R.id.addtowish);
	mAddToWish.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (!InitializeDbHelper.isCompleteLoading()
			&& !sharedpreferences.contains(DB_CREATED)) {
		    // myAutoComplete.setVisibility(0);
		    Toast toast = Toast
			    .makeText(
				    getApplicationContext(),
				    "Loading awesome movies.. "
					    + InitializeDbHelper
						    .getLoadPercent()
					    + "% \nPlease hold on \n(Don't worry, this happens only on first start!)",
				    Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.CENTER, 0, 0);
		    toast.show();
		} else {
		    Intent intent = new Intent(MainActivity.this,
			    EnterMovieActivity.class);
		    intent.putExtra("method", "wishlist");
		    startActivity(intent);
		}
	    }
	});

	mMyWish = (TextView) findViewById(R.id.mywish);
	mMyWish.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		if (!InitializeDbHelper.isCompleteLoading()
			&& !sharedpreferences.contains(DB_CREATED)) {
		    // myAutoComplete.setVisibility(0);
		    Toast toast = Toast
			    .makeText(
				    getApplicationContext(),
				    "Loading awesome movies.. "
					    + InitializeDbHelper
						    .getLoadPercent()
					    + "% \nPlease hold on \n(Don't worry, this happens only on first start!)",
				    Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.CENTER, 0, 0);
		    toast.show();
		} else {
		    Intent i = new Intent(MainActivity.this,
			    MoviesListActivity.class);
		    i.putExtra("method", "wishlist");
		    startActivity(i);
		}
	    }
	});

	mEnterMovie = (TextView) findViewById(R.id.enterMoviewBtn);
	mEnterMovie.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (!InitializeDbHelper.isCompleteLoading()
			&& !sharedpreferences.contains(DB_CREATED)) {
		    // myAutoComplete.setVisibility(0);
		    Toast toast = Toast
			    .makeText(
				    getApplicationContext(),
				    "Loading awesome movies.. "
					    + InitializeDbHelper
						    .getLoadPercent()
					    + "% \nPlease hold on \n(Don't worry, this happens only on first start!)",
				    Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.CENTER, 0, 0);
		    toast.show();
		} else {
		    Intent i = new Intent(MainActivity.this,
			    EnterMovieActivity.class);
		    i.putExtra("method", "add");
		    startActivity(i);
		}
	    }
	});

	mMyMovie = (TextView) findViewById(R.id.mymoviesBtn);
	mMyMovie.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		if (!InitializeDbHelper.isCompleteLoading()
			&& !sharedpreferences.contains(DB_CREATED)) {
		    // myAutoComplete.setVisibility(0);
		    Toast toast = Toast
			    .makeText(
				    getApplicationContext(),
				    "Loading awesome movies.. "
					    + InitializeDbHelper
						    .getLoadPercent()
					    + "% \nPlease hold on \n(Don't worry, this happens only on first start!)",
				    Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.CENTER, 0, 0);
		    toast.show();
		} else {
		    Intent i = new Intent(MainActivity.this,
			    MoviesListActivity.class);
		    i.putExtra("method", "mymovies");
		    startActivity(i);
		}
	    }
	});

	if (!sharedpreferences.contains(DB_CREATED)) {
	    // instantiate database handler
	    dbHandler = new DatabaseHandler(MainActivity.this);
	    dbHandler.getWritableDatabase();

	    InitializeDbHelper task = new InitializeDbHelper();
	    task.execute(new Object[] { dbHandler, this });
	}
    }
}
