package com.thinklazy.mymovielist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MoviesListActivity extends Activity {
    // for database operations
    DatabaseHandler databaseH;
    TextView listTitle;
    TextView home;

    ListView listview;

    String method;

    @Override
    protected void onResume() {
	super.onResume();

	Drawable listActivityBackground = findViewById(R.id.list_layout)
		.getBackground();
	listActivityBackground.setAlpha(35);

	databaseH = new DatabaseHandler(MoviesListActivity.this);
	databaseH.getWritableDatabase();

	home = (TextView) findViewById(R.id.btnback);
	home.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Intent i = new Intent(MoviesListActivity.this,
			MainActivity.class);
		startActivity(i);
	    }
	});

	List<Movie> movies;
	if (method.equalsIgnoreCase("wishlist")) {
	    movies = databaseH.getWishList();
	    listTitle.setText(R.string.mywish);
	} else {
	    movies = databaseH.getMyMovies();
	    listTitle.setText(R.string.mymovies);
	}

	final ArrayList<String> list = new ArrayList<String>();
	for (Movie movie : movies) {
	    list.add(movie.name);
	}

	final StableArrayAdapter adapter = new StableArrayAdapter(this,
		android.R.layout.simple_list_item_1, list);
	listview.setAdapter(adapter);

	listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	    @SuppressLint("NewApi")
	    @Override
	    public void onItemClick(AdapterView<?> parent, final View view,
		    int position, long id) {
		final String movie = (String) parent
			.getItemAtPosition(position);

		Intent i = new Intent(MoviesListActivity.this,
			ShowDetailsActivity.class);
		i.putExtra("movie", movie);
		startActivity(i);
	    }
	});

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.listviewmymovies);

	Intent intent = getIntent();
	method = intent.getStringExtra("method");

	listTitle = (TextView) findViewById(R.id.listtitle);
	listview = (ListView) findViewById(R.id.listview);
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

	HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	public StableArrayAdapter(Context context, int textViewResourceId,
		List<String> objects) {
	    super(context, textViewResourceId, objects);
	    for (int i = 0; i < objects.size(); ++i) {
		mIdMap.put(objects.get(i), i);
	    }
	}

	@Override
	public long getItemId(int position) {
	    String item = getItem(position);
	    return mIdMap.get(item);
	}

	@Override
	public boolean hasStableIds() {
	    return true;
	}

    }
}
