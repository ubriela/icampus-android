package edu.usc.imsc.events;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

/**
 * @author Ling http://icampus.usc.edu/icampus/services/GetEvents
 * 
 *         id title subtitle summary description feature_candidate campus
 *         location image_thumb image_featured image publication_status spoken
 *         recurrence start_date end_date building_code start_time end_time
 */
public class EventJsonParser {
	private Context context;

	public EventJsonParser(Context context) {
		this.context = context;
	}

	/**
	 * get a list of USC events from an input stream
	 * 
	 * @param inputStream
	 * @return
	 */
	public List<Event> parseUSCEvent(InputStream inputStream) {
		Gson gson = new Gson();

		Event[] events = null;
		List<Event> ret = null;
		try {
			Reader reader = new InputStreamReader(inputStream);
			events = (Event[]) gson.fromJson(reader, Event[].class);

			ret = new ArrayList<Event>();
			for (Event event : events) {
				ret.add(event);
			}
		} catch (Exception ex) {
			Log.w(getClass().getSimpleName(), "", ex);
			return null;
		}

		if (ret != null && ret.size() > 0)
			return ret;
		else
			return null;
	}

	/**
	 * Retrieve an input stream from an url
	 * 
	 * @param url
	 * @return
	 */
	public InputStream retrieveStream(String url) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w(getClass().getSimpleName(), "Error " + statusCode
						+ " for URL " + url);
				return null;
			}

			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();

		} catch (IOException e) {
			getRequest.abort();
			Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
		}

		return null;
	}
}
