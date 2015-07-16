package edu.usc.imsc.trams.vehicles;

import com.google.android.maps.OverlayItem;

/**
 * @author infolab
 */
public class TramVehicleOverlayItem extends OverlayItem {
	/**
	 * @uml.property name="tramVehicle"
	 * @uml.associationEnd
	 */
	public TramVehicle tramVehicle;

	public TramVehicleOverlayItem(TramVehicle tramVehicle) {
		super(tramVehicle.getCoord(), tramVehicle.getId() + "",
				"Moving around!");
		this.tramVehicle = tramVehicle;
	}
}
