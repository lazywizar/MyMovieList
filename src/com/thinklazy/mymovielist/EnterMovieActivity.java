package com.thinklazy.mymovielist;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EnterMovieActivity extends Activity {
    private String TAG = EnterMovieActivity.class.getSimpleName();

    /*
     * Change to type CustomAutoCompleteView instead of AutoCompleteTextView
     * since we are extending to customize the view and disable filter The same
     * with the XML view, type will be CustomAutoCompleteView
     */
    CustomAutoCompleteView myAutoComplete;
    Button cancel;
    Button ok;
    Context context;
    TextView movieName;
    TextView genre;
    TextView rating;
    TextView title;
    Button add;
    String wishToastText;
    Boolean isWish;
    
    // adapter for auto-complete
    ArrayAdapter<String> myAdapter;

    // for database operations
    DatabaseHandler databaseH;
    private ProgressBar mProgress;

    // just to add some initial value
    String[] item = new String[] { "Please search..." };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.enter_movie);
	Intent intent = getIntent();
	String method = intent.getStringExtra("method");
	
	// autocompletetextview is in activity_main.xml
	myAutoComplete = (CustomAutoCompleteView) findViewById(R.id.autocompleteMovies);
	cancel = (Button) findViewById(R.id.btncancel);
	ok = (Button) findViewById(R.id.btnOk);

	movieName = (TextView) findViewById(R.id.movie_name);
	genre = (TextView) findViewById(R.id.movie_genre);
	rating = (TextView) findViewById(R.id.movie_rating);
	
	add = (Button) findViewById(R.id.btnadd);
	if(method.equalsIgnoreCase("wishlist")) {
	    wishToastText = "Added to your Wishlist!!";
	    isWish = true;
	    add.setText("Add to Wishlist");
	} else {
	    wishToastText = "Added to your list!!";
	    isWish = false;
	    add.setText("Add");
	}
	add.setVisibility(0);
	title = (TextView) findViewById(R.id.txtname);
	context = this;

	try {

	    // instantiate database handler
	    databaseH = new DatabaseHandler(EnterMovieActivity.this);
	    databaseH.getWritableDatabase();

	    // add the listener so it will tries to suggest while the user types
	    myAutoComplete
		    .addTextChangedListener(new CustomAutoCompleteTextChangedListener(
			    this));

	    // set our adapter
	    myAdapter = new ArrayAdapter<String>(this,
		    android.R.layout.simple_dropdown_item_1line, item);
	    myAutoComplete.setAdapter(myAdapter);

	    cancel.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    myAutoComplete.setText("");
		}
	    });

	    ok.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    Log.d(TAG, "TEXT:[" + myAutoComplete.getText().toString());
		    Movie movie = databaseH.get(myAutoComplete.getText()
			    .toString());

		    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(
			    myAutoComplete.getWindowToken(), 0);

		    if (movie != null) {
			movieName.setText(movie.name);
			genre.setText(movie.genre);
			rating.setText(movie.rating);
			add.setVisibility(1);
		    }
		    add.setEnabled(true);
		}
	    });

	    add.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    if(isWish) {
			databaseH.addToWish(movieName.getText().toString());
		    } else {
			databaseH.markRead(movieName.getText().toString());
		    }
		    Toast toast = Toast.makeText(getApplicationContext(), 
			    wishToastText, 
			    Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.CENTER, 0, 0);
		    toast.show();
		    add.setEnabled(false);
		}
	    });

	} catch (NullPointerException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    // this function is used in CustomAutoCompleteTextChangedListener.java
    public String[] getItemsFromDb(String searchTerm) {

	// add items on the array dynamically
	List<Movie> products = databaseH.read(searchTerm);

	for (Movie m : products) {
	    Log.d(TAG, "Items: " + m.name);
	}

	int rowCount = products.size();

	String[] item = new String[rowCount];
	int x = 0;

	for (Movie record : products) {
	    item[x] = record.name;
	    x++;
	}

	return item;
    }

}