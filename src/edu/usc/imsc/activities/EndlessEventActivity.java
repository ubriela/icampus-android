/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.usc.imsc.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.adapter.EndlessAdapter;

import edu.usc.imsc.R;
import edu.usc.imsc.events.Event;
import edu.usc.imsc.events.EventJsonParser;
import edu.usc.imsc.util.Tools;

/**
 * Demonstrates how to write an efficient list adapter. The adapter used in this example binds
 * to an ImageView and to a TextView for each row in the list.
 *
 * To work efficiently the adapter implemented here uses two techniques:
 * - It reuses the convertView passed to getView() to avoid inflating View when it is not necessary
 * - It uses the ViewHolder pattern to avoid calling findViewById() when it is not necessary
 *
 * The ViewHolder pattern consists in storing a data structure in the tag of the view returned by
 * getView(). This data structures contains references to the views we want to bind data to, thus
 * avoiding calls to findViewById() every time getView() is invoked.
 */

/**
 * 
 * @author win7 replace USCEventActivity class
 */
public class EndlessEventActivity extends ListActivity {

	public List<String> eventsinfo;
	public static int start = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.event_title);

		String eventURL = getIntent().getStringExtra("url");
		Log.e(Tools.TAG, eventURL);
//		String category = getIntent().getStringExtra("category");
//		String user = getIntent().getStringExtra("user");

		EventJsonParser parser = new EventJsonParser(this);

		List<Event> events = parser.parseUSCEvent(parser
				.retrieveStream(eventURL));
		Log.d(Tools.TAG, EndlessEventActivity.class.getName()
				+ " Number of events: " + events.size());

		// Check availability of USCEvent data
//		if (events == null) {
//			Toast.makeText(this, "Service is unavailable!", Toast.LENGTH_LONG);
//			this.onBackPressed();
//			this.onDestroy();
//			return;
//		}

//		String text = "category=" + category + ", user=" + user
//				+ ",# of events=" + events.size();
//		Toast.makeText(this, text, Toast.LENGTH_LONG).show();

		eventsinfo = new ArrayList<String>();
		for (int i = 0; i < events.size(); i++) {
			eventsinfo.add(events.get(i).getSummary());
		}

		ArrayList<String> tmp = new ArrayList<String>();
		for (int i = 0; i < Math
				.min(Tools.EVENT_LIST_LENGTH, eventsinfo.size()); i++) {
			tmp.add(eventsinfo.get(i));
		}

		Log.d(Tools.TAG, EndlessEventActivity.class.getName()
				+ " Number of events: " + eventsinfo.size());

		setListAdapter(new EndlessEventsAdapter(tmp));
	}

	class EndlessEventsAdapter extends EndlessAdapter {

		EndlessEventsAdapter(List<String> list) {
			// super(TestEventsActivity.this, new SpecialAdapter(
			// TestEventsActivity.this, list), R.layout.pending);
			super(EndlessEventActivity.this, new SpecialAdapter(
					EndlessEventActivity.this, 1, 1, list), R.layout.pending);
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			if (getWrappedAdapter().getCount() < eventsinfo.size()) {
				Log.d(Tools.TAG, EndlessEventActivity.class.getName()
						+ " cacheInBackground true");
				return (true);
			}
			throw new Exception("Ubriela!");
		}

		@Override
		protected void appendCachedData() {
			Log.d(Tools.TAG, EndlessEventActivity.class.getName()
					+ " appendCachedData");
			Log.d(Tools.TAG, EndlessEventActivity.class.getName()
					+ " cacheInBackground wrapper count "
					+ getWrappedAdapter().getCount());
			if (getWrappedAdapter().getCount() < eventsinfo.size()) {
				ArrayAdapter<String> wrappedAdapter = (ArrayAdapter<String>) getWrappedAdapter();
				
				start += Tools.EVENT_LIST_LENGTH;
				for (int k = start; k < Math.min(start
						+ Tools.EVENT_LIST_LENGTH, eventsinfo.size()); k++) {
					Log.d(Tools.TAG, EndlessEventActivity.class.getName()
							+ " appendCachedData k " + k);
					wrappedAdapter.add(eventsinfo.get(k));
				}
				
				Log.d(Tools.TAG, EndlessEventActivity.class.getName()
						+ " cacheInBackground al count --- "
						+ wrappedAdapter.getCount());
			}
			
			Log.d(Tools.TAG, EndlessEventActivity.class.getName()
					+ " cacheInBackground wrapper count --- "
					+ getWrappedAdapter().getCount());
		}
	}

	private static class SpecialAdapter extends ArrayAdapter<String> {
		private LayoutInflater mInflater;
		private Bitmap mIcon2;
		private ArrayList<String> events;

		public SpecialAdapter(Context context, int resource,
				int textViewResourceId, List<String> events) {
			super(context, resource, textViewResourceId, events);
			mInflater = LayoutInflater.from(context);
			this.events = new ArrayList<String>(events);
			// Icons bound to the rows.
			mIcon2 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.usclogo);
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			return events.size();
		}
		
		

		@Override
		public void add(String object) {
			// TODO Auto-generated method stub
			super.add(object);
			events.add(object);
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// Log.d(Tools.TAG, TestEventsActivity.class.getName() +
			// " getView");
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is
			// no need to reinflate it. We only inflate a new View when the
			// convertView
			// supplied by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.events, null);

				// Creates a ViewHolder and store references to the two children
				// views we want to bind data to.
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			holder.text.setText(events.get(position));
			Log.d(Tools.TAG, EndlessEventActivity.class.getName() + " Size " + events.size());
			holder.icon.setImageBitmap(mIcon2);

			return convertView;
		}

		static class ViewHolder {
			TextView text;
			ImageView icon;
		}
	}

}
