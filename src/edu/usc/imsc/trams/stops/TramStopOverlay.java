package edu.usc.imsc.trams.stops;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;

public class TramStopOverlay extends ItemizedOverlay<TramStopOverlayItem> {
	
	private List<TramStopOverlayItem> tramStopOverlayItems;
	private Context context;
	
	public TramStopOverlay(Drawable defaultMarker, Context con) {
		super(boundCenterBottom(defaultMarker));
		
		tramStopOverlayItems = new ArrayList<TramStopOverlayItem>();
		context = con;
	}
	
	public void addOverlay(TramStop tramStop) {
		tramStopOverlayItems.add(new TramStopOverlayItem(tramStop));
		populate();
	}
	
	public void clear() {
		tramStopOverlayItems.clear();
		populate();
	}

	@Override
	protected TramStopOverlayItem createItem(int i) {
		return tramStopOverlayItems.get(i);
	}

	@Override
	public int size() {
		return tramStopOverlayItems.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		TramStopOverlayItem tramStopOverlayItem = tramStopOverlayItems.get(index);
		TramStopDialog tramStopDialog = new TramStopDialog(context, tramStopOverlayItem.tramStop);
		Log.e("TramStopOverlay", "clicked on tramstop "+tramStopOverlayItem.tramStop.toString());
		tramStopDialog.show();
		return true;
	}
}
