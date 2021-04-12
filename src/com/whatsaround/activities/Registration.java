package com.whatsaround.activities;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.myjson.Gson;
import com.google.myjson.reflect.TypeToken;
import com.whatsaround.entity.Professional;
import com.whatsaround.restclient.AppEngineClient;
import com.whatsaround.restclient.Response;

public class Registration extends Activity {
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String country;
	private String professionTitle;
	private String professionCategory;
	private AppEngineClient client;
	private String authToken;
	private String accountName;
	private byte[] profilePicture;
	private Context mContext = this;
	private static final int SELECT_PICTURE = 1;
	private String selectedImagePath;
	private Bitmap b;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);

		Intent intent = getIntent();
		authToken = intent.getStringExtra("authToken");
		accountName = intent.getStringExtra("accountName");
		try {
			client = new AppEngineClient(new URL(
					"https://waltgogaaaa.appspot.com"), authToken, mContext);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final EditText fNameEditText = (EditText) this.findViewById(R.id.fName);
		final EditText lNameEditText = (EditText) this.findViewById(R.id.lName);
		final EditText phoneEditText = (EditText) this
				.findViewById(R.id.phone_number);
		final EditText professionTitleEditText = (EditText) this
				.findViewById(R.id.profession_title);
		final Spinner countrySpinner = (Spinner) this
				.findViewById(R.id.country_spinner);
		final Spinner professionCategorySpinner = (Spinner) this
				.findViewById(R.id.profession_category_spinner);
		final ImageButton locationButton = (ImageButton) this
				.findViewById(R.id.profession_location_imageButton);
		final ImageButton profilePicButton = (ImageButton) this
				.findViewById(R.id.profile_Pic_imageButton);
		final Button registerButton = (Button) this.findViewById(R.id.register);

		locationButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				firstName = fNameEditText.getText().toString();
				lastName = lNameEditText.getText().toString();
				phoneNumber = phoneEditText.getText().toString();
				country = String.valueOf(countrySpinner.getSelectedItem());
				professionCategory = String.valueOf(professionCategorySpinner
						.getSelectedItem());
				professionTitle = professionTitleEditText.getText().toString();

				if (firstName.trim().equalsIgnoreCase("")
						|| lastName.trim().equalsIgnoreCase("")
						|| professionTitle.trim().equalsIgnoreCase("")
						|| country == null || professionCategory == null) {

					Toast.makeText(mContext, R.string.required_details,
							Toast.LENGTH_LONG).show();
				} else {
					Intent intent = new Intent(mContext,
							ProfessionLocation.class);
					intent.putExtra("accountName", accountName);
					intent.putExtra("firstName", firstName);
					intent.putExtra("lastName", lastName);
					intent.putExtra("country", String.valueOf(countrySpinner
							.getSelectedItemPosition()));
					intent.putExtra("professionCategory", String
							.valueOf(professionCategorySpinner
									.getSelectedItemPosition()));
					intent.putExtra("professionTitle", professionTitle);
					intent.putExtra("profilePicture", profilePicture);
					intent.putExtra("phoneNumber", phoneNumber);
					intent.putExtra("authToken", authToken);
					startActivity(intent);

				}
			}
		});

		profilePicButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				v.setClickable(false);
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						SELECT_PICTURE);
			}
		});

		registerButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				v.setEnabled(false);
				firstName = fNameEditText.getText().toString();
				lastName = lNameEditText.getText().toString();
				phoneNumber = phoneEditText.getText().toString();
				country = String.valueOf(countrySpinner.getSelectedItem());
				professionCategory = String.valueOf(professionCategorySpinner
						.getSelectedItem());
				professionTitle = professionTitleEditText.getText().toString();

				if (firstName.trim().equalsIgnoreCase("")) {
					Toast.makeText(mContext, "First name is required",
							Toast.LENGTH_LONG).show();
				} else if (lastName.trim().equalsIgnoreCase("")) {
					Toast.makeText(mContext, "Last name is required",
							Toast.LENGTH_LONG).show();
				} else if (country.trim().equalsIgnoreCase("")) {
					Toast.makeText(mContext, "Country is required",
							Toast.LENGTH_LONG).show();
				} else if (!professionCategory.trim().equalsIgnoreCase("")
						&& professionTitle.trim().equalsIgnoreCase("")) {
					Toast.makeText(mContext, "Profession title is required",
							Toast.LENGTH_LONG).show();
				} else {

					new Register().execute();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {

				Uri selectedImageUri = data.getData();
				selectedImagePath = getPath(selectedImageUri);

				if (selectedImagePath != null) {

					Toast.makeText(mContext, R.string.image_captured,
							Toast.LENGTH_LONG).show();

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPurgeable = true;

					Bitmap b = Bitmap.createScaledBitmap(BitmapFactory
							.decodeFile(selectedImagePath, options), 150, 150,
							true);

					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					profilePicture = stream.toByteArray();

					Log.d(Registration.class.getSimpleName(), ""
							+ profilePicture.length);
				} else {
					Toast.makeText(mContext, "Action failed!",
							Toast.LENGTH_LONG).show();
				}
			}

		} else if (resultCode == Activity.RESULT_CANCELED) {
			Toast.makeText(mContext, "Action canceled.", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(mContext, "Action failed!", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_registration, menu);
		return true;
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		CursorLoader cursorLoader = new CursorLoader(mContext, uri, projection,
				null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();

		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();

			return cursor.getString(column_index);
		} else {
			return uri.getPath();
		}
	}

	private class Register extends AsyncTask<Void, Void, Response> {

		Response response;
		private HashMap<String, byte[]> registrationDetails = new HashMap<String, byte[]>();
		private ProgressDialog dialog;
		private Gson gson = new Gson();
		Type type = new TypeToken<HashMap<String, byte[]>>() {
		}.getType();

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(mContext);
			dialog.setMessage(getString(R.string.please_wait_while_registering));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}

		@TargetApi(8)
		@Override
		protected Response doInBackground(Void... arg0) {
			registrationDetails.put("accountName", accountName.getBytes());
			registrationDetails.put("firstName", firstName.getBytes());
			registrationDetails.put("lastName", lastName.getBytes());
			registrationDetails.put("country", country.getBytes());
			registrationDetails.put("professionCategory",
					professionCategory.getBytes());
			registrationDetails.put("professionTitle",
					professionTitle.getBytes());
			if (profilePicture != null) {
				registrationDetails.put("profilePicture", profilePicture);
			}
			registrationDetails.put("phoneNumber", phoneNumber.getBytes());

			String gsonDetails = gson.toJson(registrationDetails, type);

			try {
				response = client.post(new URL(
						"https://waltgogaaaa.appspot.com/newuser"), null,
						gsonDetails.getBytes());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(Response result) {
			dialog.dismiss();
			if (result == null) {
				Toast.makeText(mContext, client.errorMessage(),
						Toast.LENGTH_LONG).show();

			} else if ((result.status / 100) != 2) {
				Toast.makeText(mContext, R.string.network_faliure,
						Toast.LENGTH_LONG).show();
			} else {
				String gsonResult = new String(result.body);
				Type type = new TypeToken<Professional>() {
				}.getType();
				Professional professional = gson.fromJson(gsonResult, type);
				if (professional != null) {
					firstName = professional.getFirstName();
					lastName = professional.getLastName();
					profilePicture = professional.getProfilePicture();

					Intent intent = new Intent(mContext, Homepage.class);
					intent.putExtra("authToken", authToken);
					intent.putExtra("firstName", firstName);
					intent.putExtra("lastName", lastName);
					intent.putExtra("profilePicture", profilePicture);
					intent.putExtra("accountName", accountName);
					intent.putExtra("country", country);
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(mContext, R.string.try_again,
							Toast.LENGTH_LONG).show();
				}

			}
		}
	}
}
