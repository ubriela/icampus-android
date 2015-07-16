package edu.usc.imsc.spatial;

import com.google.android.maps.GeoPoint;

/**
 * This class represents a point place. Eg. a USC building or a location of the
 * vendor
 * 
 * @original author William Quach, Nga Chung
 * @author linghu
 */
public class Building extends AbstractPointPlace implements Comparable<Building> {
	
	private String shortName = null;
	private String address = null;
	private String departmentName = null;
	private String departmentWebsite = null;
	private String departmentLocation = null;
	private String description = null;
	private String website = null;

	/**
	 * Constructs a Place object.
	 */
	public Building() {

	}

	/**
	 * Constructs a Place object which represents a USC building.
	 * 
	 * @param id
	 *            ID of building
	 * @param name
	 *            full name of building
	 * @param shortName
	 *            abbreviated name of building
	 */
	private Building(int id, String name, String shortName) {
		this(id);
		this.name = name;
		this.shortName = shortName;
	}

	/**
	 * Constructs a Place object which represents a USC building.
	 * 
	 * @param id
	 *            ID of building
	 */
	private Building(int id) {
		this.id = id;
	}

	/**
	 * Constructs a Place object which represents a USC building.
	 * 
	 * @param id
	 *            ID of building
	 * @param name
	 *            full name of building
	 * @param shortName
	 *            abbreviated name of building
	 * @param latitude
	 *            latitude of centroid of building
	 * @param longitude
	 *            longitude of centroid of building
	 */
	public Building(int id, String name, String shortName, double latitude,
			double longitude) {
		this(id, name, shortName);

		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Returns abbreviated name of building.
	 * 
	 * @return abbreviated name of building
	 * @uml.property name="shortName"
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Sets abbreviated name of building.
	 * 
	 * @param shortName
	 *            abbreviated name of building
	 * @uml.property name="shortName"
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(Building another) {
		return this.getName().compareTo(another.getName());
	}
	
	public GeoPoint getLocation() {
		return new GeoPoint((int) (getLatitude() * 1E6), (int) (getLongitude() * 1E6));
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDepartmentWebsite() {
		return departmentWebsite;
	}

	public void setDepartmentWebsite(String departmentWebsite) {
		this.departmentWebsite = departmentWebsite;
	}

	public String getDepartmentLocation() {
		return departmentLocation;
	}

	public void setDepartmentLocation(String departmentLocation) {
		this.departmentLocation = departmentLocation;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
	
	
}
