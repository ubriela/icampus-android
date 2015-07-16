package edu.usc.imsc.trucks;

import com.google.android.maps.OverlayItem;

/**
 * @author infolab
 */
public class TruckOverlayItem extends OverlayItem {

	/**
	 * @uml.property name="truck"
	 * @uml.associationEnd
	 */
	public Truck truck;

	public TruckOverlayItem(Truck truck) {
		super(truck.getLocation(), truck.getName(), truck.getTweet());
		this.truck = truck;
	}

}
