package edu.usc.imsc.trams.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint;

import edu.usc.imsc.trams.stops.TramStop;
import edu.usc.imsc.trams.vehicles.TramVehicle;

/**
 * @author infolab
 */
public class TramRoute {

	/**
	 * @uml.property name="id"
	 */
	private int id; // tram route id
	/**
	 * @uml.property name="name"
	 */
	private String name;
	/**
	 * @uml.property name="stops"
	 */
	private Map<Integer, TramStop> stops; // indexed by stopID
	/**
	 * @uml.property name="vehicles"
	 */
	private Map<Integer, TramVehicle> vehicles;
	
	private List<GeoPoint> coords; // just the ordered list of the path we need to draw for this route

	public TramRoute(int id, String name) {
		super();
		this.id = id;
		this.name = name;
		stops = new HashMap<Integer, TramStop>();
		vehicles = new HashMap<Integer, TramVehicle>();
		coords = new ArrayList<GeoPoint>();
	}

	public void putVehicle(int id, TramVehicle vehicle) {
		this.vehicles.put(id, vehicle);
	}

	public void putStop(int id, TramStop stop) {
		this.stops.put(id, stop);
	}
	
	public void addCoord(GeoPoint coord) {
		this.coords.add(coord);
	}
	
	public List<GeoPoint> getCoords() {
		return coords;
	}

	/**
	 * @return the id
	 * @uml.property name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the stops
	 * @uml.property name="stops"
	 */
	public Map<Integer, TramStop> getStops() {
		return stops;
	}

	/**
	 * @return the vehicles
	 * @uml.property name="vehicles"
	 */
	public Map<Integer, TramVehicle> getVehicles() {
		return vehicles;
	}

}
