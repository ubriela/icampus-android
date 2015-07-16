package edu.usc.imsc.events;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


public class EventOverlayItem extends OverlayItem{
	
	private int id;
	private Event event; 
	
	public EventOverlayItem(GeoPoint point, Event event) {
		super(point, event.getTitle(), event.getBuilding_code());
		this.id = Integer.valueOf(event.getId());
		this.event = event;
	}

	public int getId() {
		return id;
	}

	public Event getEvent() {
		return event;
	}
}
