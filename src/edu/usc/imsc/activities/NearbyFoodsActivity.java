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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.commonsware.cwac.adapter.EndlessAdapter;
import com.google.android.maps.GeoPoint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import edu.usc.imsc.R;
import edu.usc.imsc.camera.CameraActivity;
import edu.usc.imsc.facebook.DataStorage;
import edu.usc.imsc.food.Food;
import edu.usc.imsc.food.FoodJsonParser;
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
public class NearbyFoodsActivity extends ListActivity {

	public List<Food> foodsinfo;
	public static int start = 0;
	public static int FOOD_LIST_LENGTH = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.food_title);
		start = 0;
		FOOD_LIST_LENGTH = Integer
		.valueOf(MapBrowserActivity.preferences.getString(
				"food_list_count_pref", "10"));

		FoodJsonParser parser = new FoodJsonParser(this);

		GeoPoint current = null;
		if (MapBrowserActivity.myLocationOverlay != null) {
			current = MapBrowserActivity.myLocationOverlay.getMyLocation();
			if (current == null) {
				current = new GeoPoint(34020209, -118285686);
			}
		} else {
			current = new GeoPoint(34020209, -118285686);
		}
		String url = Tools.getNearbyFoodsURL(current, start, FOOD_LIST_LENGTH);
		List<Food> foods = parser.parseFoods((parser.retrieveStream(url)));

		// Check availability of USCEvent data
		if (foods == null) {
			Toast.makeText(this, "Service is unavailable!", Toast.LENGTH_LONG);
			this.onBackPressed();
			this.onDestroy();
			return;
		}

		foodsinfo = new ArrayList<Food>();
		for (int i = 0; i < foods.size(); i++) {
			foodsinfo.add(foods.get(i));
		}

		setListAdapter(new EndlessEfficientAdapter(this, foodsinfo));

		// getListView().setTextFilterEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.info_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera:
			show_camera();
			break;
		}
		return true;
	}
	
	private void show_camera() {
		// TODO Auto-generated method stub
		if (DataStorage.isLoggedIn()) {
			// Intent i = new Intent().setClass(this,
			// ImageUploadActivity.class);
			Intent i = new Intent().setClass(this, CameraActivity.class);
			startActivity(i);
		} else {
			new AlertDialog.Builder(this)
					.setMessage("You need login to view status!")
					.setTitle("Status").setCancelable(true).show();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		Intent data = new Intent();
		if (foodsinfo.get(position).getLat() != null)
			data.putExtra("food_lat", foodsinfo.get(position).getLat());
		if (foodsinfo.get(position).getLon() != null)
			data.putExtra("food_lon", foodsinfo.get(position).getLon());
		if (foodsinfo.get(position).getPid() != null)
			data.putExtra("food_pid", foodsinfo.get(position).getPid());
		if (foodsinfo.get(position).getTitle() != null)
			data.putExtra("food_title", foodsinfo.get(position).getTitle());
		if (foodsinfo.get(position).getTime() != null)
			data.putExtra("food_time", foodsinfo.get(position).getTime());
		if (foodsinfo.get(position).getPlace() != null)
			data.putExtra("food_place", foodsinfo.get(position).getPlace());
		if (foodsinfo.get(position).getRating() != null)
			data.putExtra("food_rating", foodsinfo.get(position).getRating());
		if (foodsinfo.get(position).getSmallphoto() != null)
			data.putExtra("food_smallphoto", foodsinfo.get(position)
					.getSmallphoto());
		if (foodsinfo.get(position).getComment() != null)
			data.putExtra("food_comment", foodsinfo.get(position).getComment());
		// If the image don't have its location
		if (foodsinfo.get(position).getLat().equals("")
				| foodsinfo.get(position).getLon().equals("")) {
			Toast.makeText(getApplicationContext(),
					"No location associates with this data", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		if (getParent() == null) {
			setResult(Activity.RESULT_OK, data);
		} else {
			getParent().setResult(Activity.RESULT_OK, data);
		}

		this.onBackPressed();
	}

	class EndlessEfficientAdapter extends EndlessAdapter {
		Context m_context;
		List<Food> foods;

		EndlessEfficientAdapter(Context context, List<Food> foods) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			super(context, new SpecialAdapter(context, 1, 1, foods),
					R.layout.pending);
			m_context = context;
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			FoodJsonParser parser = new FoodJsonParser(m_context);

			GeoPoint current = null;
			if (MapBrowserActivity.myLocationOverlay != null) {
				current = MapBrowserActivity.myLocationOverlay.getMyLocation();
				if (current == null) {
					current = new GeoPoint(34020209, -118285686);
				}
			} else {
				current = new GeoPoint(34020209, -118285686);
			}
			start += FOOD_LIST_LENGTH;
			String url = Tools.getNearbyFoodsURL(current, start, Integer
					.valueOf(MapBrowserActivity.preferences.getString(
							"food_list_count_pref", "10")));
			if (foods != null)
				foods.clear();
			foods = parser.parseFoods((parser.retrieveStream(url)));

			if (foods != null) {
				Log.d(Tools.TAG, NearbyFoodsActivity.class.getName()
						+ " cacheInBackground true");
				return (true);
			}
			throw new Exception("Ubriela!");
		}

		@Override
		protected void appendCachedData() {
			Log.d(Tools.TAG, NearbyFoodsActivity.class.getName()
					+ " appendCachedData");
			Log.d(Tools.TAG, NearbyFoodsActivity.class.getName()
					+ " cacheInBackground wrapper count "
					+ getWrappedAdapter().getCount());
			if (getWrappedAdapter().getCount() < 100) {
				ArrayAdapter<Food> wrappedAdapter = (ArrayAdapter<Food>) getWrappedAdapter();

				Iterator it = foods.iterator();
				while (it.hasNext()) {
					Food food = (Food) it.next();
					wrappedAdapter.add(food);
				}

				Log.d(Tools.TAG,
						NearbyFoodsActivity.class.getName()
								+ " cacheInBackground al count --- "
								+ wrappedAdapter.getCount());
			}

			Log.d(Tools.TAG, NearbyFoodsActivity.class.getName()
					+ " cacheInBackground wrapper count --- "
					+ getWrappedAdapter().getCount());
		}
	}

	private static class SpecialAdapter extends ArrayAdapter<Food> {

		private LayoutInflater mInflater;
		public Bitmap mIcon;
		private ArrayList<Food> foods;

		public SpecialAdapter(Context context, int resource,
				int textViewResourceId, List<Food> foods) {
			super(context, resource, textViewResourceId, foods);
			mInflater = LayoutInflater.from(context);
			this.foods = new ArrayList<Food>(foods);
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			return foods.size();
		}

		@Override
		public void add(Food object) {
			// TODO Auto-generated method stub
			super.add(object);
			foods.add(object);
		}

		/**
		 * Use the array index as a unique id.
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;
			RatingBar mSmallRatingBar = null;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.foods, null);
				holder = new ViewHolder();
				holder.title_text = (TextView) convertView
						.findViewById(R.id.food_title_text);
				holder.food_icon = (ImageView) convertView
						.findViewById(R.id.food_icon);
				mSmallRatingBar = (RatingBar) convertView
						.findViewById(R.id.small_ratingbar);
				mSmallRatingBar.setNumStars(5);
				mSmallRatingBar.setStepSize(1);
				mSmallRatingBar.setProgress(Integer.valueOf(foods.get(position)
						.getRating()));
				Log.d(Tools.TAG, NearbyFoodsActivity.class.getName() + " rating " + foods.get(position)
						.getRating());

				holder.place_text = (TextView) convertView
						.findViewById(R.id.food_place_text);
				holder.time_text = (TextView) convertView
						.findViewById(R.id.food_time_text);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			if (foods.get(position).getTitle() != null)
				holder.title_text
						.setText(foods.get(position).getTitle().trim());

			if (foods.get(position).getPlace() != null)
				holder.place_text
						.setText(foods.get(position).getPlace().trim());

			String distance = "";
			if (foods.get(position).getDist() != null)
				distance = "\n" + foods.get(position).getDist().substring(0, 6)
						+ " m";

			if (foods.get(position).getTime() != null)
				holder.time_text.setText(foods.get(position).getTime().trim()
						+ distance);

			Bitmap mIcon2 = getBitmapFromURL(foods.get(position).getIconphoto());

			if (mIcon2 == null) {
				holder.food_icon.setImageBitmap(mIcon);
			} else {
				holder.food_icon.setImageBitmap(mIcon2);
			}

			return convertView;
		}

		static class ViewHolder {
			TextView title_text;
			ImageView food_icon;
			TextView place_text;
			TextView time_text;
		}

		static public Bitmap getBitmapFromURL(String src) {
			try {
				if (Tools.getImageURL(src) != null) {
					URL url = new URL(Tools.getImageURL(src));
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setDoInput(true);
					connection.connect();
					InputStream input = connection.getInputStream();
					Bitmap myBitmap = BitmapFactory.decodeStream(input);
					return myBitmap;
				} else
					return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}
