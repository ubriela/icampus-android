package edu.usc.imsc.camera;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import edu.usc.imsc.R;
import edu.usc.imsc.activities.MapBrowserActivity;
import edu.usc.imsc.facebook.DataStorage;
import edu.usc.imsc.util.Tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

public class ImageUploadActivity extends Activity {
	private static final int PICK_IMAGE = 1;
	private ImageView imgView;
	private Button upload;
	private EditText title;
	private EditText place;
	private EditText comment;
	private RatingBar rating;
	private Bitmap bitmap;
	private Bitmap smallBitmap;
	private Bitmap iconBitmap;
	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_upload);

		imgView = (ImageView) findViewById(R.id.imageView);
		
		upload = (Button) findViewById(R.id.buttonSubmit);
		title = (EditText) findViewById(R.id.edittextTitle);
		// title.setText(filePath);
		comment = (EditText) findViewById(R.id.edittextComment);
		place = (EditText) findViewById(R.id.edittextLocation);
		rating = (RatingBar) findViewById(R.id.ratingBar);

		upload.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (bitmap == null) {
					Toast.makeText(getApplicationContext(),
							"Please select image", Toast.LENGTH_SHORT).show();
				} else {
					dialog = ProgressDialog.show(ImageUploadActivity.this,
							"Uploading", "Please wait...", true);
					new ImageUploadTask().execute();
				}
			}
		});
		
		
		// Get image uri
		String filePath = getIntent().getStringExtra("imgUri");
//		Toast.makeText(getApplicationContext(), filePath, Toast.LENGTH_LONG)
//				.show();
		try {

			if (filePath != null) {
				decodeFile(filePath);
			} else {
				smallBitmap = null;
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Internal error",
					Toast.LENGTH_LONG).show();
			Log.e(e.getClass().getName(), e.getMessage(), e);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.imageupload_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.ic_menu_gallery:
			try {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						PICK_IMAGE);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PICK_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImageUri = data.getData();
				String filePath = null;

				try {
					// OI FILE Manager
					String filemanagerstring = selectedImageUri.getPath();

					// MEDIA GALLERY
					String selectedImagePath = getPath(selectedImageUri);

					if (selectedImagePath != null) {
						filePath = selectedImagePath;
					} else if (filemanagerstring != null) {
						filePath = filemanagerstring;
					} else {
						Toast.makeText(getApplicationContext(), "Unknown path",
								Toast.LENGTH_LONG).show();
						Log.e("Bitmap", "Unknown path");
					}

					// place.setText(filePath);

					if (filePath != null) {
						decodeFile(filePath);
					} else {
						bitmap = null;
						smallBitmap = null;
						iconBitmap = null;
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Internal error",
							Toast.LENGTH_LONG).show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
				}
			}
			break;
		default:
		}
	}

	class ImageUploadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... unsued) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				Log.d("Service: ", Tools.UPLOAD_IMAGE_URL);
				HttpPost httpPost = new HttpPost(Tools.UPLOAD_IMAGE_URL);

				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				entity.addPart("returnformat", new StringBody("json"));

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 100, bos);
				byte[] data = bos.toByteArray();
				entity.addPart("photo", new ByteArrayBody(data, "photo.jpg"));

				ByteArrayOutputStream smallbos = new ByteArrayOutputStream();
				smallBitmap.compress(CompressFormat.JPEG, 100, smallbos);
				byte[] smallData = smallbos.toByteArray();
				entity.addPart("smallphoto", new ByteArrayBody(smallData,
						"smallphoto.jpg"));

				ByteArrayOutputStream iconbos = new ByteArrayOutputStream();
				iconBitmap.compress(CompressFormat.JPEG, 100, iconbos);
				byte[] iconData = iconbos.toByteArray();
				entity.addPart("iconphoto", new ByteArrayBody(iconData,
						"iconphoto.jpg"));

				entity.addPart("title", new StringBody(title.getText()
						.toString()));
				entity.addPart("comment", new StringBody(comment.getText()
						.toString()));
				entity.addPart("place", new StringBody(place.getText()
						.toString()));
				entity.addPart("rating",
						new StringBody(String.valueOf(rating.getProgress())));
				entity.addPart("comment", new StringBody(comment.getText()
						.toString()));
				entity.addPart(
						"uid",
						new StringBody(String.valueOf(DataStorage.getMe()
								.getId())));
				if (MapBrowserActivity.myLocationOverlay != null) {
					GeoPoint current = MapBrowserActivity.myLocationOverlay
							.getMyLocation();
					if (current != null) {
						entity.addPart(
								"lat",
								new StringBody(String.valueOf(current
										.getLatitudeE6() / 1e6)));
						entity.addPart(
								"lon",
								new StringBody(String.valueOf(current
										.getLongitudeE6() / 1e6)));
					}
				}
				httpPost.setEntity(entity);

				HttpResponse response = httpClient.execute(httpPost,
						localContext);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));

				String sResponse = reader.readLine();
				return sResponse;
			} catch (Exception e) {
				if (dialog.isShowing())
					dialog.dismiss();
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
				return null;
			}

		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {
				if (dialog.isShowing())
					dialog.dismiss();

				if (sResponse != null) {
					JSONObject JResponse = new JSONObject(sResponse);
					int success = JResponse.getInt("SUCCESS");
					String message = JResponse.getString("MESSAGE");
					if (success == 0) {
						Toast.makeText(getApplicationContext(), message,
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getApplicationContext(),
								"Photo uploaded successfully",
								Toast.LENGTH_SHORT).show();
						title.setText("");
					}
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		// The new size we want to scale to

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		int smallScale = 1;
		int iconScale = 1;
		while (true) {
			if (width_tmp < Tools.PHOTO_REQUIRED_SIZE
					&& height_tmp < Tools.PHOTO_REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}
		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filePath, o2);

		smallScale = scale;
		while (true) {
			if (width_tmp < Tools.SMALLPHOTO_REQUIRED_SIZE
					&& height_tmp < Tools.SMALLPHOTO_REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			smallScale *= 2;
		}
		// Decode with inSampleSize
		o2.inSampleSize = smallScale;
		smallBitmap = BitmapFactory.decodeFile(filePath, o2);

		iconScale = smallScale;
		while (true) {
			if (width_tmp < Tools.ICONPHOTO_REQUIRED_SIZE
					&& height_tmp < Tools.ICONPHOTO_REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			iconScale *= 2;
		}
		// Decode with inSampleSize
		o2.inSampleSize = iconScale;
		iconBitmap = BitmapFactory.decodeFile(filePath, o2);

		imgView.setImageBitmap(smallBitmap);
	}

	public void discardCamera(View v) {
		this.onBackPressed();
	}
}