package com.whatsaround.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatsaround.activities.R;
import com.whatsaround.entity.Professional;

public class ProfessionalListAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<Professional> professionalList;

	public ProfessionalListAdapter(Activity activit,
			ArrayList<Professional> professionalList) {

		this.activity = activit;
		this.professionalList = professionalList;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public int getCount() {
		return professionalList.size();
	}

	public Object getItem(int position) {

		return position;
	}

	public long getItemId(int position) {

		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		Professional professional = professionalList.get(position);
		if (convertView == null)
			vi = inflater.inflate(R.layout.professional_list_row, null);

		TextView nameText = (TextView) vi.findViewById(R.id.professional_name);
		TextView titleText = (TextView) vi
				.findViewById(R.id.professional_title);
		TextView contactText = (TextView) vi
				.findViewById(R.id.professional_contact);

		nameText.setText(professional.getFirstName());
		titleText.setText(professional.getProfessionTitle());
		contactText.setText(professional.getPhoneNumber());

		ImageView profilePicture = (ImageView) vi.findViewById(R.id.professional_list_picture);
		imageLoader.DisplayImage(professional.getProfilePicture(), profilePicture);
		return vi;
	}

}
