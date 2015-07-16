package edu.usc.imsc.trams.vehicles;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;

public class TramVehicleOverlay extends ItemizedOverlay<TramVehicleOverlayItem> {

	private List<TramVehicleOverlayItem> tramVehicleOverlayItems;
	private Context context;

	public TramVehicleOverlay(Drawable defaultMarker, Context con) {
		super(boundCenterBottom(defaultMarker));

		tramVehicleOverlayItems = new ArrayList<TramVehicleOverlayItem>();
		context = con;
	}

	public void addOverlay(TramVehicle tramVehicle) {
		tramVehicleOverlayItems.add(new TramVehicleOverlayItem(tramVehicle));
		populate();
	}

	public void clear() {
		tramVehicleOverlayItems.clear();
		populate();
	}

	@Override
	protected TramVehicleOverlayItem createItem(int i) {
		return tramVehicleOverlayItems.get(i);
	}

	@Override
	public int size() {
		return tramVehicleOverlayItems.size();
	}

	@Override
	protected boolean onTap(int index) {
		TramVehicleOverlayItem tramVehicleOverlayItem = tramVehicleOverlayItems
				.get(index);
		TramVehicleDialog tramVehicleDialog = new TramVehicleDialog(context,
				tramVehicleOverlayItem.tramVehicle);
		tramVehicleDialog.show();
		return true;
	}
}
