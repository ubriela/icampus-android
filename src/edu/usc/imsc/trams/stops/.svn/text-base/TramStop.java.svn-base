package edu.usc.imsc.trams.stops;

import com.google.android.maps.GeoPoint;

/**
 * @author infolab
 */
public class TramStop {

	public static final int TIME_UNKNOWN = 60 * 60 * 60;
	/**
	 * @uml.property name="id"
	 */
	private int id;
	/**
	 * @uml.property name="name"
	 */
	private String name;
	/**
	 * @uml.property name="coord"
	 */
	private GeoPoint coord;
	/**
	 * @uml.property name="color"
	 */
	private int color;
	/**
	 * @uml.property name="minutesToArrival"
	 */
	private int minutesToArrival;

	public TramStop(int id, String name, GeoPoint coords) {
		super();
		this.coord = coords;
		this.name = name;
		this.id = id;
		minutesToArrival = TIME_UNKNOWN;
	}

	@Override
	public String toString() {
		return "TramStop [id=" + id + ", coord=" + coord + ", name=" + name
				+ ", minutesToArrival=" + minutesToArrival + ", color=" + color
				+ "]";
	}

	/**
	 * @return
	 * @uml.property name="color"
	 */
	public int getColor() {
		return color;
	}

	/**
	 * @return the coord
	 * @uml.property name="coord"
	 */
	public GeoPoint getCoord() {
		return coord;
	}

	/**
	 * @return the name
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the id
	 * @uml.property name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the minutesToArrival
	 * @uml.property name="minutesToArrival"
	 */
	public int getMinutesToArrival() {
		return minutesToArrival;
	}

	/**
	 * @param minutesToArrival
	 *            the minutesToArrival to set
	 * @uml.property name="minutesToArrival"
	 */
	public void setMinutesToArrival(int minutesToArrival) {
		this.minutesToArrival = minutesToArrival;
	}
}
