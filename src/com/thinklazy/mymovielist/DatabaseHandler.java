package com.thinklazy.mymovielist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
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
    public final static String TABLE_NAME = "movies_top_unique";
    public final static String FIELD_OBJECT_ID = "id";
    public final static String NAME = "name";
    public final static String STATUS = "status";
    public final static String RATING = "rating";
    public final static String YEAR = "year";
    public final static String GENRE = "genre";
    public final static String SEEN = "seen";
    public final static String WISHLIST = "wishlist";
    
    public final static String id = "id";

    public static final String CREATE_TABLE_MOVIES = "CREATE TABLE if not exists "
	    + TABLE_NAME
	    + "("
	    + id + " TEXT PRIMARY KEY,"
	    + NAME + " TEXT,"
	    + STATUS + " TEXT,"
	    + RATING + " TEXT,"
	    + SEEN + " TEXT,"
	    + WISHLIST + " TEXT," 
	    + GENRE + " TEXT" + ")";

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

	String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
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
	Cursor cursor = db.rawQuery("SELECT " + FIELD_OBJECT_ID + " FROM "
		+ TABLE_NAME + " WHERE name= '" + movieName + "'", null);

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
	sql += "SELECT * FROM " + TABLE_NAME;
	sql += " WHERE " + this.NAME + " LIKE '%" + searchTerm + "%'";
	sql += " ORDER BY " + FIELD_OBJECT_ID + " DESC";
	sql += " LIMIT 0,3";

	SQLiteDatabase db = this.getWritableDatabase();

	// execute the query
	Cursor cursor = db.rawQuery(sql, null);

	// looping through all rows and adding to list
	if (cursor.moveToFirst()) {
	    do {
		String objectName = cursor.getString(cursor
			.getColumnIndex(this.NAME));
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
	sql += "SELECT * FROM " + TABLE_NAME;
	sql += " WHERE " + NAME + " = '" + movieName + "'";

	SQLiteDatabase db = this.getWritableDatabase();

	// execute the query
	Cursor cursor = db.rawQuery(sql, null);

	// looping through all rows and adding to list
	if (cursor.moveToFirst()) {
	    String genreName = cursor.getString(cursor.getColumnIndex(GENRE));
	    String ratingName = cursor
		    .getString(cursor.getColumnIndex(RATING));
	    String seendate = cursor
		    .getString(cursor.getColumnIndex(SEEN));
	    String isWishlist = cursor
		    .getString(cursor.getColumnIndex(WISHLIST));
	    
	    Movie movie = new Movie(movieName, null, ratingName, genreName, seendate, isWishlist, null);
	    return movie;
	}

	return null;
    }

    public void markSeen(String movieName) {
	// select query
	String sql = "";
	sql += "update "+ TABLE_NAME ;
	sql += " set " + SEEN + "='" +  getDateTime()  + "' where name = '" ;
	sql += movieName + "'";

	SQLiteDatabase db = this.getWritableDatabase();

	// execute the query
	db.execSQL(sql);
    }
    
    public void addToWish(String movieName) {
	// select query
	String sql = "";
	sql += "update "+ TABLE_NAME ;
	sql += " set " + WISHLIST + "='Y' where name = '" ;
	sql += movieName + "'";

	SQLiteDatabase db = this.getWritableDatabase();

	// execute the query
	db.execSQL(sql);
    }
    
    public List<Movie> getMyMovies() {
	List<Movie> recordsList = new ArrayList<Movie>();

	// select query
	String sql = "";
	sql += "SELECT * FROM " + TABLE_NAME;
	sql += " WHERE " + SEEN + " LIKE '%" + "-" + "%'";

	SQLiteDatabase db = this.getWritableDatabase();

	// execute the query
	Cursor cursor = db.rawQuery(sql, null);

	// looping through all rows and adding to list
	if (cursor.moveToFirst()) {
	    do {
		String movieName = cursor.getString(cursor
			.getColumnIndex(NAME));
		String genreName = cursor.getString(cursor
			.getColumnIndex(GENRE));
		String ratingName = cursor.getString(cursor
			.getColumnIndex(RATING));
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
    
    public void addMovie(Movie movie) {
	SQLiteDatabase db = this.getWritableDatabase();

	ContentValues values = new ContentValues();
	// values.put(KEY_ID, contact.get_id());
	values.put(NAME, movie.name);
	values.put(STATUS, movie.status);
	values.put(RATING, movie.rating);
	values.put(WISHLIST, movie.wishlist);
	values.put(SEEN, movie.seen);
	values.put(GENRE, movie.genre);
	values.put(id, movie.id);

	// Inserting Row
	db.insert(TABLE_NAME, null, values);
	db.close(); // Closing database connection
}
    
    public List<Movie> getWishList() {
	List<Movie> recordsList = new ArrayList<Movie>();

	// select query
	String sql = "";
	sql += "SELECT * FROM " + TABLE_NAME;
	sql += " WHERE " + WISHLIST + " LIKE '%" + "Y" + "%'";

	SQLiteDatabase db = this.getWritableDatabase();

	// execute the query
	Cursor cursor = db.rawQuery(sql, null);

	// looping through all rows and adding to list
	if (cursor.moveToFirst()) {
	    do {
		String movieName = cursor.getString(cursor
			.getColumnIndex(NAME));
		String genreName = cursor.getString(cursor
			.getColumnIndex(GENRE));
		String ratingName = cursor.getString(cursor
			.getColumnIndex(RATING));
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
    
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
}
    
}