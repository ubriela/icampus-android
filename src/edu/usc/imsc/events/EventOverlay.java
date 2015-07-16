package edu.usc.imsc.events;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

import edu.usc.imsc.listener.EventOverlayListener;

public class EventOverlay extends ItemizedOverlay<EventOverlayItem>{
	private List<EventOverlayItem> overlays;
	private EventOverlayItem chosenItem;
	private MapView map;
	private List<EventOverlayListener> listeners;
	private Drawable marker;
	private boolean isDisplay = false;
	private Context context;
	
	@Override
	protected EventOverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	public EventOverlay(Drawable defaultMarker, MapView map, Context con) {
		super(defaultMarker);
		overlays = new ArrayList<EventOverlayItem>();
		listeners = new LinkedList<EventOverlayListener>();
		this.map = map;
		this.marker = defaultMarker;
		context = con;
	}
	
	public void addEventOverlayListener(EventOverlayListener listener) {
		listeners.add(listener);
	}
	
	public void removeEventOverlayListener(EventOverlayListener listener) {
		listeners.remove(listener);
	}
	
	public String getSelectedEventTitle() {
		return chosenItem.getTitle();
	}
	
	public int getSelectedPlaceId() {
		return chosenItem.getId();
	}

	
	@Override
	protected boolean onTap(int index) {
		EventOverlayItem eventOverlayItem = overlays.get(index);
		EventDialog eventDialog = new EventDialog(context, eventOverlayItem.getEvent());
		Log.e("EventOverlay", "clicked on events " + eventOverlayItem.getTitle());
		eventDialog.show();
		return true;
	}
	
	private void notifyListeners() {
		for (EventOverlayListener l : listeners) {
			l.onEventSelectionChange();
		}
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(this.marker);
		drawInfoBox(canvas, mapView);
	}
	
	private void drawInfoBox(Canvas canvas, MapView mapView) {
		if (chosenItem != null) {
			Point screenLocation = mapView.getProjection().toPixels(
					chosenItem.getPoint(), null);

			int size = chosenItem.getSnippet().length() * 3 + 10;
			RectF infoBox = new RectF(screenLocation.x - size,
					screenLocation.y - 12, screenLocation.x + size,
					screenLocation.y + 12);

			Paint paint = new Paint();
			paint.setAlpha(0);
			paint.setColor(Color.GREEN);

			Paint borderPaint = new Paint();
			borderPaint = new Paint();
			borderPaint.setARGB(255, 255, 255, 255);
			borderPaint.setAntiAlias(true);
			borderPaint.setStyle(Style.STROKE);
			borderPaint.setStrokeWidth(2);

			canvas.drawRoundRect(infoBox, 5, 5, paint);
			canvas.drawRoundRect(infoBox, 5, 5, borderPaint);

			Paint fontColor = new Paint();
			fontColor.setColor(Color.WHITE);

			canvas.drawText(chosenItem.getSnippet(), screenLocation.x - size
					+ 8, screenLocation.y + 4, fontColor);
		}
	}
	
	public void addOverlay(EventOverlayItem overlay) {
		overlays.add(overlay);
		populate();
	}
	
	public void setSelectedItem(EventOverlayItem chosenItem) {
		Log.i("ITEM CHOSEN", chosenItem.getId() + "");
		this.chosenItem = chosenItem;
		map.invalidate();
	}
	
	public void setSelectedItem(int id) {
		
		for (EventOverlayItem item : overlays) {
			if (item.getId() == id) {
				this.chosenItem = item;
				break;
			}
		}
		map.invalidate();
	}
	
	// ?
	public void clear() {
		this.overlays.clear();
		this.listeners.clear();
		populate();
	}
}
