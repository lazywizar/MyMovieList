package com.thinklazy.mymovielist;

public class Movie {
    
    public String name;
    public String status;
    public String rating;
    public String genre;
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
