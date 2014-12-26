package com.thinklazy.mymovielist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    // for our logs
    public static final String TAG = "DatabaseHandler.java";

    // database version
    private static final int DATABASE_VERSION = 4;

    // database name
    protected static final String DATABASE_NAME = "movies";

    // table details
    public final static String tableName = "movies_top_unique";
    public final static String fieldObjectId = "id";
    public final static String name = "name";
    public final static String status = "status";
    public final static String rating = "rating";
    public final static String year = "year";
    public final static String genre = "genre";
    public final static String seen = "seen";
    public final static String wishlist = "wishlist";
    public final static String id = "id";

    public static final String CREATE_TABLE_MOVIES = "CREATE TABLE if not exists "
	    + tableName
	    + "("
	    + id + " TEXT PRIMARY KEY,"
	    + name + " TEXT,"
	    + status + " TEXT,"
	    + rating + " TEXT,"
	    + seen + " TEXT,"
	    + wishlist + " TEXT," 
	    + genre + " TEXT" + ")";

    // constructor
    public DatabaseHandler(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creating table
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    // When upgrading the database, it will drop the current table and recreate.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	String sql = "DROP TABLE IF EXISTS " + tableName;
	db.execSQL(sql);

	onCreate(db);
    }

    /*
     * // create new record // @param myObj contains details to be added as
     * single row. public boolean create(Movie movie) {
     * 
     * boolean createSuccessful = false;
     * 
     * // if(!checkIfExists(movie.name)){
     * 
     * SQLiteDatabase db = this.getWritableDatabase();
     * 
     * String sql = "INSERT or replace INTO " + tableName +
     * " (name, status, rating, year, genre, id) VALUES('" + movie.name +
     * "' , '" + movie.cast + "', '" + movie.genre + "')"; db.execSQL(sql);
     * db.close(); return createSuccessful; }
     */

    // check if a record exists so it won't insert the next time you run this
    // code
    public boolean checkIfExists(String movieName) {

	boolean recordExists = false;

	SQLiteDatabase db = this.getWritableDatabase();
	Cursor cursor = db.rawQuery("SELECT " + fieldObjectId + " FROM "
		+ tableName + " WHERE name= '" + movieName + "'", null);

	if (cursor != null) {

	    if (cursor.getCount() > 0) {
		recordExists = true;
	    }
	}

	cursor.close();
	db.close();

	return recordExists;
    }

    // Read records related to the search term
    public List<Movie> read(String searchTerm) {

	List<Movie> recordsList = new ArrayList<Movie>();

	// select query
	String sql = "";
	sql += "SELECT * FROM " + tableName;
	sql += " WHERE " + this.name + " LIKE '%" + searchTerm + "%'";
	sql += " ORDER BY " + fieldObjectId + " DESC";
	sql += " LIMIT 0,5";

	SQLiteDatabase db = this.getWritableDatabase();

	// execute the query
	Cursor cursor = db.rawQuery(sql, null);

	// looping through all rows and adding to list
	if (cursor.moveToFirst()) {
	    do {
		String objectName = cursor.getString(cursor
			.getColumnIndex(this.name));
		Movie movie = new Movie(objectName);

		// add to list
		recordsList.add(movie);

	    } while (cursor.moveToNext());
	}

	cursor.close();
	db.close();

	// return the list of records
	return recordsList;
    }

    // Read records related to the search term
    public Movie get(String movieName) {
	// select query
	String sql = "";
	sql += "SELECT * FROM " + tableName;
	sql += " WHERE " + this.name + " = '" + movieName + "'";

	SQLiteDatabase db = this.getWritableDatabase();

	// execute the query
	Cursor cursor = db.rawQuery(sql, null);

	// looping through all rows and adding to list
	if (cursor.moveToFirst()) {
	    String moviewName = cursor.getString(cursor
		    .getColumnIndex(this.name));
	    String genre = cursor.getString(cursor.getColumnIndex(this.genre));
	    String rating = cursor
		    .getString(cursor.getColumnIndex(this.rating));
	    Movie movie = new Movie(moviewName, null, rating, genre, null);
	    return movie;
	}

	return null;
    }

    public void markRead(String movieName) {
	// select query
	String sql = "";
	sql += "update "+ tableName ;
	sql += " set " + seen + "='Y' where name = '" ;
	sql += movieName + "'";

	SQLiteDatabase db = this.getWritableDatabase();

	// execute the query
	db.execSQL(sql);
    }
    
    public List<Movie> getMyMovies() {
	List<Movie> recordsList = new ArrayList<Movie>();

	// select query
	String sql = "";
	sql += "SELECT * FROM " + tableName;
	sql += " WHERE " + seen + " LIKE '%" + "Y" + "%'";

	SQLiteDatabase db = this.getWritableDatabase();

	// execute the query
	Cursor cursor = db.rawQuery(sql, null);

	// looping through all rows and adding to list
	if (cursor.moveToFirst()) {
	    do {
		String movieName = cursor.getString(cursor
			.getColumnIndex(name));
		String genreName = cursor.getString(cursor
			.getColumnIndex(genre));
		String ratingName = cursor.getString(cursor
			.getColumnIndex(rating));
		Movie movie = new Movie(movieName, null, ratingName, genreName, null);
		// add to list
		recordsList.add(movie);

	    } while (cursor.moveToNext());
	}

	cursor.close();
	db.close();

	// return the list of records
	return recordsList;
    }
}