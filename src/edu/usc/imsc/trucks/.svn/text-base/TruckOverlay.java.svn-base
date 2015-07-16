package edu.usc.imsc.trucks;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;

public class TruckOverlay extends ItemizedOverlay<TruckOverlayItem> {

	private List<TruckOverlayItem> truckOverlayItems;
	private Context context;

	public TruckOverlay(Drawable defaultMarker, Context con) {
		super(boundCenterBottom(defaultMarker));

		truckOverlayItems = new ArrayList<TruckOverlayItem>();
		context = con;
	}

	public void addOverlay(Truck truck) {
		truckOverlayItems.add(new TruckOverlayItem(truck));
		populate();
	}

	public void clear() {
		truckOverlayItems.clear();
		populate();
	}

	@Override
	protected TruckOverlayItem createItem(int i) {
		return truckOverlayItems.get(i);
	}

	@Override
	public int size() {
		return truckOverlayItems.size();
	}

	@Override
	protected boolean onTap(int index) {
		TruckOverlayItem truckOverlayItem = truckOverlayItems.get(index);
		TruckDialog truckDialog = new TruckDialog(context,
				truckOverlayItem.truck);
		truckDialog.show();
		return true;
	}

}
