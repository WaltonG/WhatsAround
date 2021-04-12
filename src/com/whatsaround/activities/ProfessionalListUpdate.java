package com.whatsaround.activities;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.myjson.Gson;
import com.google.myjson.reflect.TypeToken;
import com.whatsaround.adapters.ProfessionalListAdapter;
import com.whatsaround.entity.Professional;
import com.whatsaround.restclient.AppEngineClient;

public class ProfessionalListUpdate extends Activity {

	private AppEngineClient client;
	private String authToken;
	private String accountName;
	private String country;
	private Context mContext = this;
	private ListView professionalListView;
	private ArrayList<Professional> professionalList;
	private ProfessionalListAdapter listViewAdaper;
	private String gsonResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_professional_list);

		Intent intent = getIntent();
		accountName = intent.getStringExtra("accountName");
		country = intent.getStringExtra("country");
		authToken = intent.getStringExtra("authToken");
		gsonResult = intent.getStringExtra("gsonResult");

		Gson gson = new Gson();

		professionalList = new ArrayList<Professional>();
		Type type = new TypeToken<List<Professional>>() {
		}.getType();

		professionalList = gson.fromJson(gsonResult, type);

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

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_professional_list_update,
				menu);
		return true;
	}

}
