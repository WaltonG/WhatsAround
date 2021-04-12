package com.whatsaround.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.whatsaround.location.MyLocation;
import com.whatsaround.location.MyLocation.LocationResult;

public class Homepage extends Activity {

	private Integer[] mThumbIds = { R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher };

	private String[] mTextIds = { "Professionals", "Careers", "Market",
			"Institutions", "Traffic" };

	private String firstName;
	private String lastName;
	private String authToken;
	private byte[] profilePicture;
	private byte[] cV;
	private String accountName;
	private double latitude;
	private double longitude;
	private String country;
	private Context mContext = this;
	private static final int PROFESSIONALS_GPS = 1;
	private static final int CAREERS_GPS = 2;
	private static final int TRAFFIC_GPS = 3;
	private static final int MARKET_GPS = 4;
	private static final int INSTITUTIONS_GPS = 5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homepage);
		
		Intent intent = getIntent();
		firstName = intent.getStringExtra("firstName");
		lastName = intent.getStringExtra("lastName");
		firstName = intent.getStringExtra("firstName");
		accountName = intent.getStringExtra("accountName");
		profilePicture= intent.getByteArrayExtra("profilePicture");
		country = intent.getStringExtra("country");
		authToken = intent.getStringExtra("authToken");
		
		final LocationResult locationResult = new LocationResult() {

			@Override
			public void gotLocation(Location location) {

				latitude = location.getLatitude();
				longitude = location.getLatitude();
			}
		};

		final MyLocation myLocation = new MyLocation();

		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 5; i++) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("txt", mTextIds[i]);
			hm.put("pic", Integer.toString(mThumbIds[i]));
			aList.add(hm);

			String[] from = { "pic", "txt" };

			int[] to = { R.id.homepage_icon_image, R.id.hompage_icon_text };

			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList,
					R.layout.hompage_icons, from, to);

			GridView gridView = (GridView) findViewById(R.id.homepage_gridview);

			gridView.setAdapter(adapter);

			gridView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {

					switch (position) {
					case 0:
						if (!myLocation.getLocation(mContext, locationResult)) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									mContext);
							builder.setMessage(R.string.enable_gps);
							builder.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											startActivityForResult(
													new Intent(
															Settings.ACTION_LOCATION_SOURCE_SETTINGS),
													PROFESSIONALS_GPS);
										}
									});
							builder.setNegativeButton(R.string.skip,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = new Intent(
													mContext,
													ProfessionalList.class);
											intent.putExtra("accountName",
													accountName);
											intent.putExtra("country", country);
											intent.putExtra("authToken",
													authToken);
											startActivity(intent);
										}
									});
							builder.setIcon(android.R.drawable.stat_sys_warning);
							builder.setTitle(R.string.attention);
							builder.show();
						} else {
							Intent intent = new Intent(mContext,
									ProfessionalListLocation.class);
							intent.putExtra(accountName, accountName);
							intent.putExtra("latitude", latitude);
							intent.putExtra("longitude", longitude);
							intent.putExtra("authToken", authToken);
							intent.putExtra("country", country);
							Log.d(Homepage.class.getSimpleName(), "Latitude" + latitude
									+ "Longitude" + longitude +" OnCreate");
							startActivity(intent);

						}
						break;

					default:
						break;
					}

				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == PROFESSIONALS_GPS) {
				final LocationResult locationResult = new LocationResult() {

					@Override
					public void gotLocation(Location location) {

						latitude = location.getLatitude();
						longitude = location.getLatitude();
					}
				};

				final MyLocation myLocation = new MyLocation();
				if (!myLocation.getLocation(mContext, locationResult)) {
					Intent intent = new Intent(mContext, ProfessionalList.class);
					intent.putExtra("accountName", accountName);
					intent.putExtra("country", country);
					intent.putExtra("authToken", authToken);
					startActivity(intent);
				} else {
					Intent intent = new Intent(mContext,
							ProfessionalListLocation.class);
					intent.putExtra(accountName, accountName);
					intent.putExtra("latitude", latitude);
					intent.putExtra("longitude", longitude);
					intent.putExtra("authToken", authToken);
					intent.putExtra("country", country);
					Log.d("OnActivityResult", "Latitude" + latitude
							+ "Longitude" + longitude +" OnActivityResult");
					startActivity(intent);
				}
			}
			if (requestCode == CAREERS_GPS) {

			}
			if (requestCode == INSTITUTIONS_GPS) {

			}
			if (requestCode == MARKET_GPS) {

			}
			if (requestCode == TRAFFIC_GPS) {

			}

		} else if (resultCode == Activity.RESULT_CANCELED) {
			Toast.makeText(mContext, R.string.action_canceled,
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(mContext, R.string.action_failed, Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_homepage, menu);
		return true;
	}

}
