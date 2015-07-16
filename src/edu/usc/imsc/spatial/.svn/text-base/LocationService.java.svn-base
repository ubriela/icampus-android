package edu.usc.imsc.spatial;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import edu.usc.imsc.R;

/**
 * This class retrieves the user's current location from the GPS provider.
 * @original  author William Quach, Nga Chung
 * @author  linghu
 */
public class LocationService {
	private static final double DEFAULT_LOCATION_LAT = 34.0225096780457;
	private static final double DEFAULT_LOCATION_LON = -118.284359926051;
	private static final long MIN_DIST_LOCATION_UPDATE = 5;
	private static final float MIN_TIME_LOCATION_UPDATE = 5;
	private static final String MOCK_PROVIDER_NAME = "mockProvider";

	private Context ctx;
	private LocationManager locMgr;
	private boolean useMockLocation;
	private boolean useWifiLocation;
	private long minDistLocationUpdate;
	private float minTimeLocationUpdate;
	private String locProvider;

	/**
	 * Constructor
	 * 
	 * @param ctx context that is constructing this class
	 */
	public LocationService(Context ctx) {
		this.ctx = ctx;

		try {
			useMockLocation = Boolean.parseBoolean(ctx
					.getString(R.string.useMockLocation));
		} catch (Exception e) {
			useMockLocation = false;
		}

		try {
			useWifiLocation = Boolean.parseBoolean(ctx
					.getString(R.string.useWifiLocation));
		} catch (Exception e) {
			useWifiLocation = false;
		}

		locMgr = (LocationManager) ctx
				.getSystemService(Context.LOCATION_SERVICE);
		locProvider = "";
		if (useMockLocation) {
			setUseMockLocation(useMockLocation);
		} else if (useWifiLocation) {
			setUseWifi(useWifiLocation);
		} else {
			initDefaultProvider();
		}
		
		try {
			minDistLocationUpdate = Long.parseLong(ctx
					.getString(R.string.minDistLocationUpdate));
		} catch (Exception e) {
			minDistLocationUpdate = MIN_DIST_LOCATION_UPDATE;
		}

		try {
			minTimeLocationUpdate = Float.parseFloat(ctx
					.getString(R.string.minTimeLocationUpdates));
		} catch (Exception e) {
			minTimeLocationUpdate = MIN_TIME_LOCATION_UPDATE;
		}

		Log.i("Location Provider", locProvider);
	}

	/**
	 * Adds specified LocationListener.
	 * 
	 * @param listener LocationListener
	 */
	public void addLocationListener(LocationListener listener) {
		locMgr.requestLocationUpdates(locProvider, minDistLocationUpdate,
				minTimeLocationUpdate, listener);
	}

	/**
	 * Removes specified LocationListener.
	 * 
	 * @param listener LocationListener
	 */
	public void removeLocationListener(LocationListener listener) {
		locMgr.removeUpdates(listener);
	}

	/**
	 * Sets the minimum change in distance before a new location is requested.
	 * @param minDistLocationUpdate  minimum change in distance before a new  location is requested
	 * @uml.property  name="minDistLocationUpdate"
	 */
	public void setMinDistLocationUpdate(long minDistLocationUpdate) {
		this.minDistLocationUpdate = minDistLocationUpdate;
	}

	/**
	 * Sets the minimum change in time before a new location is requested.
	 * 
	 * @param minTimeLocationUpdate minimum change in time before a new location
	 *            is requested
	 */
	public void setMinTimeLocationUpdate(long minTimeLocationUpdate) {
		this.minTimeLocationUpdate = minTimeLocationUpdate;
	}

	/**
	 * If true enable location retrieval using WiFi.
	 * 
	 * @param useWifi if true enable location retrieval using WiFi
	 */
	public void setUseWifi(boolean useWifi) {
		if (useWifi) {
			locProvider = LocationManager.NETWORK_PROVIDER;
		} else {
			initDefaultProvider();
		}
	}

	/**
	 * If true uses the user configurable mockLocationLat/mockLocationLon parameters in Android configuration file: strings.xml.
	 * @param  useMockLocation
	 * @uml.property  name="useMockLocation"
	 */
	public void setUseMockLocation(boolean useMockLocation) {
		if (useMockLocation) {
			double lat = DEFAULT_LOCATION_LAT;
			double lon = DEFAULT_LOCATION_LON;

			try {
				lat = Double.parseDouble(ctx
						.getString(R.string.mockLocationLat));
				lon = Double.parseDouble(ctx
						.getString(R.string.mockLocationLon));
			} catch (Exception e) {
			}

			try {
				locMgr.addTestProvider(MOCK_PROVIDER_NAME, false, false, false,
						false, true, true, true, Criteria.POWER_LOW,
						Criteria.ACCURACY_FINE);
			} catch (IllegalArgumentException e) {}

			Location loc = new Location(MOCK_PROVIDER_NAME);
			loc.setLatitude(lat);
			loc.setLongitude(lon);
			loc.setTime(System.currentTimeMillis());

			locMgr.setTestProviderLocation(MOCK_PROVIDER_NAME, loc);
			locProvider = MOCK_PROVIDER_NAME;
		} else {
			initDefaultProvider();
		}
	}

	private void initDefaultProvider() {
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.NO_REQUIREMENT);
		locProvider = locMgr.getBestProvider(crit, true);

		Log.i("Location Provider", locProvider);
	}
}
