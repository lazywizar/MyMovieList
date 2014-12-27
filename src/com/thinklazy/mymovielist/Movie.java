package com.thinklazy.mymovielist;

public class Movie {
    
    public String name;
    public String status;
    public String rating;
    public String genre;
    public String seen;
    public String wishlist;
    
    public Movie(String name, String status, String rating, String genre,
	    String seen, String wishlist, String id) {
	super();
	this.name = name;
	this.status = status;
	this.rating = rating;
	this.genre = genre;
	this.seen = seen;
	this.wishlist = wishlist;
	this.id = id;
    }

    public String id;
    
    public Movie(String name, String status, String rating,
	    String genre, String id) {
	super();
	this.name = name;
	this.status = status;
	this.rating = rating;
	this.genre = genre;
	this.id = id;
    }
 
    public Movie(String name) {
	super();
	this.name = name;
    }
 
   
}
