package edu.usc.imsc.trams.stops;

import com.google.android.maps.OverlayItem;

/**
 * @author infolab
 */
public class TramStopOverlayItem extends OverlayItem {

	/**
	 * @uml.property name="tramStop"
	 * @uml.associationEnd
	 */
	public TramStop tramStop;

	public TramStopOverlayItem(TramStop tramStop) {

		super(tramStop.getCoord(), tramStop.getName(), "Tram at stop in "
				+ tramStop.getMinutesToArrival() + " minutes.");
		// Log.i(Maptivity.TAG,
		// "Adding tramstop overlay item, name="+tramStop.getName() +
		// " arriving in "+ tramStop.getMinutesToArrival() + " minutes." +
		// " coordinates="+tramStop.getCoord().toString());
		this.tramStop = tramStop;
	}
}
