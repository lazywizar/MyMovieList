package com.thinklazy.mymovielist;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
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
    TextView seen;
    Button add;
    TextView back;

    Button addToWish;
    String wishToastText;
    Boolean isWish;

    // adapter for auto-complete
    ArrayAdapter<String> myAdapter;

    // for database operations
    DatabaseHandler databaseH;
    private ProgressBar mProgress;
    private Boolean isPresent;
    private String searchMovie = "";

    // just to add some initial value
    String[] item = new String[] { "Please search..." };

    @Override
    protected void onResume() {
	super.onResume();

	Drawable listActivityBackground = findViewById(R.id.enterlayout)
		.getBackground();
	listActivityBackground.setAlpha(50);
	
	add.setEnabled(false);
	add.setBackgroundDrawable(getResources().getDrawable(
		R.drawable.btn_pressedjustwatched));

	addToWish.setEnabled(false);
	addToWish.setBackgroundDrawable(getResources().getDrawable(
		R.drawable.btn_pressedaddtowishlist));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.enter_movie);
	Intent intent = getIntent();
	String method = intent.getStringExtra("method");
	isPresent = false;

	// autocompletetextview is in activity_main.xml
	myAutoComplete = (CustomAutoCompleteView) findViewById(R.id.autocompleteMovies);
	cancel = (Button) findViewById(R.id.btncancel);
	ok = (Button) findViewById(R.id.btnOk);

	movieName = (TextView) findViewById(R.id.movie_name);
	genre = (TextView) findViewById(R.id.movie_genre);
	rating = (TextView) findViewById(R.id.movie_rating);
	seen = (TextView) findViewById(R.id.haveseen);
	back = (TextView) findViewById(R.id.btnback);
	add = (Button) findViewById(R.id.btnadd);
	addToWish = (Button) findViewById(R.id.btnaddtowish);
	seen.setText("");

	/*
	 * if(method.equalsIgnoreCase("wishlist")) { wishToastText =
	 * "Added to your Wishlist!!"; isWish = true;
	 * add.setText("Add to Wishlist"); } else { wishToastText =
	 * "Added to your list!!"; isWish = false; add.setText("Add");
	 * addToWish.setVisibility(0); }
	 */

	addToWish.setVisibility(0);
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

	    back.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    Intent i = new Intent(EnterMovieActivity.this,
			    MainActivity.class);
		    startActivity(i);
		}
	    });

	    ok.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    Log.d(TAG, "TEXT:[" + myAutoComplete.getText().toString()
			    + "]");
		    isPresent = false;
		    seen.setText("");
		    searchMovie = myAutoComplete.getText().toString();

		    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(
			    myAutoComplete.getWindowToken(), 0);

		    movieName.setText("Searching ...");

		    Movie movie = databaseH.get(myAutoComplete.getText()
			    .toString());

		    movieName.setText("");
		    genre.setText("");
		    rating.setText("");

		    if (movie != null) {
			isPresent = true;
			if (!TextUtils.isEmpty(movie.seen)
				&& movie.seen.contains("-")) {
			    seen.setText("You have seen this movie on "
				    + movie.seen + "\n");
			    add.setEnabled(false);
			    add.setBackgroundDrawable(getResources()
				    .getDrawable(
					    R.drawable.btn_pressedjustwatched));

			} else {
			    add.setEnabled(true);
			    add.setBackgroundDrawable(getResources()
				    .getDrawable(R.drawable.btnjustwatched));

			}
			addToWish.setEnabled(true);
			addToWish.setBackgroundDrawable(getResources()
				.getDrawable(R.drawable.btn_addtowishlist));

			movieName.setText(movie.name + "\n");
			if (movie.genre != null) {
			    genre.setText(movie.genre + "\n");
			} else {
			    genre.setText("-\n");
			}

			if (movie.rating != null) {
			    rating.setText("Rating : " + movie.rating);
			} else {
			    rating.setText("-");
			}
			addToWish.setVisibility(1);
			add.setVisibility(1);
		    } else {
			movieName.setText("Searching ...");

			// Movie not found in DB, try searching online
			MovieSearchHelper searchHelper = new MovieSearchHelper();
			JSONObject obj = null;

			if (movie == null) {
			    try {

				String json = searchHelper.execute(
					myAutoComplete.getText().toString())
					.get();

				if (!json.contains("Error")) {
				    obj = new JSONObject(json);
				    Log.d("My App", obj.toString());
				    if (obj == null) {
					movieName
						.setText("Oops! can't find this Movie, try entering it manually");
				    } else {
					movie = new Movie(obj
						.getString("Title")
						+ ", "
						+ obj.getString("Year"), "Out",
						obj.getString("Rated"), obj
							.getString("Genre"),
						"", "", obj.getString("imdbID"));

					isPresent = true;

					databaseH.addMovie(movie);
					if (!TextUtils.isEmpty(movie.seen)
						&& movie.seen.contains("-")) {
					    seen.setText("You have seen this movie on "
						    + movie.seen + "\n");
					    add.setEnabled(false);
					    add.setBackgroundDrawable(getResources()
						    .getDrawable(
							    R.drawable.btn_pressedjustwatched));

					} else {
					    add.setEnabled(true);
					    add.setBackgroundDrawable(getResources()
						    .getDrawable(
							    R.drawable.btnjustwatched));

					}
					addToWish.setEnabled(true);
					addToWish
						.setBackgroundDrawable(getResources()
							.getDrawable(
								R.drawable.btn_addtowishlist));

					movieName.setText(movie.name + "\n");
					genre.setText(movie.genre + "\n");
					rating.setText("Rating : "
						+ movie.rating);

					addToWish.setVisibility(1);
					add.setVisibility(1);
				    }

				} else {
				    movieName
					    .setText("Oops! can't find this Movie.\n Press Just Watched to enter \""
						    + myAutoComplete.getText()
							    .toString()
						    + "\" to your list");
				}
			    } catch (Throwable e) {
				// TODO Auto-generated catch block
				movieName
					.setText("Oops! can't find this Movie, try entering it manually");
				e.printStackTrace();

				isPresent = false;
			    }
			}

		    }

		}
	    });

	    add.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    if (isPresent) {
			databaseH.markSeen(movieName.getText().toString());
		    } else {
			Movie m = new Movie(searchMovie);
			m.seen = DatabaseHandler.getDateTime();
			databaseH.addMovie(m);
		    }
		    Toast toast = Toast.makeText(getApplicationContext(),
			    "Added to your list!!", Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.CENTER, 0, 0);
		    toast.show();
		    add.setEnabled(false);
		    add.setBackgroundDrawable(getResources().getDrawable(
			    R.drawable.btn_pressedjustwatched));

		}
	    });

	    addToWish.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    if (isPresent) {
			databaseH.addToWish(movieName.getText().toString());
		    } else {
			Movie m = new Movie(searchMovie);
			m.wishlist = "Y";
			databaseH.addMovie(m);
		    }

		    Toast toast = Toast.makeText(getApplicationContext(),
			    "Added to your Wishlist!!", Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.CENTER, 0, 0);
		    toast.show();
		    addToWish.setEnabled(false);
		    addToWish.setBackgroundDrawable(getResources().getDrawable(
			    R.drawable.btn_pressedaddtowishlist));

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