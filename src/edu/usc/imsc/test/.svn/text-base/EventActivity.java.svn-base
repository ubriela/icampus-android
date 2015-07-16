package edu.usc.imsc.test;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import edu.usc.imsc.events.Event;
import edu.usc.imsc.events.EventJsonParser;
import edu.usc.imsc.util.Tools;

public class EventActivity extends ListActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String eventURL = getIntent().getStringExtra("url");
		Log.e(Tools.TAG, eventURL);
		String category = getIntent().getStringExtra("category");
		String user = getIntent().getStringExtra("user");

		EventJsonParser parser = new EventJsonParser(this);
		List<Event> events = parser.parseUSCEvent(parser
				.retrieveStream(eventURL));
		String text = "category=" + category + ", user=" + user
				+ ",# of events=" + events.size();
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		// Log.e("Test", ninfo.toString());
		List<String> eventsinfo = new ArrayList<String>();
		for (int i = 0; i < events.size(); i++) {
			eventsinfo.add(events.get(i).getTitle());
		}

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, eventsinfo));
		getListView().setTextFilterEnabled(true);
	}
}