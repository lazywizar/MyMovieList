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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyMoviesActivity extends Activity {
    // for database operations
    DatabaseHandler databaseH;
    TextView listTitle;
    
    @Override
    protected void onResume() {
	super.onResume();
	
	Drawable listActivityBackground = findViewById(R.id.list_layout).getBackground();
	listActivityBackground.setAlpha(35);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.listviewmymovies);

	Intent intent =  getIntent();
	String method = intent.getStringExtra("method");
	
	final ListView listview = (ListView) findViewById(R.id.listview);

	listTitle = (TextView) findViewById(R.id.listtitle);
	
	databaseH = new DatabaseHandler(MyMoviesActivity.this);
	databaseH.getWritableDatabase();

	List<Movie> movies ;
	if(method.equalsIgnoreCase("wishlist")) {
	    movies = databaseH.getWishList();
	    listTitle.setText(R.string.mywish);
	} else {
	    movies = databaseH.getMyMovies();
	    listTitle.setText(R.string.mymovies);	
	}
	final ArrayList<String> list = new ArrayList<String>();
	
	for(Movie movie : movies) {
	    list.add(movie.name);
	}

	final StableArrayAdapter adapter = new StableArrayAdapter(this,
		android.R.layout.simple_list_item_1, list);
	listview.setAdapter(adapter);
	
	/*
	listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		@SuppressLint("NewApi") @Override
		public void onItemClick(AdapterView<?> parent, final View view,
				int position, long id) {
			final String item = (String) parent.getItemAtPosition(position);
			view.animate().setDuration(2000).alpha(0)
					.withEndAction(new Runnable() {
						@Override
						public void run() {
							list.remove(item);
							adapter.notifyDataSetChanged();
							view.setAlpha(1);
						}
					});
		}
	});
	*/
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
