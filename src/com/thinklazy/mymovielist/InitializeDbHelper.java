package com.thinklazy.mymovielist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

class InitializeDbHelper extends AsyncTask<Object, Void, String> {
    private SQLiteDatabase db;
    private String TAG = InitializeDbHelper.class.getSimpleName();
    private static Boolean complete = true;
    private static long completedPercent = 0;

    public static boolean isCompleteLoading() {
	return complete;
    }

    public static long getLoadPercent() {
	return completedPercent;
    }

    @Override
    protected String doInBackground(Object... params) {
	String response = "";
	DatabaseHandler dbHelper = (DatabaseHandler) params[0];
	Context context = (Context) params[1];

	db = dbHelper.getWritableDatabase();
	Log.d(TAG, "Creating Table");

	// TODO : remove
	// String sql = "DROP TABLE IF EXISTS " + DatabaseHandler.tableName;
	// db.execSQL(sql);

	if (isTableExists(db, DatabaseHandler.TABLE_NAME)) {
	    Log.d(TAG, "table already exists");

	    Cursor mCount = db.rawQuery("select count(*) from "
		    + DatabaseHandler.TABLE_NAME, null);
	    mCount.moveToFirst();
	    int rowCount = mCount.getInt(0);
	    mCount.close();

	    if (rowCount > 2000) {
		return response;
	    }
	} else {
	    db.execSQL(DatabaseHandler.CREATE_TABLE_MOVIES);
	    db.close();
	}

	// load data
	AssetManager assetManager = context.getAssets();
	try {
	    InputStream inputStream = assetManager
		    .open("movies_cleaned_list.sql");
	    InputStreamReader streamReader = new InputStreamReader(inputStream);
	    BufferedReader bufferedReader = new BufferedReader(streamReader);
	    String line;
	    int count = 0;
	    db = dbHelper.getWritableDatabase();
	    int masterCount = 1;
	    complete = false;
	    while ((line = bufferedReader.readLine()) != null && completedPercent < 3) {
		/*
		 * String insert = "INSERT or replace INTO " +
		 * DatabaseHandler.tableName + " (name) VALUES('" + line.trim()
		 * + "')";
		 */
		db.execSQL(line);
		// Log.d(TAG, count + " : " + line);
		count++;
		if (count == 500) {
		    count = 0;
		    completedPercent = ((500 * masterCount++) * 100) / 78000;
		    Log.d(TAG, "done : " + completedPercent + "%");
		    SharedPreferences sharedpreferences = context
			    .getSharedPreferences(MainActivity.MyPREFERENCES,
				    context.MODE_PRIVATE);
		    Editor editor = sharedpreferences.edit();
		    editor.putLong(MainActivity.DB_LOAD_PERCENT,
			    completedPercent);
		    editor.commit();
		}
	    }

	    Log.d(TAG, "********* Completed!!! ********");
	    SharedPreferences sharedpreferences = context.getSharedPreferences(
		    MainActivity.MyPREFERENCES, context.MODE_PRIVATE);
	    Editor editor = sharedpreferences.edit();
	    editor.putString(MainActivity.DB_CREATED, "true");
	    editor.commit();
	    complete = true;
	} catch (IOException e) {
	    Log.e("TBCAE", "Failed to open data input file");
	    e.printStackTrace();
	}
	return response;
    }

    @Override
    protected void onPostExecute(String result) {
	db.close();
	complete = true;
    }

    public boolean isTableExists(SQLiteDatabase db, String tableName) {
	Cursor cursor = db.rawQuery(
		"select DISTINCT tbl_name from sqlite_master where tbl_name = '"
			+ tableName + "'", null);
	if (cursor != null) {
	    if (cursor.getCount() > 0) {
		cursor.close();
		return true;
	    }
	    cursor.close();
	}
	return false;
    }
}