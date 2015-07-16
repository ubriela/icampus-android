package edu.usc.imsc.activities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ZoomButtonsController.OnZoomListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomButton;
import android.widget.ZoomButtonsController;
import android.widget.ZoomControls;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import edu.usc.imsc.R;
import edu.usc.imsc.SAX.handler.PlaceHandler;
import edu.usc.imsc.camera.CameraActivity;
import edu.usc.imsc.camera.ImageUploadActivity;
import edu.usc.imsc.events.Event;
import edu.usc.imsc.events.EventJsonParser;
import edu.usc.imsc.events.EventOverlay;
import edu.usc.imsc.events.EventOverlayItem;
import edu.usc.imsc.facebook.DataStorage;
import edu.usc.imsc.facebook.FacebookActivity;
import edu.usc.imsc.facebook.FacebookProfile;
import edu.usc.imsc.food.Food;
import edu.usc.imsc.food.FoodJsonParser;
import edu.usc.imsc.food.FoodOverlay;
import edu.usc.imsc.food.FoodOverlayItem;
import edu.usc.imsc.listener.BuildingOverlayListener;
import edu.usc.imsc.listener.EventOverlayListener;
import edu.usc.imsc.listener.ITrajectoryListener;
import edu.usc.imsc.services.Controller;
import edu.usc.imsc.services.MessengerService;
import edu.usc.imsc.services.SaveLocationIntentService;
import edu.usc.imsc.spatial.Building;
import edu.usc.imsc.spatial.BuildingOverlay;
import edu.usc.imsc.spatial.BuildingOverlayItem;
import edu.usc.imsc.spatial.Place;
import edu.usc.imsc.spatial.PlaceHolder;
import edu.usc.imsc.spatial.building.Buildings;
import edu.usc.imsc.spatial.building.BuildingsOverlay;
import edu.usc.imsc.spatial.building.BuildingsOverlayItem;
import edu.usc.imsc.trajectory.TrajectoryApp;
import edu.usc.imsc.trajectory.TrajectoryPath;
import edu.usc.imsc.trajectory.TrajectoryPathOverlay;
import edu.usc.imsc.trajectory.TrajectoryStopOverlay;
import edu.usc.imsc.trams.app.RouteName;
import edu.usc.imsc.trams.app.RouteOverlay;
import edu.usc.imsc.trams.app.TramApp;
import edu.usc.imsc.trams.app.TramRoute;
import edu.usc.imsc.trams.app.TramXMLParser;
import edu.usc.imsc.trams.stops.TramStop;
import edu.usc.imsc.trams.stops.TramStopOverlay;
import edu.usc.imsc.trams.vehicles.TramVehicle;
import edu.usc.imsc.trams.vehicles.TramVehicleOverlay;
import edu.usc.imsc.trucks.Truck;
import edu.usc.imsc.trucks.TruckOverlay;
import edu.usc.imsc.trucks.TruckXMLParser;
import edu.usc.imsc.util.Tools;
import edu.usc.imsc.util.Tools.QueryType;

public class MapBrowserActivity extends MapActivity implements
		BuildingOverlayListener, ITrajectoryListener, EventOverlayListener,
		OnZoomListener, OnClickListener, OnTouchListener {
	private EditText selectedLocationField;
	private String locationName;
	private int locationId;
	private boolean selectedLocation;

	private boolean selectedEvent;
	private String eventTitle;
	private int eventId;

	public static MapView map;

	private BuildingOverlay buildingOverlay;
	private EventOverlay eventOverlay;
	public static FoodOverlay foodOverlay;

	static private BuildingsOverlay parksOverlay;
	static private BuildingsOverlay pharmacyOverlay;
	private static BuildingsOverlay public_transportsOverlay;

	private List<Overlay> mapOverlays;

	private TruckOverlay truckOverlay;
	private TramVehicleOverlay tramVehicleOverlay;
	public static MyLocationOverlay myLocationOverlay;

	// private TramStopOverlay tramStopOverlay;
	private Map<Integer, TramStopOverlay> tramStopOverlayAllRoutes; // Map<routeId,
																	// TramStop>
	private Map<Integer, RouteOverlay> tramRouteOverlayAllRoutes; // Map<routeId,
																	// List<Route>>

	private TramXMLParser tramXMLParser;
	private TruckXMLParser truckXMLParser;

	HttpClient httpClient = AndroidHttpClient.newInstance("Android-iCampus");

	private Handler handler;
	static private Context context;

	private Drawable[] tramStopDrawables = new Drawable[5];
	private Timer tramTimer;
	private Timer foodtruckTimer;
	// public Timer saveLocationTimer;

	// hientt
	boolean isTramSelected = false;
	boolean isTruckSelected = false;
	boolean isBuildingSelected = false;
	boolean isEventSelected = false;
	boolean isMyLocationSelected = true;
	boolean isTrajectorySelected = false;
	static boolean isFoodSelected = false;

	protected String[] layerSelections = new String[Tools.LAYER_NUMBER];
	protected boolean[] isLayerSelections = new boolean[Tools.LAYER_NUMBER];

	private static final int DIALOG_YES_NO_MESSAGE = 1;
	private static final int DIALOG_YES_NO_LONG_MESSAGE = 2;
	private static final int DIALOG_LIST = 3;
	private static final int DIALOG_PROGRESS = 4;
	private static final int DIALOG_SINGLE_CHOICE = 5;
	private static final int DIALOG_MULTIPLE_CHOICE = 6;
	private static final int DIALOG_TEXT_ENTRY = 7;
	private static final int DIALOG_MULTIPLE_CHOICE_CURSOR = 8;

	private static final int MAP_LAYER_REQUEST_CODE = 1;
	private static final int SETTINGS_REQUEST_CODE = 2;
	protected final static int FOOD_TABS_REQUEST_CODE = 6;

	private static final int DIALOG_WAITING = 9;

	private static boolean[] mapLayersSelected = { false, false };
	public static SharedPreferences preferences;
	public static SharedPreferences sharedPreferences;

	// private static HashMap<String, Building> mapNameLocation;
	ProgressDialog progressDialog;
	List<Event> events = null;
	static boolean isEventHasData = false; // Do not need to retrieve the event
											// data again when they are in the
											// phone.

	public static Button fb_button = null;
	public static Button food_button = null;
	public static ArrayList<String> listBuildings = null;
	AutoCompleteTextView buildingTextView = null;
	ImageView buildingSearchImageView = null;

	public static GeoPoint lastPoint;
	public static boolean newSession = true;
	public static long period = 6 * 1000;

	public static TrajectoryPath trajectoryPath;
	private TrajectoryStopOverlay trajectoryStopOverlay;
	private TrajectoryPathOverlay trajectoryPathOverlay;

	public static boolean isSpatialQuery = false;
	public static QueryType queryType = QueryType.Rectangle;
	public static Vector<Double> tappedPoints = new Vector<Double>();
	public static List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();

	ZoomButtonsController zoomButtonController = null;
	ZoomControls zoomControls = null;
	ZoomButton zoomButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.map);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.map_title);

		OnClickListener searchImageListener = new OnClickListener() {
			public void onClick(View v) {
				buildingTextView.getText().clear();
				buildingTextView.setVisibility((buildingTextView
						.getVisibility() == View.VISIBLE) ? View.INVISIBLE
						: View.VISIBLE);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInputFromInputMethod(
						buildingTextView.getWindowToken(), 1);
				// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			}
		};

		buildingSearchImageView = (ImageView) findViewById(R.id.map_title_search);
		buildingSearchImageView.setOnClickListener(searchImageListener);

		this.locationId = -1;
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		context = this;
		tramXMLParser = new TramXMLParser(context);
		handler = new Handler();
		tramStopDrawables[0] = getResources().getDrawable(
				R.drawable.default_tram_stop32);
		tramStopDrawables[1] = getResources().getDrawable(R.drawable.stop_a_1);
		tramStopDrawables[2] = getResources().getDrawable(R.drawable.stop_pc);
		tramStopDrawables[3] = getResources().getDrawable(R.drawable.stop_a_1); // HACK,
																				// we
																				// want
																				// a
																				// unique
																				// drawable
		tramStopDrawables[4] = getResources().getDrawable(R.drawable.stop_pc); // HACK,
																				// we
																				// want
																				// a
																				// unique
																				// drawable
		this.setupMapView();

		// found on
		// http://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled

		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Yout GPS seems to be disabled, do you want to enable it?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(
										@SuppressWarnings("unused") final DialogInterface dialog,
										@SuppressWarnings("unused") final int id) {
									startActivity(new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS));
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(
										final DialogInterface dialog,
										@SuppressWarnings("unused") final int id) {
									dialog.cancel();
								}
							});
			final AlertDialog alert = builder.create();
			alert.show();
		}
		// end found on
		// http://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled

		// Check login should be done before other operation: e.g trajectory
		fb_button = (Button) findViewById(R.id.FacebookButton);
		SharedPreferences settings = getSharedPreferences(
				Tools.PREFERENCE_FILE, MODE_PRIVATE);
		if (settings.getBoolean("isLogin", false)) {
			Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " login "
					+ settings.getBoolean("isLogin", false));
			DataStorage.setLoggedIn(true);
			fb_button.setVisibility(View.VISIBLE);
			FacebookProfile fb_profile = new FacebookProfile(
					settings.getString("id", ""),
					settings.getString("name", ""));
			fb_profile.setEmail(settings.getString("email", ""));
			fb_profile.setFirstName(settings.getString("firstname", ""));
			fb_profile.setLastName(settings.getString("lastname", ""));
			DataStorage.setMe(fb_profile);
		}

		food_button = (Button) findViewById(R.id.FoodTabsButton);
		
		sharedPreferences = getPreferences(MODE_APPEND);
		if (sharedPreferences.getBoolean("USC buildings", false)) {
			// this.setupBuildings();
			new BuildingSetupTask().execute(this);
			isBuildingSelected = true;
			isLayerSelections[0] = true;
		}
		if (sharedPreferences.getBoolean("USC events", false)) {
			isEventSelected = true;
			// this.setupEvents();
			new EventSetupTask(this).execute(this);
			isLayerSelections[1] = true;
		}
		if (sharedPreferences.getBoolean("Trams", false)) {
			isTramSelected = true;
			this.setupTramApp();
			isLayerSelections[2] = true;
		}
		if (sharedPreferences.getBoolean("Trucks", false)) {
			isTruckSelected = true;
			this.setupTramApp();
			isLayerSelections[3] = true;
		}

		if (sharedPreferences.getBoolean("Foods", false)) {
			this.setupFoods();
			isFoodSelected = true;
			this.isLayerSelections[4] = true;
			food_button.setVisibility(View.VISIBLE);
		}

		/*
		if (sharedPreferences.getBoolean("Trajectory", false)) {
			this.setupTrajectory();
			isTrajectorySelected = true;
			this.isLayerSelections[5] = true;
		}
		*/

		Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " building "
				+ sharedPreferences.getBoolean("USC buildings", false));
		Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " event "
				+ sharedPreferences.getBoolean("USC events", false));
		Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " trams "
				+ sharedPreferences.getBoolean("Trams", false));
		Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " trucks "
				+ sharedPreferences.getBoolean("Trucks", false));
		Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " foods "
				+ sharedPreferences.getBoolean("Foods", false));
//		Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " trajectory "
//				+ sharedPreferences.getBoolean("Trajectory", false));

		tramTimer = new Timer();
		foodtruckTimer = new Timer();

		// Search building
		if (listBuildings == null) {
			setupBuildingSearch();
		}

		// Service to save location

		// startService(new Intent(this, SaveLocationIntentService.class));

		// lastPoint = new GeoPoint(34020209, -118285686);
		// TimerTask locationTimerTask = new SaveLocationTimerTask();
		// timer = new Timer();
		// timer.scheduleAtFixedRate(locationTimerTask, 1000, period);
	}

	private void setupTrajectory() {
		Log.i(Tools.TAG, MapBrowserActivity.class.getName()
				+ ": Setup Trajectory");
		isTrajectorySelected = true;
		if (isTrajectorySelected) {

			TrajectoryApp trajectoryApp = TrajectoryApp.getInstance(context);
			setupTrajectoryStop(trajectoryApp);
			setupTrajectoryPath(trajectoryApp);
		}
	}

	private void setupTrajectoryPath(TrajectoryApp trajectoryApp) {
		// TODO Auto-generated method stub
		trajectoryPath = trajectoryApp.getTrajectoryPath();
		if (trajectoryPath.getCoords().size() >= 2) {
			List<GeoPoint> coords = trajectoryPath.getCoords();
			drawPath(coords, Color.BLUE);// R.color.solid_red);//android.R.color.darker_gray);
		} else {
			Toast.makeText(getApplicationContext(),
					"You don't have trajectory", Toast.LENGTH_LONG).show();
		}
	}

	private void setupTrajectoryStop(TrajectoryApp trajectoryApp) {
		Drawable trajectoryStopDrawable = getResources().getDrawable(
				R.drawable.stop_16);
		trajectoryPath = trajectoryApp.getTrajectoryPath();

		if (trajectoryPath.getCoords().size() >= 2) {
			trajectoryStopOverlay = new TrajectoryStopOverlay(
					trajectoryStopDrawable, this);

			for (int routeid : trajectoryPath.getStops().keySet()) {
				trajectoryStopOverlay.addOverlay(trajectoryPath.getStops().get(
						routeid));
			}

			trajectoryStopOverlay.addTrajectoryListener(this);

			map.getOverlays().add(trajectoryStopOverlay);
			map.invalidate();
		} else {

		}
	}

	private void drawPath(List<GeoPoint> geoPoints, int color) {
		trajectoryPathOverlay = new TrajectoryPathOverlay(geoPoints, color);
		map.getOverlays().add(trajectoryPathOverlay);
		map.invalidate();
	}

	private void setupBuildingSearch() {
		// TODO Auto-generated method stub
		listBuildings = new ArrayList<String>();

		SAXParser parser;
		PlaceHandler handler = null;
		if (PlaceHolder.getInstance().getPlaces().isEmpty()) {
			try {
				parser = SAXParserFactory.newInstance().newSAXParser();
				handler = new PlaceHandler();
				InputStream is = this.getResources().openRawResource(
						R.raw.uscbuildings_new);
				parser.parse(is, handler);
				PlaceHolder.getInstance().setPlaces(handler.getPlaces());
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (Building place : PlaceHolder.getInstance().getPlaces()) {
			if (place.getName() != null)
				listBuildings.add(place.getName().trim());
			if (place.getShortName() != null)
				listBuildings.add(place.getShortName().trim());
			// if (place.getDepartmentName() != null)
			// listBuildings.add(place.getDepartmentName().trim());
			// listBuildings.add(place.getAddress());
			// listBuildings.add(place.getDepartmentLocation());
		}

		buildingTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_map);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, listBuildings);
		buildingTextView.setAdapter(adapter);

		buildingTextView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					Object test = parent.getAdapter().getItem(position);
					if (adapter.getItem(position) != null) {
						Log.i(Tools.TAG, MapBrowserActivity.class.getName()
								+ " : " + adapter.getItem(position));
						Building building = null;

						for (Building place : PlaceHolder.getInstance()
								.getPlaces()) {
							if (place.getName().trim()
									.equals(adapter.getItem(position))) {
								building = place;
								break;
							}
							if (place.getShortName().trim()
									.equals(adapter.getItem(position))) {
								building = place;
								break;
							}
						}

						// navigate to the building
						if (building.getLocation() != null) {

							map.getController().animateTo(
									building.getLocation());
							BuildingOverlayItem item = new BuildingOverlayItem(
									building);

							// Setup builing mark overlay
							Drawable buildingMark = context.getResources()
									.getDrawable(R.drawable.yellowpin);
							buildingMark.setBounds(0, 0,
									buildingMark.getIntrinsicWidth(),
									buildingMark.getIntrinsicHeight());
							BuildingOverlay overlay = new BuildingOverlay(
									buildingMark, map, context);
							// overlay.addBuildingOverlayListener();
							overlay.addOverlay(item);
							map.getOverlays().add(overlay);
							map.invalidate();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				buildingTextView.setVisibility(View.INVISIBLE);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(buildingTextView.getWindowToken(),
						0);

			}
		});
	}

	private void setupMapView() {
		map = (MapView) findViewById(R.id.mapview);
		map.setBuiltInZoomControls(true);
		zoomToUSC();
		mapOverlays = map.getOverlays();

		layerSelections[0] = "USC buildings";
		layerSelections[1] = "USC events";
		layerSelections[2] = "Trams";
		layerSelections[3] = "Food trucks";
		layerSelections[4] = "Foods";
//		layerSelections[5] = "Trajectory";

		myLocationOverlay = new MyLocationOverlay(this, map);
		mapOverlays.add(myLocationOverlay);

		zoomButtonController = map.getZoomButtonsController();
		zoomButtonController.setOnZoomListener(this);
		map.setOnTouchListener(this);
	}

	public static void setupBuildings(List<Buildings> buildings, String type) {
		// TODO Auto-generated method stub
		if (map.getZoomLevel() < Tools.ZOOM_LEVEL_THRESHOLD)
			return;

		Drawable drawable = null;

		if (type == "pharmacy") {
			drawable = context.getResources().getDrawable(
					R.drawable.pharmacy_64);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());

			if (pharmacyOverlay != null) {
				map.getOverlays().remove(pharmacyOverlay);
				pharmacyOverlay.clear();
			}

			pharmacyOverlay = new BuildingsOverlay(drawable, map, context);

			if (buildings != null) {

				for (Buildings building : buildings) {
					Place place = new Place(Integer.valueOf(String
							.valueOf(building.getId())), building.getName(),
							Double.valueOf(building.getLat()),
							Double.valueOf(building.getLon()));

					BuildingsOverlayItem oi = new BuildingsOverlayItem(place);
					pharmacyOverlay.addOverlay(oi);
				}

				map.getOverlays().add(pharmacyOverlay);
				map.invalidate();
			}
		} else if (type == "park") {
			drawable = context.getResources().getDrawable(R.drawable.park_64);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());

			if (parksOverlay != null) {
				map.getOverlays().remove(parksOverlay);
				parksOverlay.clear();
			}

			parksOverlay = new BuildingsOverlay(drawable, map, context);

			if (buildings != null) {

				for (Buildings building : buildings) {
					Place place = new Place(Integer.valueOf(String
							.valueOf(building.getId())), building.getName(),
							Double.valueOf(building.getLat()),
							Double.valueOf(building.getLon()));

					BuildingsOverlayItem oi = new BuildingsOverlayItem(place);
					parksOverlay.addOverlay(oi);
				}

				map.getOverlays().add(parksOverlay);
				map.invalidate();
			}
		} else if (type == "public_transport") {
			drawable = context.getResources().getDrawable(
					R.drawable.public_transport_64);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());

			if (public_transportsOverlay != null) {
				map.getOverlays().remove(public_transportsOverlay);
				public_transportsOverlay.clear();
			}

			public_transportsOverlay = new BuildingsOverlay(drawable, map,
					context);

			if (buildings != null) {

				for (Buildings building : buildings) {
					Place place = new Place(Integer.valueOf(String
							.valueOf(building.getId())), building.getName(),
							Double.valueOf(building.getLat()),
							Double.valueOf(building.getLon()));

					BuildingsOverlayItem oi = new BuildingsOverlayItem(place);
					public_transportsOverlay.addOverlay(oi);
				}

				map.getOverlays().add(public_transportsOverlay);
				map.invalidate();
			}
		}

	}

	public void setupFoods() {
		if (isFoodSelected == false)
			return;

		// We will not display food if zoom level is small
		if (map.getZoomLevel() < Tools.ZOOM_LEVEL_THRESHOLD)
			return;

		isFoodSelected = true;
		Drawable drawable = this.getResources().getDrawable(R.drawable.food_32);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());

		if (foodOverlay != null) {
			map.getOverlays().remove(foodOverlay);
			foodOverlay.clear();
		}

		foodOverlay = new FoodOverlay(drawable, map, this);
		foodOverlay.addBuildingOverlayListener(this);

		FoodJsonParser jsonParser = new FoodJsonParser(this);

		Vector<Double> screenCoords = new Vector<Double>();

		// Get screen coords
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		GeoPoint p1 = map.getProjection().fromPixels(0, 0);

		GeoPoint p2 = map.getProjection().fromPixels(width, height);
		screenCoords.add(p1.getLatitudeE6() / 1E6);
		screenCoords.add(p1.getLongitudeE6() / 1E6);
		screenCoords.add(p2.getLatitudeE6() / 1E6);
		screenCoords.add(p2.getLongitudeE6() / 1E6);

		List<Food> foods = jsonParser.parseFoods(jsonParser
				.retrieveStream(Tools
						.getRectangleSpatialQueryService(screenCoords)));

		if (foods != null) {
			for (Food food : foods) {
				Place place = new Place(Integer.valueOf(String.valueOf(food
						.getPid())), food.getTitle(), Double.valueOf(food
						.getLat()), Double.valueOf(food.getLon()));
				// Log.d("Place", String.valueOf(place.getLongitude()));
				// Log.d("Place", String.valueOf(place.getLatitude()));
				FoodOverlayItem oi = new FoodOverlayItem(place);
				oi.setFood(food);
				foodOverlay.addOverlay(oi);
				if (place.getId() == locationId) {
					foodOverlay.setSelectedItem(oi);
					selectedLocation = true;
					locationName = foodOverlay.getSelectedPlaceName();
				}
			}

			map.getOverlays().add(foodOverlay);
			map.invalidate();
		}
	}

	public void searchSpatialFood(Tools.QueryType spatialQueryType) {
		Drawable drawable = this.getResources().getDrawable(R.drawable.food_32);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());

		isFoodSelected = false;
		isLayerSelections[1] = false;

		if (foodOverlay != null) {
			map.getOverlays().remove(foodOverlay);
			foodOverlay.clear();
		}

		foodOverlay = new FoodOverlay(drawable, map, this);
		foodOverlay.addBuildingOverlayListener(this);
		FoodJsonParser jsonParser = new FoodJsonParser(this);

		List<Food> foods = null;

		switch (spatialQueryType) {
		case Rectangle:
			foods = jsonParser.parseFoods(jsonParser.retrieveStream(Tools
					.getRectangleSpatialQueryService(tappedPoints)));
			break;
		case Polygon:
			Log.i(Tools.TAG,
					MapBrowserActivity.class.getName() + " : "
							+ Tools.getPolygonSpatialQueryService(geoPoints));
			foods = jsonParser.parseFoods(jsonParser.retrieveStream(Tools
					.getPolygonSpatialQueryService(geoPoints)));
			break;
		case Circle:
			foods = jsonParser.parseFoods(jsonParser.retrieveStream(Tools
					.getCircleSpatialQueryService(geoPoints)));
			break;
		case KNN:
			Log.i(Tools.TAG,
					MapBrowserActivity.class.getName() + " : "
							+ Tools.getKNNSpatialQueryService(tappedPoints));
			foods = jsonParser.parseFoods(jsonParser.retrieveStream(Tools
					.getKNNSpatialQueryService(tappedPoints)));
			break;
		}

		if (foods != null) {
			Log.i(Tools.TAG, MapBrowserActivity.class.getName()
					+ " Food size: " + String.valueOf(foods.size()));

			for (Food food : foods) {
				Place place = new Place(Integer.valueOf(String.valueOf(food
						.getPid())), food.getTitle(), Double.valueOf(food
						.getLat()), Double.valueOf(food.getLon()));
				// Log.d("Place", String.valueOf(place.getLongitude()));
				// Log.d("Place", String.valueOf(place.getLatitude()));
				FoodOverlayItem oi = new FoodOverlayItem(place);
				foodOverlay.addOverlay(oi);
				if (place.getId() == locationId) {
					foodOverlay.setSelectedItem(oi);
					selectedLocation = true;
					locationName = foodOverlay.getSelectedPlaceName();
				}
			}

			map.getOverlays().add(foodOverlay);
			map.invalidate();

			MapBrowserActivity.isSpatialQuery = false;
			if (tappedPoints != null)
				MapBrowserActivity.tappedPoints.clear();
			if (geoPoints != null)
				MapBrowserActivity.geoPoints.clear();
		}
	}

	/**
	 * Display USC's building
	 */
	private void setupBuildings() {
		isBuildingSelected = true;
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.buildings32);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		if (buildingOverlay == null) {
			buildingOverlay = new BuildingOverlay(drawable, map, this);
			buildingOverlay.addBuildingOverlayListener(this);
			try {
				if (PlaceHolder.getInstance().getPlaces().isEmpty()) {
					SAXParser parser = SAXParserFactory.newInstance()
							.newSAXParser();
					PlaceHandler handler = new PlaceHandler();
					InputStream is = this.getResources().openRawResource(
							R.raw.uscbuildings_new);
					parser.parse(is, handler);
					PlaceHolder.getInstance().setPlaces(handler.getPlaces());
				}
				for (Building place : PlaceHolder.getInstance().getPlaces()) {
					BuildingOverlayItem oi = new BuildingOverlayItem(place);
					buildingOverlay.addOverlay(oi);
					if (place.getId() == locationId) {
						buildingOverlay.setSelectedItem(oi);
						selectedLocation = true;
						locationName = buildingOverlay.getSelectedPlaceName();
					}
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		map.getOverlays().add(buildingOverlay);
		map.invalidate();
	}

	// display all TramStopsOverlays & RouteOverlays
	private void setupTramApp() {
		if (isTramSelected) {
			TramApp tramApp = TramApp.getInstance(context);
			setupTramStopForAllRoutes(tramApp);
			setupTramRouteLinesForAllTramRoutes(tramApp);
		}
	}

	private void setupEvents() {
		isEventSelected = true;
		Drawable drawable = this.getResources().getDrawable(R.drawable.event32);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());

		if (!isEventHasData) {
			EventJsonParser jsonParser = new EventJsonParser(this);
			events = jsonParser.parseUSCEvent(jsonParser
					.retrieveStream(getPersonalizedEventCategoryURL()));

			// get eventinfo
			if (events == null) {
				return;
			}
		}

		if (eventOverlay == null) {
			isEventHasData = true;
			eventOverlay = new EventOverlay(drawable, map, this);
			eventOverlay.addEventOverlayListener(this);
			Iterator<Event> it = events.iterator();
			while (it.hasNext()) {
				Event event = (Event) it.next();
				int lat = (int) (Float.valueOf(event.getLat()) * 1E6);
				int lon = (int) (Float.valueOf(event.getLon()) * 1E6);
				GeoPoint point = new GeoPoint(lat, lon);

				EventOverlayItem oi = new EventOverlayItem(point, event);
				eventOverlay.addOverlay(oi);

				if (Integer.valueOf(event.getId()) == eventId) {
					eventOverlay.setSelectedItem(oi);
					selectedEvent = true;
					eventTitle = eventOverlay.getSelectedEventTitle();
				}
			}
		}
		map.getOverlays().add(eventOverlay);
		map.invalidate();
	}

	/**
	 * display all TramStopOverlay
	 * 
	 * @param tramApp
	 */
	private void setupTramStopForAllRoutes(TramApp tramApp) {
		Drawable tramStopDrawable;
		tramStopOverlayAllRoutes = new HashMap<Integer, TramStopOverlay>();

		// draw out all the stops
		for (TramRoute route : tramApp.getSupportedRoutes().values()) {
			RouteName routeEnum = RouteName.getRouteEnumById(route.getId());
			if (routeEnum == RouteName.ROUTEA)
				tramStopDrawable = this.tramStopDrawables[0];
			else if (routeEnum == RouteName.ROUTEB)
				tramStopDrawable = this.tramStopDrawables[1];
			else if (routeEnum == RouteName.ROUTEC)
				tramStopDrawable = this.tramStopDrawables[2];
			else if (routeEnum == RouteName.PARKINGCENTER)
				tramStopDrawable = this.tramStopDrawables[3];
			else
				tramStopDrawable = this.tramStopDrawables[4];

			TramStopOverlay tramStopOverlay = new TramStopOverlay(
					tramStopDrawable, context);

			for (TramStop stop : route.getStops().values()) {
				tramStopOverlay.addOverlay(stop);
			}

			tramStopOverlayAllRoutes.put(route.getId(), tramStopOverlay);
		}

		// we must need this when users choose favorite trams before enable
		// trams
		if (isRoutePersonalized()) {
			String personalizedRoute = getPersonalizedRoute().trim();
			for (int routeid : tramStopOverlayAllRoutes.keySet()) {
				if (routeid == Integer.parseInt(personalizedRoute)) {
					mapOverlays.add(tramStopOverlayAllRoutes.get(routeid));
					break;
				}
			}
		} else {
			mapOverlays.addAll(tramStopOverlayAllRoutes.values());
		}
	}

	/**
	 * display all RouteOverlay
	 * 
	 * @param tramApp
	 */
	private void setupTramRouteLinesForAllTramRoutes(TramApp tramApp) {
		tramRouteOverlayAllRoutes = new HashMap<Integer, RouteOverlay>();
		for (TramRoute route : tramApp.getSupportedRoutes().values()) {
			RouteName routeEnum = RouteName.getRouteEnumById(route.getId());
			List<GeoPoint> coords = route.getCoords();
			if (routeEnum == RouteName.ROUTEA) {
				drawPath(coords, Color.BLACK, route.getId());// R.color.solid_red);//android.R.color.darker_gray);
			} else if (routeEnum == RouteName.ROUTEB) {
				drawPath(coords, Color.BLUE, route.getId());
			} else if (routeEnum == RouteName.ROUTEC) {
				drawPath(coords, Color.RED, route.getId());
			} else if (routeEnum == RouteName.PARKINGCENTER) {
				drawPath(coords, Color.BLACK, route.getId());// R.color.solid_green);//android.R.color.background_light);
			}
		}

		if (isRoutePersonalized()) {
			String personalizedRoute = getPersonalizedRoute().trim();
			for (int routeid : tramRouteOverlayAllRoutes.keySet()) {
				if (routeid == Integer.parseInt(personalizedRoute)) {

					// if the map does not contain routeid -> add routeid
					if (!mapOverlays.contains(tramRouteOverlayAllRoutes
							.get(routeid))) {
						mapOverlays.add(tramRouteOverlayAllRoutes.get(routeid));
					}
				} else {
					RouteOverlay routeOverlay = tramRouteOverlayAllRoutes
							.get(routeid);
					mapOverlays.remove(routeOverlay);
				}
			}
		} else { // add back all tram route lines if not present
			for (int routeid : tramRouteOverlayAllRoutes.keySet()) {
				if (!mapOverlays.contains(tramRouteOverlayAllRoutes
						.get(routeid))) {
					mapOverlays.add(tramRouteOverlayAllRoutes.get(routeid));
				}
			}
		}

	}

	/**
	 * draw lines between point
	 * 
	 * @param geoPoints
	 * @param color
	 * @param routeid
	 */
	private void drawPath(List<GeoPoint> geoPoints, int color, int routeid) {
		RouteOverlay routeOverlay = new RouteOverlay(geoPoints, color);
		tramRouteOverlayAllRoutes.put(routeid, routeOverlay);
		mapOverlays.add(routeOverlay);
		map.invalidate();
	}

	/**
	 * personalized update
	 */
	private void updateTramApp() {
		Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " updateTramApp");
		if (isTramSelected) {
			TramApp tramApp = TramApp.getInstance(context);
			updateTramStops(tramApp);
			updateTramRouteLines(tramApp);
		}
	}

	private void updateEvents() {
		if (isEventSelected) {
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.event32);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());

			// get eventinfo
			Log.i(Tools.TAG, "Update events");
			EventJsonParser jsonParser = new EventJsonParser(this);

			events = jsonParser.parseUSCEvent(jsonParser
					.retrieveStream(getPersonalizedEventCategoryURL()));

			if (events == null) {
				return;
			}

			if (eventOverlay != null) {
				map.getOverlays().remove(eventOverlay);
				eventOverlay.clear();
			}

			eventOverlay = new EventOverlay(drawable, map, this);
			eventOverlay.addEventOverlayListener(this);

			Iterator<Event> it = events.iterator();
			while (it.hasNext()) {
				Event event = (Event) it.next();
				int lat = (int) (Float.valueOf(event.getLat()) * 1E6);
				int lon = (int) (Float.valueOf(event.getLon()) * 1E6);
				GeoPoint point = new GeoPoint(lat, lon);

				EventOverlayItem oi = new EventOverlayItem(point, event);
				eventOverlay.addOverlay(oi);

				if (Integer.valueOf(event.getId()) == eventId) {
					eventOverlay.setSelectedItem(oi);
					selectedEvent = true;
					eventTitle = eventOverlay.getSelectedEventTitle();
				}
			}
			map.getOverlays().add(eventOverlay);
			map.invalidate();
		}
	}

	private class BuildingSetupTask extends
			AsyncTask<MapBrowserActivity, Void, Void> {

		private ProgressDialog Dialog = new ProgressDialog(context);

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			Dialog.setMessage("Please wait ...");
			Dialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (Dialog.isShowing()) {
					Dialog.dismiss();
				}
				// do your Display and data setting operation here
			} catch (Exception e) {

			}

			if (buildingOverlay != null) {
				map.getOverlays().add(buildingOverlay);
				map.invalidate();
			}
		}

		@Override
		protected Void doInBackground(MapBrowserActivity... params) {
			// TODO Auto-generated method stub
			isBuildingSelected = true;
			Drawable drawable = context.getResources().getDrawable(
					R.drawable.buildings32);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			if (buildingOverlay == null) {
				buildingOverlay = new BuildingOverlay(drawable, map, context);
				buildingOverlay.addBuildingOverlayListener(params[0]);
				try {
					if (PlaceHolder.getInstance().getPlaces().isEmpty()) {
						SAXParser parser = SAXParserFactory.newInstance()
								.newSAXParser();
						PlaceHandler handler = new PlaceHandler();
						InputStream is = context.getResources()
								.openRawResource(R.raw.uscbuildings_new);
						parser.parse(is, handler);
						PlaceHolder.getInstance()
								.setPlaces(handler.getPlaces());
					}
					for (Building place : PlaceHolder.getInstance().getPlaces()) {
						BuildingOverlayItem oi = new BuildingOverlayItem(place);
						buildingOverlay.addOverlay(oi);
						if (place.getId() == locationId) {
							buildingOverlay.setSelectedItem(oi);
							selectedLocation = true;
							locationName = buildingOverlay
									.getSelectedPlaceName();
						}
					}
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	private class EventSetupTask extends
			AsyncTask<MapBrowserActivity, Void, Void> {

		private ProgressDialog Dialog = new ProgressDialog(context);

		EventSetupTask(Context ctx) {
			Dialog.setCancelable(true);
			Dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					cancel(true);

				}
			});
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			Dialog.setMessage("Please wait ...");
			Dialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (Dialog.isShowing()) {
					Dialog.dismiss();
				}
				// do your Display and data setting operation here
			} catch (Exception e) {

			}

			if (events != null && eventOverlay != null
					&& !map.getOverlays().contains(eventOverlay)) {
				isEventSelected = true;
				isEventHasData = true;
				map.getOverlays().add(eventOverlay);
				map.invalidate();
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			Log.i(Tools.TAG, MapBrowserActivity.class.getName()
					+ " onCancelled");
			if (eventOverlay != null
					&& map.getOverlays().contains(eventOverlay)) {
				map.getOverlays().remove(eventOverlay);
				eventOverlay.clear();
				eventOverlay = null;
				isEventSelected = false;
				isEventHasData = false;
				events.clear();
				events = null;
			}
		}

		@Override
		protected Void doInBackground(MapBrowserActivity... params) {
			// TODO Auto-generated method stub
			// isEventSelected = true;
			Drawable drawable = context.getResources().getDrawable(
					R.drawable.event32);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());

			if (!isEventHasData) {
				EventJsonParser jsonParser = new EventJsonParser(context);
				events = jsonParser.parseUSCEvent(jsonParser
						.retrieveStream(getPersonalizedEventCategoryURL()));

				// get eventinfo
				if (events == null) {
					return null;
				}
			}

			if (eventOverlay == null) {
				// isEventHasData = true;
				eventOverlay = new EventOverlay(drawable, map, context);
				eventOverlay.addEventOverlayListener(params[0]);
				Iterator<Event> it = events.iterator();
				while (it.hasNext()) {
					Event event = (Event) it.next();
					int lat = (int) (Float.valueOf(event.getLat()) * 1E6);
					int lon = (int) (Float.valueOf(event.getLon()) * 1E6);
					GeoPoint point = new GeoPoint(lat, lon);

					EventOverlayItem oi = new EventOverlayItem(point, event);
					eventOverlay.addOverlay(oi);

					if (Integer.valueOf(event.getId()) == eventId) {
						eventOverlay.setSelectedItem(oi);
						selectedEvent = true;
						eventTitle = eventOverlay.getSelectedEventTitle();
					}
				}
			}

			return null;
		}
	}

	private class EventUpdateTask extends
			AsyncTask<MapBrowserActivity, Void, Void> {

		private ProgressDialog Dialog = new ProgressDialog(context);

		EventUpdateTask(Context cxt) {
			Dialog.setCancelable(true);
			Dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					cancel(true);
				}
			});
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			Dialog.setMessage("Please wait ...");
			Dialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (Dialog.isShowing()) {
					Dialog.dismiss();
				}
				// do your Display and data setting operation here
			} catch (Exception e) {

			}

			if (events != null && eventOverlay != null
					&& !map.getOverlays().contains(eventOverlay)
					&& isEventSelected) {
				isEventHasData = true;
				map.getOverlays().add(eventOverlay);
				map.invalidate();
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			Log.i(Tools.TAG, MapBrowserActivity.class.getName()
					+ " onCancelled");
			if (eventOverlay != null
					&& map.getOverlays().contains(eventOverlay)) {
				map.getOverlays().remove(eventOverlay);
				eventOverlay.clear();
				eventOverlay = null;
				isEventHasData = false;
				events.clear();
				events = null;
			}
		}

		@Override
		protected Void doInBackground(MapBrowserActivity... params) {
			// TODO Auto-generated method stub
			if (isEventSelected) {
				Drawable drawable = context.getResources().getDrawable(
						R.drawable.event32);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());

				// get eventinfo
				Log.i(Tools.TAG, EventUpdateTask.class.getName()
						+ " get eventinfo");
				EventJsonParser jsonParser = new EventJsonParser(context);

				events = jsonParser.parseUSCEvent(jsonParser
						.retrieveStream(getPersonalizedEventCategoryURL()));

				if (events == null) {
					return null;
				}

				if (eventOverlay != null) {
					map.getOverlays().remove(eventOverlay);
					eventOverlay.clear();
				}

				eventOverlay = new EventOverlay(drawable, map, context);
				eventOverlay.addEventOverlayListener(params[0]);

				Iterator<Event> it = events.iterator();
				while (it.hasNext()) {
					Event event = (Event) it.next();
					int lat = (int) (Float.valueOf(event.getLat()) * 1E6);
					int lon = (int) (Float.valueOf(event.getLon()) * 1E6);
					GeoPoint point = new GeoPoint(lat, lon);

					EventOverlayItem oi = new EventOverlayItem(point, event);
					eventOverlay.addOverlay(oi);

					if (Integer.valueOf(event.getId()) == eventId) {
						eventOverlay.setSelectedItem(oi);
						selectedEvent = true;
						eventTitle = eventOverlay.getSelectedEventTitle();
					}
				}
			}

			return null;
		}
	}

	private void updateTramStops(TramApp tramApp) {
		Log.i(Tools.TAG, MapBrowserActivity.class.getName()
				+ " updateTramStops");
		if (tramStopOverlayAllRoutes == null) {
			tramStopOverlayAllRoutes = new HashMap<Integer, TramStopOverlay>();
		}

		// clear tram stop overlay, not for UI but for memory
		for (TramStopOverlay tramStopOverlay : tramStopOverlayAllRoutes
				.values()) {
			tramStopOverlay.clear();
		}

		// update tram stops according to personalization
		for (TramRoute route : tramApp.getSupportedRoutes().values()) {
			TramStopOverlay tramStopOverlay = tramStopOverlayAllRoutes
					.get(route.getId());
			if (tramStopOverlay == null) {
				Log.e(Tools.TAG, "on redrawing the tramstop overlay, route "
						+ route.getId() + " is not supported.");
				continue;
			}

			if (isRoutePersonalized()) { // personalized
				String personalizedRoute = getPersonalizedRoute().trim();
				if (route.getId() == Integer.parseInt(personalizedRoute)) {
					for (TramStop stop : route.getStops().values()) {
						tramStopOverlay.addOverlay(stop);
					}
					if (!mapOverlays.contains(tramStopOverlay)) {
						mapOverlays.add(tramStopOverlay);
					}
				} else {
					mapOverlays.remove(tramStopOverlay);
				}
			} else { // display all
				for (TramStop stop : route.getStops().values()) {
					tramStopOverlay.addOverlay(stop);
				}
				if (!mapOverlays.contains(tramStopOverlay)) {
					mapOverlays.add(tramStopOverlay);
				}
			}
		}

	}

	private void updateTramRouteLines(TramApp tramApp) {
		Log.i(Tools.TAG, MapBrowserActivity.class.getName()
				+ " updateTramRouteLines");
		if (tramRouteOverlayAllRoutes == null) {
			tramRouteOverlayAllRoutes = new HashMap<Integer, RouteOverlay>();
		}

		// update tram route lines according to personalization
		if (isRoutePersonalized()) {
			String personalizedRoute = getPersonalizedRoute().trim();
			for (int routeid : tramRouteOverlayAllRoutes.keySet()) {
				if (routeid == Integer.parseInt(personalizedRoute)) {

					// if the map does not contain routeid -> add routeid
					if (!mapOverlays.contains(tramRouteOverlayAllRoutes
							.get(routeid))) {
						mapOverlays.add(tramRouteOverlayAllRoutes.get(routeid));
					}
				} else {
					RouteOverlay routeOverlay = tramRouteOverlayAllRoutes
							.get(routeid);
					mapOverlays.remove(routeOverlay);
				}
			}
		} else { // add back all tram route lines if not present
			for (int routeid : tramRouteOverlayAllRoutes.keySet()) {
				if (!mapOverlays.contains(tramRouteOverlayAllRoutes
						.get(routeid))) {
					mapOverlays.add(tramRouteOverlayAllRoutes.get(routeid));
				}
			}
		}
	}

	/**
	 * update vehicle overlays by timer
	 */
	public void updateVehicleOverlays() {
		Log.i(Tools.TAG, MapBrowserActivity.class.getName()
				+ " updateVehicleOverlays");
		TramApp tramApp = TramApp.getInstance(context);

		// always create new TramVehicle
		if (tramVehicleOverlay != null) {
			tramVehicleOverlay.clear();
		} else {
			Drawable tramDrawable = getResources().getDrawable(
					R.drawable.tram32);
			tramVehicleOverlay = new TramVehicleOverlay(tramDrawable, context);
		}

		// add back all vehicles
		for (TramRoute route : tramApp.getSupportedRoutes().values()) {
			// if personalized, only add vehicles from the personalized route
			if (isRoutePersonalized()) {
				String personalizedRoute = getPersonalizedRoute().trim();
				if (route.getId() == Integer.parseInt(personalizedRoute)) {
					for (TramVehicle vehicle : route.getVehicles().values()) {
						Log.i(Tools.TAG,
								MapBrowserActivity.class.getName()
										+ " personalized: updateVehicleOverlays : found tram vehicle while refreshing overlay "
										+ vehicle.toString());
						tramVehicleOverlay.addOverlay(vehicle);
					}
					break; // assuming only one personalized route
				}
			} else {
				for (TramVehicle vehicle : route.getVehicles().values()) {
					Log.i(Tools.TAG,
							"found tram vehicle while refreshing overlay "
									+ vehicle.toString());
					tramVehicleOverlay.addOverlay(vehicle);
				}
			}
		}
		if (tramVehicleOverlay.size() > 0) {
			Log.i(Tools.TAG, MapBrowserActivity.class.getName() + ": "
					+ tramVehicleOverlay.size() + " tram vehicles are found.");
			mapOverlays.add(tramVehicleOverlay);
		} else {
			Log.i(Tools.TAG, MapBrowserActivity.class.getName()
					+ " updateVehicleOverlays: tram vehicle overlay is empty");
		}
		map.invalidate();
	}

	/**
	 * 
	 */
	private void setupTruckApp() {
		isTruckSelected = true;
		Drawable truckDrawable = getResources().getDrawable(
				R.drawable.foodtruck32);
		truckOverlay = new TruckOverlay(truckDrawable, this);
		InputStream is = this.getResources().openRawResource(
				R.raw.tweet_example);
		truckXMLParser = new TruckXMLParser(this);
		Map<Integer, Truck> foodTrucks = truckXMLParser.parseXMLFile(is);

		Log.i(Tools.TAG, "iCampus " + foodTrucks.size() + " foodtrucks found.");
		for (Truck truck : foodTrucks.values()) {
			truckOverlay.addOverlay(truck);
			Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " adding "
					+ truck.toString());
		}

		mapOverlays.add(truckOverlay);
	}

	/**
	 * 
	 * @param trucks
	 */
	private void updateTruckOverlay(Map<Integer, Truck> trucks) {
		Log.i(Tools.TAG, "updating truck overlay.");
		this.truckOverlay.clear();
		for (Truck truck : trucks.values()) {
			truckOverlay.addOverlay(truck);
		}
		map.invalidate();
	}

	private class TramTimerTask extends TimerTask {
		@Override
		public void run() {
			handler.post(new Runnable() {
				public void run() {
					if (isTramSelected) {
						new TramDataFetchTask().execute();
					}
				}
			});
		}
	}

	private class TruckTimerTask extends TimerTask {
		@Override
		public void run() {
			handler.post(new Runnable() {
				public void run() {
					if (isTruckSelected) {
						new TruckDataFetchTask().execute();
					}
				}
			});
		}
	}

	/**
	 * Event handler for zooming to USC campus on the map
	 * 
	 * @param v
	 *            the view calling this as an event handler
	 */
	public void zoomToUSC(View v) {
		zoomToUSC();
	}

	protected void zoomToUSC() {
		// GeoPoint gp = new GeoPoint((int) (34.0214587868681 * 1E6),
		// (int) (-118.28657890219 * 1E6));
		// MapController mapController = map.getController();
		// mapController.zoomToSpan((int) 0.01E6, (int) 0.01E6);
		// mapController.setCenter(gp);

		MapController mapController = map.getController();

		// Start animating the map towards the given point
		if (myLocationOverlay != null) {
			GeoPoint current = myLocationOverlay.getMyLocation();
			if (current == null) { // if we don't have a location yet
				Log.i(Tools.TAG, MapBrowserActivity.class.getName()
						+ "Trying to zoom to a null current location.");
				GeoPoint gp = new GeoPoint(Tools.USC_POINT_LON,
						Tools.USC_POINT_LAT);
				mapController.zoomToSpan(Tools.MAP_ZOOM_TO_SPAN_LON,
						Tools.MAP_ZOOM_TO_SPAN_LAT);
				mapController.setCenter(gp);
			} else {
				mapController.animateTo(current);
				mapController.setZoom(19);
			}
		} else {
			GeoPoint gp = new GeoPoint(Tools.USC_POINT_LON, Tools.USC_POINT_LAT);
			mapController.zoomToSpan(Tools.MAP_ZOOM_TO_SPAN_LON,
					Tools.MAP_ZOOM_TO_SPAN_LAT);
			mapController.setCenter(gp);
		}
	}

	private void show_event() {
		Intent intent = new Intent(this, EndlessEventActivity.class);

		if (isEventCategoryPersonalized()) {
			// intent.putExtra("category", getPersonalizedEventCategory());
			intent.putExtra("url", getPersonalizedEventCategoryURL());
			// intent.putExtra("user", "imsc1@gmail.com");
		} else {
			// intent.putExtra("category", "All");
			intent.putExtra("url", Tools.EVENT_ICAMPUS_URL);
			// intent.putExtra("user", "imsc1@gmail.com");
		}
		this.startActivity(intent);
	}

	private void show_spatial_search() {
		showDialog(DIALOG_LIST);
	}

	/**
	 * show a list of layer types, ex Street view, Sattelite
	 */
	private void show_google_map_layers() {
		/* Display a list of checkboxes */
		showDialog(DIALOG_MULTIPLE_CHOICE);
	}

	private void show_camera() {
		// TODO Auto-generated method stub
		if (DataStorage.isLoggedIn()) {
			// Intent i = new Intent().setClass(this,
			// ImageUploadActivity.class);
			Intent i = new Intent().setClass(this, CameraActivity.class);
			startActivity(i);
		} else {
			new AlertDialog.Builder(this)
					.setMessage("You need login to view status!")
					.setTitle("Status").setCancelable(true).show();
		}
	}

	private void show_info() {
		Intent intent = new Intent().setClass(this, SocialActivity.class);
		this.startActivity(intent);
	}

	private void show_settings() {
		Intent intent = new Intent().setClass(this, SettingsActivity.class);
		this.startActivityForResult(intent, SETTINGS_REQUEST_CODE);
	}

	/*
	 * private void show_preference() { // Launch Preference activity Intent i =
	 * new Intent(this, PreferencesActivity.class); startActivity(i); }
	 */

	private void show_about() {
		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
	}

	@Override
	public void onPlaceSelectionChange() {
		selectedLocation = true;
		locationName = buildingOverlay.getSelectedPlaceName();
		locationId = buildingOverlay.getSelectedPlaceId();
	}

	@Override
	public void onEventSelectionChange() {
		selectedEvent = true;
		eventTitle = eventOverlay.getSelectedEventTitle();
		eventId = eventOverlay.getSelectedPlaceId();
	}

	public void onOkClick(View v) {

	}

	public void onCancelClick(View v) {

	}

	@Override
	public void onBackPressed() {
		showDialog(DIALOG_YES_NO_MESSAGE);
	}

	public void locationSelectionFieldOnClick(View v) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.my_location:
			Log.i(Tools.TAG, "my location menu click.");
			if (isMyLocationSelected) {
				myLocationOverlay.disableMyLocation();
				map.getOverlays().remove(myLocationOverlay);
				isMyLocationSelected = false;
			} else {
				myLocationOverlay.enableMyLocation();
				map.getOverlays().add(myLocationOverlay);
				isMyLocationSelected = true;
			}
			break;
//		case R.id.friends:
//			Log.i(Tools.TAG, "iCampus-Facebook"
//					+ "before starting facebook activity!");
//			startActivity(new Intent(this, FacebookActivity.class));
//			Log.i(Tools.TAG, "iCampus-Facebook"
//					+ "after starting facebook activity!");
//			break;

//		case R.id.camera:
//			show_camera();
//			break;
		case R.id.social:
			show_info();
			break;
		case R.id.settings:
			show_settings();
			break;
//		case R.id.search:
//			show_spatial_search();
//			break;
//		case R.id.event:
//			show_event();
//			break;
		case R.id.about:
			show_about();
			break;
		case R.id.layers:
			show_google_map_layers();
			break;
		case R.id.exit:
			Log.i(Tools.TAG, "iCampus-clicked on exit!");
			System.exit(0);
			// finish(); //this does not stop timer tasks
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * this method decides which layers are displayed
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case MAP_LAYER_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				// get the new layer selections
				layerSelections = data.getExtras().getStringArray(
						Tools.MAP_LAYER_PARAM);
				isLayerSelections = data.getExtras().getBooleanArray(
						Tools.MAP_LAYER_VALUE);

				map.getOverlays().clear();
				map.invalidate();

				// add back the myLocationOverlay which shouldn't be removed in
				// the first place
				if (isMyLocationSelected) {
					map.getOverlays().add(myLocationOverlay);
				}

				isTramSelected = false;
				isBuildingSelected = false;
				isEventSelected = false;
				isTruckSelected = false;
				isFoodSelected = false;
				isTrajectorySelected = false;
				sharedPreferences = getPreferences(MODE_APPEND);
				SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
				prefsEditor.putBoolean("USC buildings", false);
				prefsEditor.putBoolean("Trams", false);
				prefsEditor.putBoolean("USC events", false);
				prefsEditor.putBoolean("Trucks", false);
				prefsEditor.putBoolean("Trajectory", false);
				prefsEditor.putBoolean("Foods", false);
				food_button.setVisibility(View.INVISIBLE);

				for (int i = 0; i < layerSelections.length; i++) {
					if (isLayerSelections[i]) {
						if (layerSelections[i].equals("USC buildings")) {
							if (isBuildingSelected == false) {
								// this.setupBuildings();
								new BuildingSetupTask().execute(this);
							}

							prefsEditor.putBoolean("USC buildings", true);
						} else if (layerSelections[i].equals("USC events")) {
							if (layerSelections[i].equals("USC events")) {
								if (isEventSelected == false) {
									// this.setupEvents();
									new EventSetupTask(this).execute(this);
								}
							}

							prefsEditor.putBoolean("USC events", true);
						} else if (layerSelections[i].equals("Trams")) {
							// only redraw TramOverlays if the overlays have not
							// been drawn yet
							if (isTramSelected == false) {
								isTramSelected = true;
								this.setupTramApp();
							}

							prefsEditor.putBoolean("Trams", true);
						} else if (layerSelections[i].equals("Vendors")) {
						} else if (layerSelections[i].equals("Restrooms")) {
						} else if (layerSelections[i].equals("Food trucks")) {
							if (isTruckSelected == false) {
								isTruckSelected = true;
								this.setupTruckApp();
							}

							prefsEditor.putBoolean("Trucks", true);
						} else if (layerSelections[i].equals("Foods")) {
							if (layerSelections[i].equals("Foods")) {
								if (isFoodSelected == false) {
									this.setupFoods();
									isFoodSelected = true;
									food_button.setVisibility(View.VISIBLE);
								}
							}
							prefsEditor.putBoolean("Foods", true);
						} else if (layerSelections[i].equals("Trajectory")) {
							if (layerSelections[i].equals("Trajectory")) {
								if (isTrajectorySelected == false) {
									this.setupTrajectory();
								}
							}
							prefsEditor.putBoolean("Trajectory", true);
						}
					}
				}
				prefsEditor.commit();
				// progressDialog.cancel();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.i(Tools.TAG, MapBrowserActivity.class.getName()
						+ " RESULT_CANCELED");
			}
			Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " :layers: "
					+ layerSelections.toString());
			// decide which layers to display
			break;

		case SETTINGS_REQUEST_CODE:

			if (SettingsActivity.isChangeSettings) {
				// update GUI
				this.updateTramApp();
				// this.updateEvents();
				new EventUpdateTask(this).execute(this);

				// update personalized data in db
				this.savePersonalizedEvent();
				this.savePersonalizedTramRoute();

				boolean tracking = preferences.getBoolean(
						"enable_trajectory_pref", false);
				if (tracking) {
					Log.i(Tools.TAG, MapBrowserActivity.class.getName()
							+ " Trajectory enabled");
					startService(new Intent(this,
							SaveLocationIntentService.class));
				} else {
					stopService(new Intent(this,
							SaveLocationIntentService.class));
				}

				SettingsActivity.isChangeSettings = false;
			}
			break;
		case FOOD_TABS_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				myLocationOverlay.disableMyLocation();
				Log.i(Tools.TAG, MapBrowserActivity.class.getName()
						+ " FOOD_TABS_ACTIVITY_RESULT");
				String pid = "";
				String title = "";
				String time = "";
				String rating = "";
				String place = "";
				String smallphoto = "";
				String comment = "";
				String lat = "";
				String lon = "";
				if (data.getExtras().getString("food_pid") != null)
					pid = data.getExtras().getString("food_pid");
				if (data.getExtras().getString("food_title") != null)
					title = data.getExtras().getString("food_title");
				if (data.getExtras().getString("food_lat") != null)
					lat = data.getExtras().getString("food_lat");
				if (data.getExtras().getString("food_lon") != null)
					lon = data.getExtras().getString("food_lon");
				if (data.getExtras().getString("food_rating") != null)
					rating = data.getExtras().getString("food_rating");
				if (data.getExtras().getString("food_place") != null)
					place = data.getExtras().getString("food_place");
				if (data.getExtras().getString("food_smallphoto") != null)
					smallphoto = data.getExtras().getString("food_smallphoto");
				if (data.getExtras().getString("food_comment") != null)
					comment = data.getExtras().getString("food_comment");

				Place pl = new Place(Integer.valueOf(pid), title,
						Double.valueOf(lat), Double.valueOf(lon));
				Food food = new Food(time, title, rating, place, comment,
						smallphoto, lon, lat);

				FoodOverlayItem item = new FoodOverlayItem(pl);
				item.setFood(food);

				// Setup food mark overlay
				Drawable foodMark = context.getResources().getDrawable(
						R.drawable.food_64);
				foodMark.setBounds(0, 0, foodMark.getIntrinsicWidth(),
						foodMark.getIntrinsicHeight());
				FoodOverlay overlay = new FoodOverlay(foodMark, map, context);
				overlay.addOverlay(item);
				Log.i(Tools.TAG,
						MapBrowserActivity.class.getName()
								+ " lat "
								+ String.valueOf((int) (Double.valueOf(lat) * 1E6))
								+ ","
								+ String.valueOf((int) (Double.valueOf(lon) * 1E6)));

				map.getController().animateTo(
						new GeoPoint((int) (Double.valueOf(lat) * 1E6),
								(int) (Double.valueOf(lon) * 1E6)));
				map.getOverlays().add(overlay);
				map.invalidate();
			}
			break;

		default:
			break;

		}
	}

	/**
	 * display MapLayers dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_MULTIPLE_CHOICE:

			return new AlertDialog.Builder(MapBrowserActivity.this)
					.setIcon(R.drawable.ic_popup_layers)
					.setTitle(R.string.dialog_multi_choice)
					.setMultiChoiceItems(R.array.mapLayerTypes,
							mapLayersSelected,
							new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton, boolean isChecked) {

									/* User clicked on a check box do some stuff */
									mapLayersSelected[whichButton] = isChecked;
								}
							})
					.setPositiveButton(R.string.dialog_multi_choice_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked Yes so do some stuff */
									// map.setStreetView(mapLayersSelected[0]);
									map.setSatellite(mapLayersSelected[0]);
									map.setTraffic(mapLayersSelected[1]);
								}
							})
					.setNegativeButton(R.string.dialog_multi_choice_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked No so do some stuff */
									// do nothing!
								}
							}).create();
		case DIALOG_LIST:
			return new AlertDialog.Builder(MapBrowserActivity.this)
					.setTitle("Select a query type")
					.setItems(Tools.queryTypes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									Toast.makeText(getApplicationContext(),
											Tools.queryTypes[item],
											Toast.LENGTH_SHORT).show();
									isSpatialQuery = true;
									queryType = QueryType
											.valueOf((String) Tools.queryTypes[item]);
								}
							}).create();
		case DIALOG_YES_NO_MESSAGE:
			return new AlertDialog.Builder(MapBrowserActivity.this)
					.setTitle("Do you really want to quit?")
					.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									System.exit(0);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									// User clicked Cancel so do some stuff
									System.out.println("cancel clicked.");
								}
							}).create();
			// case DIALOG_WAITING: {
			// ProgressDialog dialog = new ProgressDialog(this);
			// dialog.setMessage("Please wait while loading...");
			// dialog.setIndeterminate(true);
			// dialog.setCancelable(true);
			// dialog.dismiss();
			// return dialog;
			// }
		}
		return null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(Tools.TAG, MapBrowserActivity.class.getName()
				+ " Map activity on pause");
		tramTimer.cancel();
		// saveLocationTimer.cancel();
		foodtruckTimer.cancel();

		if (isMyLocationSelected) {
			myLocationOverlay.disableMyLocation();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(Tools.TAG, MapBrowserActivity.class.getName()
				+ " Map activity onResume");
		tramTimer = new Timer();
		// saveLocationTimer = new Timer();
		foodtruckTimer = new Timer();
		// saveLocationTimer = new Timer();

		resumeTramTimer();
		resumeFoodtruckTimer();

		if (isMyLocationSelected) {
			myLocationOverlay.enableMyLocation();
			myLocationOverlay.runOnFirstFix(new Runnable() {
				public void run() {
					MapController controller = map.getController();
					GeoPoint geoPoint = myLocationOverlay.getMyLocation();
					controller.animateTo(geoPoint);
				}
			});
		}
	}

	private void resumeTramTimer() {
		TimerTask tramTimerTask = new TramTimerTask();
		tramTimer.scheduleAtFixedRate(tramTimerTask, 1000, 10000);
	}

	private void resumeFoodtruckTimer() {
		TimerTask truckTimerTask = new TruckTimerTask();
		foodtruckTimer.scheduleAtFixedRate(truckTimerTask, 1000, 60000);
	}

	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}

	/**
	 * This class asynchronously get updated data from Tram Service and
	 * 
	 * @author infolab
	 * 
	 */
	private class TramDataFetchTask extends
			AsyncTask<String, Void, Map<Integer, TramRoute>> {

		InputStream responseStream;

		@Override
		protected Map<Integer, TramRoute> doInBackground(String... params) {

			// Personalization
			String tramURL = getPersonalizedRouteURL();
			Log.e(Tools.TAG, TramDataFetchTask.class.getName()
					+ " : doInBackground TRAM URL on refreshing = " + tramURL);
			HttpGet httpGet = new HttpGet(tramURL);
			try {
				responseStream = httpClient.execute(httpGet).getEntity()
						.getContent();
				Map<Integer, TramRoute> newlyParsedTramRoutes = tramXMLParser
						.parseXMLFile(responseStream);
				return newlyParsedTramRoutes;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		// update TramOverlays in post execution
		protected void onPostExecute(Map<Integer, TramRoute> tramRoutes) {
			if (responseStream != null) {
				Log.i(Tools.TAG, MapBrowserActivity.class.getName()
						+ " :TRAM_RESPONSE: Finished parsing tram response!");
				try {
					responseStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				TramApp.getInstance(context).update(tramRoutes);

				updateVehicleOverlays();
			}

		}
	}

	private class TruckDataFetchTask extends
			AsyncTask<String, Void, Map<Integer, Truck>> {

		InputStream responseStream;

		@Override
		protected Map<Integer, Truck> doInBackground(String... params) {
			String url = String.format(Tools.FOOD_TRUCK_URL_FORMAT_STRING,
					Tools.kMinLat, Tools.kMinLong, Tools.kMaxLat,
					Tools.kMaxLong);
			Log.i(Tools.TAG, "FOOD_TRUCK_URL" + url);
			HttpGet httpGet = new HttpGet(String.format(
					Tools.FOOD_TRUCK_URL_FORMAT_STRING, Tools.kMinLat,
					Tools.kMinLong, Tools.kMaxLat, Tools.kMaxLong));
			try {
				responseStream = httpClient.execute(httpGet).getEntity()
						.getContent();
				// Log.e(Tools.TAG,
				// Tools.convertStreamToString(responseStream));
				Log.e(Tools.TAG, "before entering parseXMLFile for trucks");
				Map<Integer, Truck> foodTrucks = truckXMLParser
						.parseXMLFile(responseStream);
				final int numFoodTrucks = foodTrucks.size();
				handler.post(new Runnable() {
					@Override
					public void run() {
						// String toastStr = "iCampus: no" +
						// " foodtrucks found.";
						if (numFoodTrucks > 0) {
							String toastStr = numFoodTrucks
									+ " foodtrucks found.";
							Toast.makeText(context, toastStr,
									Toast.LENGTH_SHORT).show();
						}
					}
				});

				return foodTrucks;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Map<Integer, Truck> trucks) {
			if (responseStream != null) {
				Log.i(Tools.TAG, "TRUCK_RESPNONSE:"
						+ "Finish parsing foodtruck feeds.");
				try {
					responseStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (trucks != null && trucks.size() > 0) {
					updateTruckOverlay(trucks);
				}

			}
		}
	}

	private class SaveLocationTimerTask extends TimerTask {
		@Override
		public void run() {
			handler.post(new Runnable() {
				public void run() {
					try {
						GeoPoint currentPoint = MapBrowserActivity.myLocationOverlay
								.getMyLocation();
						// if (Tools
						// .CalculationByDistance(currentPoint, lastPoint) > 10)
						// {
						if (DataStorage.isLoggedIn()) {
							Log.i("SaveLocation: ", Tools.getSaveLocation(
									DataStorage.getMe().getId(), currentPoint,
									newSession));
							Tools.callService(Tools.getSaveLocation(DataStorage
									.getMe().getId(), lastPoint, newSession));
						}
						lastPoint = currentPoint;
						// }
					} catch (Exception e) {
					}
				}
			});
		}
	}

	protected boolean isRoutePersonalized() {
		return preferences.getBoolean("enable_tram_route_pref", false);
	}

	// not used yet
	protected boolean setRoutePersonalized() {
		return preferences.getBoolean("enable_tram_route_pref", true);
	}

	protected String getPersonalizedRouteURL() {
		if (isRoutePersonalized()) {
			String route_id = String.valueOf(preferences.getString(
					"favorite_tram_route", "unknown"));
			Log.i(Tools.TAG, MapBrowserActivity.class.getName()
					+ " personalized_route_id " + route_id);

			// if user does log in
			if (DataStorage.isLoggedIn() && DataStorage.getMe() != null) {

				Log.i(Tools.TAG,
						MapBrowserActivity.class.getName()
								+ " personalized_route_url "
								+ Tools.getPersonalizedTram(DataStorage.getMe()
										.getId() != null ? DataStorage.getMe()
										.getId() : ""));
				return Tools
						.getPersonalizedTram(DataStorage.getMe().getId() != null ? DataStorage
								.getMe().getId() : "");

			}
			return Tools.getSpecificTram(route_id);
		}

		// default = return all trams
		return Tools.TRAMS_URL_STRING;
	}

	protected String getPersonalizedRoute() {
		if (preferences.getBoolean("enable_tram_route_pref", false)) {
			Log.e(Tools.TAG,
					MapBrowserActivity.class.getName()
							+ " personalized_route "
							+ preferences.getString("favorite_tram_route",
									"unknown"));
			String routeid = String.valueOf(preferences.getString(
					"favorite_tram_route", "938"));
			return routeid;
		}
		return "all";
	}

	protected boolean isEventCategoryPersonalized() {
		return preferences.getBoolean("enable_event_pref", false);
	}

	// not used yet
	public void setEventCategoryPersonalized() {
		SharedPreferences.Editor prefsEditor = preferences.edit();
		prefsEditor.putBoolean("enable_event_pref", true);
	}

	protected String getPersonalizedEventCategoryURL() {
		Log.i(Tools.TAG, MapBrowserActivity.class.getName()
				+ " getPersonalizedEventCategoryURL");
		if (isEventCategoryPersonalized()) {
			String eventCategory = String.valueOf(preferences.getString(
					"event_category_pref", "unknown"));
			Log.i(Tools.TAG, MapBrowserActivity.class.getName()
					+ " event_category_pref " + eventCategory);

			// if user don't log in
			if (DataStorage.isLoggedIn() && DataStorage.getMe() != null) {
				Log.i(Tools.TAG,
						MapBrowserActivity.class.getName()
								+ " PersonalizedEventCategoryURL "
								+ Tools.getPersonalizedEvent(DataStorage
										.getMe().getId() != null ? DataStorage
										.getMe().getId() : ""));
				return Tools
						.getPersonalizedEvent(DataStorage.getMe().getId() != null ? DataStorage
								.getMe().getId() : "");
			}
			return Tools.getPersonalizedEventCategory(eventCategory);
		} else
			return Tools.EVENT_ICAMPUS_URL;
	}

	public void savePersonalizedEvent() {

		Log.i(Tools.TAG, MapBrowserActivity.class.getName()
				+ " savePersonalizedEvent");
		String category = getPersonalizedEventCategory();

		// Save personalized data to db
		if (DataStorage.isLoggedIn() && DataStorage.getMe() != null
				&& DataStorage.getMe().getId() != null) {
			String[] arr = getResources().getStringArray(
					R.array.eventCategoriesValues);
			String allCategories = "";
			for (int i = 0; i < arr.length; i++) {
				allCategories += arr[i];
				if (i != arr.length - 1)
					allCategories += ",";
			}
			Tools.callService(Tools.getUserPreferenceURL(DataStorage.getMe()
					.getId(), "events", allCategories, true)); // remove data
			Tools.callService(Tools.getUserPreferenceURL(DataStorage.getMe()
					.getId(), "events", category, false)); // add data
		}
	}

	public void savePersonalizedTramRoute() {
		Log.i(Tools.TAG, MapBrowserActivity.class.getName()
				+ " savePersonalizedTramRoute");
		String route_id = getPersonalizedRoute();

		// Save personalized data to db
		if (DataStorage.isLoggedIn() && DataStorage.getMe() != null
				&& DataStorage.getMe().getId() != null) {
			String[] arr = getResources().getStringArray(
					R.array.tramRoutesValues);
			String allRoutes = "";
			for (int i = 0; i < arr.length; i++) {
				allRoutes += arr[i];
				if (i != arr.length - 1)
					allRoutes += ",";
			}
			Tools.callService(Tools.getUserPreferenceURL(DataStorage.getMe()
					.getId(), "trams", allRoutes, true));
			Tools.callService(Tools.getUserPreferenceURL(DataStorage.getMe()
					.getId(), "trams", route_id, false));
		}
	}

	protected String getPersonalizedEventCategory() {
		if (isEventCategoryPersonalized()) {
			String eventCategory = String.valueOf(preferences.getString(
					"event_category_pref", "unknown"));
			Log.e(Tools.TAG,
					MapBrowserActivity.class.getName()
							+ " event_category_pref "
							+ preferences.getString("event_category_pref",
									"unknown"));
			return eventCategory;
		}
		return "all";
	}

	private boolean isNetworkConnected() {
		NetworkInfo activeNetworkInfo = null;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();// .getAllNetworkInfo();
			activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			if (activeNetworkInfo != null) {
				return activeNetworkInfo.isConnected();
			}
			// NetworkInfo networkInfo =
			// conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return activeNetworkInfo != null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return true;
	}

	/**
	 * showLayerList is an onClick action on the map's layer button see map.xml
	 * 
	 * @param v
	 */
	public void showLayerList(View v) {
		Intent layerSelection = new Intent(this, LayersSelectionActivity.class);
		layerSelection.putExtra(Tools.MAP_LAYER_PARAM, layerSelections);
		layerSelection.putExtra(Tools.MAP_LAYER_VALUE, isLayerSelections);
		this.startActivityForResult(layerSelection, MAP_LAYER_REQUEST_CODE);
	}

	/**
	 * @param v
	 */
	public void showFacebook(View v) {
		Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " showFacebook");
		this.startActivity(new Intent(this, FacebookActivity.class));
	}

	public void showFoodTabs(View v) {
		Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " showFoodTabs");
		Intent i = new Intent(this, FoodTabsActivity.class);
		this.startActivityForResult(i, FOOD_TABS_REQUEST_CODE);
	}

	@Override
	public void onVisibilityChanged(boolean visible) {

	}

	@Override
	public void onZoom(boolean isZoomIn) {
		// TODO Auto-generated method stub
		Log.i(Tools.TAG, "Zoom level: " + String.valueOf(map.getZoomLevel()));
		if (isZoomIn) {
			map.getController().zoomIn();
			setupFoods();
		} else {
			map.getController().zoomOut();
			setupFoods();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		setupFoods();
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// The user took their finger off the map,
			// they probably just moved it to a new place.
			setupFoods();
			break;
		case MotionEvent.ACTION_MOVE:
			// The user is probably moving the map.
			setupFoods();
			break;
		// case MotionEvent.ACTION_DOWN:
		// if (event.getEventTime() - lasttime < 1000) {
		// seekBar.setVisibility((seekBar.getVisibility() == View.VISIBLE) ?
		// View.INVISIBLE
		// : View.VISIBLE);
		// }
		// lasttime = event.getEventTime();
		// return true;
		}

		// Return false so that the map still moves.
		return false;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Log.i(Tools.TAG, MapBrowserActivity.class.getName() + " onClick");
	}

	public MapView getMap() {
		return map;
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (myLocationOverlay != null)
			myLocationOverlay.disableMyLocation();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// menu.removeItem(R.id.refresh_taste);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * zoom the map to the right level/view to show your current location and
	 * the farthest point
	 * 
	 * @param myPoint
	 * @param furthestPoint
	 */
	protected void zoomToShowPoints(GeoPoint myPoint, GeoPoint farthestPoint) {
		map.getController()
				.zoomToSpan(
						(farthestPoint.getLatitudeE6() > myPoint.getLatitudeE6() ? farthestPoint.getLatitudeE6()
								- myPoint.getLatitudeE6()
								: myPoint.getLatitudeE6()
										- farthestPoint.getLatitudeE6()),
						(farthestPoint.getLongitudeE6() > myPoint
								.getLongitudeE6() ? farthestPoint
								.getLongitudeE6() - myPoint.getLongitudeE6()
								: myPoint.getLongitudeE6()
										- farthestPoint.getLongitudeE6()));
		map.getController().animateTo(
				new GeoPoint(farthestPoint.getLatitudeE6()
						- ((farthestPoint.getLatitudeE6() - myPoint
								.getLatitudeE6()) / 2), farthestPoint
						.getLongitudeE6()
						- ((farthestPoint.getLongitudeE6() - myPoint
								.getLongitudeE6()) / 2)));
	}

	@Override
	public void onTrajectorySelectionChange() {
		// TODO Auto-generated method stub

	}

	// public void onProgressChanged(SeekBar seekBar, int progress,
	// boolean fromTouch) {
	//
	// }
	//
	// public void onStartTrackingTouch(SeekBar seekBar) {
	//
	// }
	//
	// public void onStopTrackingTouch(SeekBar seekBar) {
	//
	// }

	// @Override
	// public boolean onDoubleTap(MotionEvent e) {
	// TODO Auto-generated method stub
	// GeoPoint p = map.getProjection().fromPixels((int) e.getX(),
	// (int) e.getY());
	//
	// AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	// dialog.setTitle("Double Tap");
	// dialog.setMessage("Location: " + p.getLatitudeE6() + ", "
	// + p.getLongitudeE6());
	// dialog.show();

	// seekBar.setVisibility((seekBar.getVisibility() == View.VISIBLE) ?
	// View.INVISIBLE
	// : View.VISIBLE);

	// return true;
	// }

	// @Override
	// public boolean onDoubleTapEvent(MotionEvent e) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean onSingleTapConfirmed(MotionEvent e) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean onDown(MotionEvent e) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
	// float arg3) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public void onLongPress(MotionEvent arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
	// float arg3) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public void onShowPress(MotionEvent arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public boolean onSingleTapUp(MotionEvent arg0) {
	// // TODO Auto-generated method stub
	// return false;
	// }
}
