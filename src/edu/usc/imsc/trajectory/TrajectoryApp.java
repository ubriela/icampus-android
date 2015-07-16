package edu.usc.imsc.trajectory;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import edu.usc.imsc.activities.MapBrowserActivity;
import edu.usc.imsc.facebook.DataStorage;
import edu.usc.imsc.util.Tools;

public class TrajectoryApp {

	Context context;
	private static TrajectoryApp singleton;

	public TrajectoryApp(Context context) {
		super();
		this.context = context;
	}

	public static TrajectoryApp getInstance(Context context) {
		if (singleton == null) {
			singleton = new TrajectoryApp(context);
		}
		return singleton;
	}
	
	public TrajectoryPath getTrajectoryPath() {
		TrajectoryPath trajectoryPath = new TrajectoryPath();

		TrajectoryJsonParser trajectoryParser = new TrajectoryJsonParser(
				context);

		if (DataStorage.isLoggedIn()) {
			Log.d("Trajectory App: ", Tools
							.getTrajectoryURL(DataStorage.getMe().getId(), Integer.valueOf(MapBrowserActivity.preferences.getString("history_locations_pref", "10")),	//Tools.TRAJECTORY_POINTS_NUMBER
									Integer.valueOf(MapBrowserActivity.preferences.getString("distance_bwn_points_pref", "100"))));		//	Tools.TRAJECTORY_DISTANCE_BETWEEN_POINTS
			List<HistoryLocation> listLocation = trajectoryParser
					.parseHistoryLocation(Tools.retrieveStream(Tools
							.getTrajectoryURL(DataStorage.getMe().getId(), Integer.valueOf(MapBrowserActivity.preferences.getString("history_locations_pref", "10")),	//	Tools.TRAJECTORY_POINTS_NUMBER
									Integer.valueOf(MapBrowserActivity.preferences.getString("distance_bwn_points_pref", "100")))));		//	//	Tools.TRAJECTORY_DISTANCE_BETWEEN_POINTS
			
			if (listLocation != null && listLocation.size() >= 2) {
				Iterator<HistoryLocation> it = listLocation.iterator();
				while (it.hasNext()) {
					HistoryLocation historyLocation = (HistoryLocation) it
							.next();
					GeoPoint point = new GeoPoint(
							(int) (Double
									.valueOf(historyLocation.getLatitude()) * 1e6),
							(int) (Double.valueOf(historyLocation
									.getLongtitude()) * 1e6));
					trajectoryPath.getCoords().add(point);
					
					TrajectoryStop trajectoryStop = new TrajectoryStop(Integer.valueOf(historyLocation.getId()), historyLocation.getTime(), point);
					trajectoryPath.getStops().put(Integer.valueOf(historyLocation.getId()), trajectoryStop);
				}
			}
		}
		return trajectoryPath;
	}

}
