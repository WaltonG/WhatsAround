package com.whatsaround.activities;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.myjson.Gson;
import com.google.myjson.reflect.TypeToken;
import com.whatsaround.adapters.ProfessionalListAdapter;
import com.whatsaround.entity.Professional;
import com.whatsaround.restclient.AppEngineClient;
import com.whatsaround.restclient.Response;

public class ProfessionalList extends Activity {

	private AppEngineClient client;
	private String authToken;
	private String accountName;
	private String country;
	private Context mContext = this;
	private ListView professionalListView;
	private ArrayList<Professional> professionalList;
	private ProfessionalListAdapter listViewAdaper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_professional_list);

		Intent intent = getIntent();
		accountName = intent.getStringExtra("accountName");
		country = intent.getStringExtra("country");
		authToken = intent.getStringExtra("authToken");
		professionalList = new ArrayList<Professional>();

		try {
			client = new AppEngineClient(new URL(
					"https://waltgogaaaa.appspot.com"), authToken, mContext);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		listViewAdaper = new ProfessionalListAdapter(this, professionalList);

		professionalListView = (ListView) this
				.findViewById(R.id.professional_list);
		professionalListView.setAdapter(listViewAdaper);

		new ListLoader().execute();

		listViewAdaper.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_professional_list, menu);
		return true;
	}

	private class ListLoader extends AsyncTask<Void, Void, Response> {
		private Response response;
		private ProgressDialog dialog;
		private Gson gson = new Gson();;

		@Override
		protected Response doInBackground(Void... params) {
			try {
				response = client.post(new URL(
						"https://waltgogaaaa.appspot.com/professionalcountry"),
						null, country.getBytes());
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
				Type type = new TypeToken<List<Professional>>() {
				}.getType();

				professionalList = gson.fromJson(gsonResult, type);
				Intent intent = new Intent(mContext, ProfessionalListUpdate.class);
				intent.putExtra("gsonResult", gsonResult);
				intent.putExtra("accountName", accountName);
				intent.putExtra("country", country);
				intent.putExtra("authToken", authToken);
				startActivity(intent);
				finish();

			}

		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(mContext);
			dialog.setMessage(getString(R.string.loading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}
	}

}
