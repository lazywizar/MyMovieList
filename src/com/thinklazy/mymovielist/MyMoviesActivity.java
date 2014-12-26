package com.thinklazy.mymovielist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyMoviesActivity extends Activity {
    // for database operations
    DatabaseHandler databaseH;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.listviewmymovies);

	Intent intent =  getIntent();
	String method = intent.getStringExtra("method");
	
	final ListView listview = (ListView) findViewById(R.id.listview);

	databaseH = new DatabaseHandler(MyMoviesActivity.this);
	databaseH.getWritableDatabase();

	List<Movie> movies ;
	if(method.equalsIgnoreCase("wishlist")) {
	    movies = databaseH.getWishList();
	} else {
	    movies = databaseH.getMyMovies();
	}
	final ArrayList<String> list = new ArrayList<String>();
	
	for(Movie movie : movies) {
	    list.add(movie.name);
	}

	final StableArrayAdapter adapter = new StableArrayAdapter(this,
		android.R.layout.simple_list_item_1, list);
	listview.setAdapter(adapter);
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
