package edu.usc.imsc.food;

import java.math.BigDecimal;

/**
 * Buildings generated by hbm2java
 */
public class Food {

    String pid;
    String time;
    String title;
    String rating;
    String place;
    String photo;
    String comment;
    String smallphoto;
    String iconphoto;
    private String lat;
	private String lon;
	String dist;
	
	
	
	public Food(String pid, String time, String title, String rating,
			String place, String photo, String comment, String smallphoto,
			String iconphoto, String lat, String lon) {
		super();
		this.pid = pid;
		this.time = time;
		this.title = title;
		this.rating = rating;
		this.place = place;
		this.photo = photo;
		this.comment = comment;
		this.smallphoto = smallphoto;
		this.iconphoto = iconphoto;
		this.lat = lat;
		this.lon = lon;
	}
	
	
	public Food(String time, String title, String rating,
			String place, String comment, String smallphoto, String lat,
			String lon) {
		super();
		this.time = time;
		this.title = title;
		this.rating = rating;
		this.place = place;
		this.comment = comment;
		this.smallphoto = smallphoto;
		this.lat = lat;
		this.lon = lon;
	}


	public Food(String string, String string2) {
		// TODO Auto-generated constructor stub
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getDist() {
		return dist;
	}
	public void setDist(String dist) {
		this.dist = dist;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getSmallphoto() {
		return smallphoto;
	}
	public void setSmallphoto(String smallphoto) {
		this.smallphoto = smallphoto;
	}
	public String getIconphoto() {
		return iconphoto;
	}
	public void setIconphoto(String iconphoto) {
		this.iconphoto = iconphoto;
	}
	
	
	
}