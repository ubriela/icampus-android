package edu.usc.imsc.trams.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import edu.usc.imsc.trams.stops.TramStop;
import edu.usc.imsc.trams.vehicles.TramVehicle;
import edu.usc.imsc.util.Tools;

/**
 * TramApp is a singleton class, it stores all tram routes and stops that are
 * supported by this app
 * 
 * @author Ling
 */
public class TramApp {

	/**
	 * @uml.property name="singleton"
	 * @uml.associationEnd
	 */
	private static TramApp singleton;
	/**
	 * @uml.property name="supportedRoutes"
	 */
	private Map<Integer, TramRoute> supportedRoutes;
	private Context context;

	public static TramApp getInstance(Context context) {
		if (singleton == null) {
			singleton = new TramApp(context);
		}
		return singleton;
	}

	private TramApp(Context con) {
		supportedRoutes = new HashMap<Integer, TramRoute>();
		context = con;
		try {
			populateRoute("parkingCenter", Tools.tram_parkingCenter_ID);
			populateRoute("routeA", Tools.tram_routeA_ID);
			populateRoute("routeB", Tools.tram_routeB_ID);
			populateRoute("routeC", Tools.tram_routeC_ID);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * find the stop in the route by their ids If the stop id does not exist in
	 * supported routes, return null
	 * 
	 * @param routeid
	 * @param stopid
	 * @return
	 */
	public TramStop getTramStopByRouteIdAndStopId(int routeid, int stopid) {
		if (supportedRoutes.containsKey(routeid)) {
			TramRoute route = supportedRoutes.get(routeid);
			Map<Integer, TramStop> stops = route.getStops();
			if (stops.containsKey(stopid)) {
				return stops.get(stopid);
			} else {
				Log.e(Tools.TAG, "stopid " + stopid
						+ " is not supported in route " + routeid);
			}
		} else {
			Log.e(Tools.TAG, "routeid " + routeid + " is not supported.");
		}
		return null;
	}

	/**
	 * update arrival information of a TramStop
	 * 
	 * @param routeid
	 * @param stopid
	 * @param minutesToArrival
	 */
	public void updateArrivalInfoByRouteIdAndStopId(int routeid, int stopid,
			int minutesToArrival) {
		if (supportedRoutes.containsKey(routeid)) {
			TramRoute route = supportedRoutes.get(routeid);
			if (route.getStops().containsKey(stopid)) {
				TramStop stop = route.getStops().get(stopid);
				stop.setMinutesToArrival(minutesToArrival);
				route.putStop(stopid, stop);
				supportedRoutes.put(route.getId(), route);
			} else {
				Log.e(Tools.TAG, TramApp.class.getName() + stopid
						+ " is not supported in route " + routeid);
			}
		} else {
			Log.e(Tools.TAG, "routeid " + routeid + " is not supported.");
		}
	}

	/**
	 * put a TramVehicle into TramRoute
	 * 
	 * @param routeid
	 * @param vehicle
	 */
	public void putVehicle(int routeid, TramVehicle vehicle) {
		if (supportedRoutes.containsKey(routeid)) {
			TramRoute route = supportedRoutes.get(routeid);
			route.putVehicle(vehicle.getId(), vehicle);
			supportedRoutes.put(route.getId(), route);
		} else {
			Log.e(Tools.TAG, "routeid " + routeid + " is not supported.");
		}
	}

	/**
	 * @return the supportedRoutes
	 * @uml.property name="supportedRoutes"
	 */
	public Map<Integer, TramRoute> getSupportedRoutes() {
		return supportedRoutes;
	}

	private void populateRoute(String fileName, int routeID) throws IOException {
		TramRoute route = new TramRoute(routeID, fileName);
		supportedRoutes.put(routeID, route);

		populateRouteStops(route);
		populateRouteCoords(route);
	}

	private void populateRouteCoords(TramRoute route) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(context
				.getAssets().open(route.getName() + "_coords")));
		String line = null;
		while ((line = br.readLine()) != null) {
			line = line.substring(1, line.indexOf(']'));
			String parts[] = line.split(",");
			double lat = Double.parseDouble(parts[0].trim());
			double lon = Double.parseDouble(parts[1].trim());
			GeoPoint geoPoint = new GeoPoint((int) (lat * 1e6),
					(int) (lon * 1e6));
			route.addCoord(geoPoint);
		}
		br.close();
	}

	/**
	 * populate the route using the corresponding file under res/asset/
	 * 
	 * @param fileName
	 * @param routeID
	 * @throws IOException
	 */
	private void populateRouteStops(TramRoute route) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(context
				.getAssets().open(route.getName() + "_stops")));
		String line = null;
		while ((line = br.readLine()) != null) {
			line = line.substring(1, line.indexOf(']'));
			String parts[] = line.split(",");
			double lat = Double.parseDouble(parts[0].trim());
			double lon = Double.parseDouble(parts[1].trim());
			GeoPoint geoPoint = new GeoPoint((int) (lat * 1e6),
					(int) (lon * 1e6));
			String name = parts[2].replace("'", "").replace("\"", "")
					.replace("&amp;", "&").trim();
			int stopID = Integer.parseInt(parts[3].trim());
			TramStop tramStop = new TramStop(stopID, name, geoPoint);
			route.putStop(stopID, tramStop);
		}
		br.close();
	}

	/**
	 * Update the Tram Routes' information including TramVehicle and Tram Stop
	 * 
	 * @param tramRoutes
	 */
	public void update(Map<Integer, TramRoute> tramRoutes) {
		if (tramRoutes == null) {
			return;
		}
		for (TramRoute tramRoute : tramRoutes.values()) {
			TramRoute currentRoute = supportedRoutes.get(tramRoute.getId());
			if (currentRoute == null) {
				continue; // means we have data for a route we don't actually
							// support right now
			}

			// update TramVehicles
			for (TramVehicle vehicle : tramRoute.getVehicles().values()) { // for
																			// each
																			// of
																			// the
																			// new
																			// values
				putVehicle(tramRoute.getId(), vehicle);
			}

			// update TramStops
			// Note: currentRoute has been changed, it should be put back to the
			// supportedRoutes
			for (TramStop stop : tramRoute.getStops().values()) {
				Log.i(Tools.TAG,
						TramApp.class.getName() + " stop-id=" + stop.getId()
								+ ", minutesToArrival="
								+ stop.getMinutesToArrival()); // "stop-name="+stop.name+", +",location="+stop.coord.toString());
				if (stop.getMinutesToArrival() > -1) {
					updateArrivalInfoByRouteIdAndStopId(tramRoute.getId(),
							stop.getId(), stop.getMinutesToArrival());
				}
			}
		}
	}
}
