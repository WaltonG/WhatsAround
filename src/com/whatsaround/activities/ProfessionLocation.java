package com.whatsaround.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.whatsaround.mapview.CustomMapView;
import com.whatsaround.restclient.AppEngineClient;

public class ProfessionLocation extends MapActivity {

	private String firstName;
	private String lastName;
	private String phoneNumber;
	private int country;
	private String professionTitle;
	private int professionCategory;
	private double latitude;
	private double longitude;
	private String authToken;
	private String accountName;
	private byte[] profilePicture;
	private CustomMapView mapView;
	private Context mContext = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profession_location);

		Intent intnt = getIntent();

		accountName = intnt.getStringExtra("accountName");
		firstName = intnt.getStringExtra("firstName");
		lastName = intnt.getStringExtra("lastName");
		country = Integer.parseInt(intnt.getStringExtra("country"));
		professionCategory = Integer.parseInt(intnt
				.getStringExtra("professionCategory"));
		professionTitle = intnt.getStringExtra("professionTitle");
		profilePicture = intnt.getByteArrayExtra("profilePicture");
		phoneNumber = intnt.getStringExtra("phoneNumber");
		authToken = intnt.getStringExtra("authToken");

		// Add the OnLongPressListener to our custom MapView
		mapView = (CustomMapView) findViewById(R.id.mapview);
		ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);

		zoomControls.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				mapView.getController().zoomIn();
			}
		});

		zoomControls.setOnZoomOutClickListener(new OnClickListener() {
			public void onClick(View v) {
				mapView.getController().zoomOut();
			}
		});

		mapView.setOnLongpressListener(new CustomMapView.OnLongpressListener() {
			public void onLongpress(final MapView view,
					final GeoPoint longpressLocation) {

				runOnUiThread(new Runnable() {
					public void run() {
						latitude = longpressLocation.getLatitudeE6() / 1E6;
						longitude = longpressLocation.getLongitudeE6() / 1E6;

						Toast.makeText(mContext, R.string.location_captured,
								Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext,
								RegistrationUpdate.class);
						intent.putExtra("accountName", accountName);
						intent.putExtra("firstName", firstName);
						intent.putExtra("lastName", lastName);
						intent.putExtra("country", String.valueOf(country));
						intent.putExtra("professionCategory",
								String.valueOf(professionCategory));
						intent.putExtra("professionTitle", professionTitle);
						intent.putExtra("profilePicture", profilePicture);
						intent.putExtra("phoneNumber", phoneNumber);
						intent.putExtra("latitude", String.valueOf(latitude));
						intent.putExtra("longitude", String.valueOf(longitude));
						intent.putExtra("authToken", authToken);
						startActivity(intent);
						finish();
					}
				});
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_profession_location, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
