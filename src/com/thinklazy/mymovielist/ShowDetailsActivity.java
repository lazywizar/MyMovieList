package com.thinklazy.mymovielist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShowDetailsActivity extends Activity {

    private static final String TAG = ShowDetailsActivity.class.getSimpleName();

    DatabaseHandler databaseH;
    String movieName;

    TextView movieNameView;
    TextView genre;
    TextView rating;
    TextView title;
    TextView seen;
    Button remove;
    Button removeFromWish;
    TextView home;

    @Override
    protected void onResume() {
	super.onResume();

	Drawable listActivityBackground = findViewById(R.id.detaillayout)
		.getBackground();
	listActivityBackground.setAlpha(50);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.show_movie_details);

	Intent intent = getIntent();
	movieName = intent.getStringExtra("movie");

	home = (TextView) findViewById(R.id.btnback);

	movieNameView = (TextView) findViewById(R.id.movie_name);
	genre = (TextView) findViewById(R.id.movie_genre);
	rating = (TextView) findViewById(R.id.movie_rating);

	seen = (TextView) findViewById(R.id.haveseen);

	remove = (Button) findViewById(R.id.btnremove);
	removeFromWish = (Button) findViewById(R.id.btnremovewish);

	try {

	    home.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    Intent i = new Intent(ShowDetailsActivity.this,
			    MainActivity.class);
		    startActivity(i);
		}
	    });

	    // instantiate database handler
	    databaseH = new DatabaseHandler(ShowDetailsActivity.this);
	    databaseH.getWritableDatabase();

	    Movie movie = databaseH.get(movieName);

	    if (movie != null) {
		movieNameView.setText(movie.name + "\n");

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

		if (!TextUtils.isEmpty(movie.seen) && movie.seen.contains("-")) {
		    seen.setText("You have seen this movie on " + movie.seen
			    + "\n");
		    remove.setEnabled(true);
		    remove.setBackgroundDrawable(getResources().getDrawable(
			    R.drawable.btn_removefrommymovies));

		} else {
		    seen.setText("\n");
		    remove.setEnabled(false);
		    remove.setBackgroundDrawable(getResources().getDrawable(
			    R.drawable.btn_pressedremovefrommymovies));

		}

		if (!TextUtils.isEmpty(movie.wishlist)
			&& movie.wishlist.contains("Y")) {
		    removeFromWish.setEnabled(true);
		    removeFromWish.setBackgroundDrawable(getResources()
			    .getDrawable(R.drawable.btn_removefromwishlist));

		} else {
		    removeFromWish.setEnabled(false);
		    removeFromWish.setBackgroundDrawable(getResources()
			    .getDrawable(
				    R.drawable.btn_pressedremovedfromwishlist));
		}

		remove.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			databaseH.markUnSeen(movieName);

			Toast toast = Toast.makeText(getApplicationContext(),
				"Removed from your movies list", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			remove.setEnabled(false);
			remove.setBackgroundDrawable(getResources()
				.getDrawable(
					R.drawable.btn_pressedremovefrommymovies));

		    }
		});

		removeFromWish.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			databaseH.removeFromWish(movieName);

			Toast toast = Toast.makeText(getApplicationContext(),
				"Removed from your Wishlist", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			removeFromWish.setEnabled(false);
			removeFromWish.setBackgroundDrawable(getResources()
				.getDrawable(
					R.drawable.btn_pressedremovedfromwishlist));

		    }
		});

	    } else {
		// Some thing majorly wrong happend
		Log.e(TAG, "Some thing wrong!!! [" + movieName + "]");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }
}
