package edu.usc.imsc.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import edu.usc.imsc.R;
import edu.usc.imsc.facebook.FacebookActivity;
import edu.usc.imsc.util.Tools;

/**
 * This class defines the basic requirements for an iTaste Activity that uses
 * Google Maps
 * 
 * @original author William Quach, Nga Chung
 * @author linghu
 */
public abstract class AbstractMapActivity extends MapActivity {
	protected final static int MAP_LAYER_REQUEST_CODE = 1;
	protected final static int STILL_IMAGE_CAMERA_REQUEST_CODE = 2;
	protected final static int VIDEO_CAMERA_REQUEST_CODE = 3;
	protected final static int IMAGE_PICK_REQUEST_CODE = 4;
	protected final static int SETTINGS_REQUEST_CODE = 5;

	protected MapView map;

	private MyLocationOverlay myLocationOverlay;
	protected static String[] layerSelections = new String[Tools.LAYER_NUMBER];
	protected static boolean[] isLayerSelections = new boolean[Tools.LAYER_NUMBER];

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		// hientt removed the center_usc on the menu
		// case R.id.center_usc:
		// zoomToUSC();
		// break;
		case R.id.my_location:
			Log.i(Tools.TAG, "my location menu click.");
			myLocationOverlay = new MyLocationOverlay(this, map);
			myLocationOverlay.enableMyLocation();
			myLocationOverlay.runOnFirstFix(new Runnable() {
				public void run() {
					MapController mapController = map.getController();
					GeoPoint current = myLocationOverlay.getMyLocation();
					if (current != null) {

						// Start animating the map towards the given point
						mapController.animateTo(current);
						mapController.setZoom(19);
					} else {
						Log.d(Tools.TAG, "current GPS reading is null!");
					}
				}
			});
			map.getOverlays().add(myLocationOverlay);
			map.invalidate();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (myLocationOverlay != null)
			myLocationOverlay.disableMyLocation();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MAP_LAYER_REQUEST_CODE: // resultCode is returned from
										// LayerSelectionListActivity class
			if (resultCode == Activity.RESULT_OK) {
				Log.d("************", "RESULT_OK");
				// get a list ofvisible layers returned from
				// LayerSelectionListActivity
				layerSelections = data.getExtras().getStringArray(
						Tools.MAP_LAYER_PARAM);
				isLayerSelections = data.getExtras().getBooleanArray(
						Tools.MAP_LAYER_VALUE);

				// save settings to preferences
				// preferences.
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.d("*************", "RESULT_CANCELED");
			}
			Log.d("**********", layerSelections.toString());
			// decide which layers to display ...
			break;
		case STILL_IMAGE_CAMERA_REQUEST_CODE:
			Intent i = new Intent(Intent.ACTION_PICK);
			i.setType("image/*");
			startActivityForResult(i, IMAGE_PICK_REQUEST_CODE);
			break;
		case IMAGE_PICK_REQUEST_CODE:
			Log.i("*********", "returned from photo picking...request="
					+ requestCode + " result=" + resultCode);
			if (resultCode == Activity.RESULT_CANCELED) {
				return;
			}
			Uri uri = data.getData();
			queryImageByUri(uri);
			break;
		default:

			break;

		}
	}

	private void queryImageByUri(Uri _uri) {
		// now we get the path to the image file
		Cursor cursor = getContentResolver()
				.query(_uri,
						new String[] {
								android.provider.MediaStore.Images.ImageColumns.DATA,
								android.provider.MediaStore.Images.ImageColumns.LATITUDE,
								android.provider.MediaStore.Images.ImageColumns.LONGITUDE,
								android.provider.MediaStore.Images.ImageColumns.DATE_TAKEN },
						null, null, null);
		cursor.moveToFirst();
		String file = cursor.getString(0);
		String lat = cursor.getString(1);
		String lng = cursor.getString(2);
		String ctime = Tools.getTime(Long.parseLong(cursor.getString(3)));
		cursor.close();
		Toast toast = Toast.makeText(this, file + " lat=" + lat + " lng=" + lng
				+ " date=" + ctime, Toast.LENGTH_LONG);
		toast.show();
		// show image
		// parent.setVisibility(View.GONE);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(_uri, "image/jpg");
		startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// menu.removeItem(R.id.refresh_taste);
		return super.onPrepareOptionsMenu(menu);
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
//		 GeoPoint gp = new GeoPoint((int) (34.0214587868681 * 1E6),
//		 (int) (-118.28657890219 * 1E6));
//		 MapController mapController = map.getController();
//		 mapController.zoomToSpan((int) 0.01E6, (int) 0.01E6);
//		 mapController.setCenter(gp);

		MapController mapController = map.getController();
		
		// Start animating the map towards the given point
		if (myLocationOverlay != null) {
			GeoPoint current = myLocationOverlay.getMyLocation();
			mapController.animateTo(current);
			mapController.setZoom(19);
		} else {
			GeoPoint gp = new GeoPoint(Tools.USC_POINT_LON, Tools.USC_POINT_LAT);
			mapController.zoomToSpan(Tools.MAP_ZOOM_TO_SPAN_LON,
					Tools.MAP_ZOOM_TO_SPAN_LAT);
			mapController.setCenter(gp);
		}
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

	/**
	 * showLayerList is an onClick action on the map's layer button see map.xml
	 * 
	 * @param v
	 */
	public void showLayerList(View v) {
		Intent layerSelection = new Intent(this, LayersSelectionActivity.class);
		layerSelection.putExtra(Tools.MAP_LAYER_PARAM, layerSelections);
		layerSelection.putExtra(Tools.MAP_LAYER_VALUE, isLayerSelections);
		this.startActivityForResult(layerSelection, MAP_LAYER_REQUEST_CODE); // look
																				// at
																				// onActivityResult()
																				// method
																				// to
																				// see
																				// how
																				// AbstractMapActivity
																				// display
																				// layers
	}
	
	/**
	 * showLayerList is an onClick action on the map's layer button see map.xml
	 * 
	 * @param v
	 */
	public void showFacebook(View v) {
		Log.d(Tools.TAG, "buttonFacebook");
		this.startActivity(new Intent(this, FacebookActivity.class)); 
	}
}
