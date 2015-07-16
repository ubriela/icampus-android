package edu.usc.imsc.trams.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import edu.usc.imsc.trams.stops.TramStop;
import edu.usc.imsc.trams.vehicles.TramVehicle;
import edu.usc.imsc.util.Tools;

public class TramXMLParser {

	List<ITramListener> tramListeners; // do not use currently
	private Context context;

	public TramXMLParser(Context ctx) {
		context = ctx;
		tramListeners = new ArrayList<ITramListener>();
	}

	public void addTramListener(ITramListener tramListener) {
		tramListeners.add(tramListener);
	}

	public void removeTramListener(ITramListener tramListener) {
		tramListeners.remove(tramListener);
	}

	public void clearTramListener() {
		tramListeners.clear();
	}

	public Map<Integer, TramRoute> parseXMLFile(InputStream inputStream) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(inputStream);

			// get the root element
			Element docEle = dom.getDocumentElement();

			Map<Integer, TramRoute> tramRoutes = new HashMap<Integer, TramRoute>();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("route");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {

					// get the event element
					Element el = (Element) nl.item(i);

					// get the event object
					TramRoute tramRoute = parseRoute(el);
					tramRoutes.put(tramRoute.getId(), tramRoute);
				}
			}
			return tramRoutes;
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}

	/**
	 * parsing route including paring vehicles & stops
	 * 
	 * @param routeElement
	 * @return
	 */
	private TramRoute parseRoute(Element routeElement) {
		int routeID = Integer.parseInt(routeElement.getAttribute("id"));
		TramRoute tramRoute = new TramRoute(routeID,
				RouteName.getRouteNameById(routeID));

		Log.d(Tools.TAG, TramXMLParser.class.getName() + " routeid = " + tramRoute.getId() + ", route name="
				+ tramRoute.getName());
		String route = tramRoute.getName();

		NodeList nl = routeElement.getElementsByTagName("vehicle");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the vehicle element
				Element el = (Element) nl.item(i);
				// get the vehicle object
				TramVehicle vehicle = parseVehicle(el, route);
				tramRoute.putVehicle(vehicle.getId(), vehicle);
			}
		}

		nl = routeElement.getElementsByTagName("arrival");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the event element
				Element el = (Element) nl.item(i);

				// get the event object
				TramStop tramStop = parseArrival(tramRoute.getId(), el);
				if (tramStop == null)
					continue;
				tramRoute.putStop(tramStop.getId(), tramStop);
			}
		}

		return tramRoute;
	}

	/**
	 * parse vehicle xml description to get id, lat, lon, doorStatus,
	 * last_update, speed, heeding
	 * 
	 * @param vehicleElement
	 * @param route
	 * @return
	 */
	private TramVehicle parseVehicle(Element vehicleElement, String route) {
		int vehicleID = Integer.parseInt(vehicleElement.getAttribute("id"));
		double lat = Double
				.parseDouble(vehicleElement.getAttribute("latitude"));
		double lon = Double.parseDouble(vehicleElement
				.getAttribute("longitude"));
		GeoPoint geoPoint = new GeoPoint((int) (lat * 1e6), (int) (lon * 1e6));
		boolean doorStatus = Integer.parseInt(vehicleElement.getAttribute(
				"door_status").trim()) == 0 ? true : false;
		String time = vehicleElement.getAttribute("last_update");
		double speed = Double.parseDouble(vehicleElement.getAttribute("speed"));
		String heading = vehicleElement.getAttribute("heading");

		TramVehicle tramVehicle = new TramVehicle(vehicleID, geoPoint, route,
				doorStatus, time, speed, heading);
		Log.e(Tools.TAG, TramXMLParser.class.getName() + " :parseVehicle: " + tramVehicle.toString());
		return tramVehicle;
	}

	/**
	 * parse TramStopn xml description to get stopId,
	 * 
	 * @param route
	 * @param arrivalElement
	 * @return
	 */
	private TramStop parseArrival(int route, Element arrivalElement) {
		int stopID = Integer.parseInt(arrivalElement.getAttribute("stop"));
		int arrivalTime = Integer.parseInt(arrivalElement.getAttribute("time"));
		TramStop stop = TramApp.getInstance(context)
				.getTramStopByRouteIdAndStopId(route, stopID);
		if (stop == null) {
			Log.e(Tools.TAG, TramXMLParser.class.getName() + " Error in the XML input, stopid " + stopID
					+ " is not supported in route " + route);
		} else {
			if (stop.getMinutesToArrival() > arrivalTime / 60) {
				stop.setMinutesToArrival(arrivalTime / 60);
			}
		}
		return stop;
	}
}
