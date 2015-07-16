package edu.usc.imsc.food;

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

import edu.usc.imsc.util.Tools;

public class FoodJsonParser {
	private Context context;

	public FoodJsonParser(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	public List<Food> parseFoods(InputStream inputStream) {
		Gson gson = new Gson();

		Food[] foods = null;
		List<Food> ret = null;
		try {
			Reader reader = new InputStreamReader(inputStream);
			foods = (Food[]) gson.fromJson(reader, Food[].class);

			ret = new ArrayList<Food>();
			for (Food event : foods) {
				Log.i(Tools.TAG, event.toString());
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

		org.apache.http.impl.client.DefaultHttpClient client = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url);
		
//		BasicHttpParams prs = new BasicHttpParams();
//		Set<String> keys = params.keySet();
//		Iterator<String> it = keys.iterator();
//		while (it.hasNext()) {
//			String key = (String)it.next();
//			prs.setDoubleParameter(key, params.get(key));
//		}
//		getRequest.setParams(prs);

		Log.d(Tools.TAG, url);
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
