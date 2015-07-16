package edu.usc.imsc.trucks;

import com.google.android.maps.GeoPoint;

/**
 * @author infolab
 */
public class Truck {
	/**
	 * @uml.property name="id"
	 */
	private int id;
	/**
	 * @uml.property name="location"
	 */
	private GeoPoint location;
	/**
	 * @uml.property name="name"
	 */
	private String name;
	/**
	 * @uml.property name="tweet"
	 */
	private String tweet;
	/**
	 * @uml.property name="time"
	 */
	private String time;

	public Truck(int id, GeoPoint location, String name, String tweet,
			String time) {
		super();
		this.id = id;
		this.location = location;
		this.name = name;
		this.tweet = tweet;
		this.time = time;
	}

	/**
	 * @return
	 * @uml.property name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 * @uml.property name="location"
	 */
	public GeoPoint getLocation() {
		return location;
	}

	/**
	 * @return
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 * @uml.property name="tweet"
	 */
	public String getTweet() {
		return tweet;
	}

	/**
	 * @return
	 * @uml.property name="time"
	 */
	public String getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "Truck [id=" + id + ", location=" + location + ", name=" + name
				+ ", tweet=" + tweet + ", time=" + time + "]";
	}
}
