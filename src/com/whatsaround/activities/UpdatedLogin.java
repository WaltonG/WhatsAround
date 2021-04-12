package com.whatsaround.activities;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.myjson.Gson;
import com.google.myjson.reflect.TypeToken;
import com.whatsaround.entity.Professional;
import com.whatsaround.restclient.AppEngineClient;
import com.whatsaround.restclient.Authenticator;
import com.whatsaround.restclient.Response;

public class UpdatedLogin extends Activity {

	private Context mContext = this;
	private int mAccountSelectedPosition = 0;
	private static AppEngineClient client;
	private final int RESULT_CODE = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		List<String> accounts = getGoogleAccounts();
		if (accounts.size() == 0) {

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage(R.string.needs_account);
			builder.setPositiveButton(R.string.add_account,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							startActivityForResult(new Intent(
									Settings.ACTION_ADD_ACCOUNT), RESULT_CODE);
						}
					});
			builder.setNegativeButton(R.string.skip,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			builder.setIcon(android.R.drawable.stat_sys_warning);
			builder.setTitle(R.string.attention);
			builder.show();

		} else {
			final ListView listView = (ListView) this
					.findViewById(R.id.select_account);
			listView.setAdapter(new ArrayAdapter<String>(mContext,
					R.layout.accounts, accounts));
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listView.setItemChecked(mAccountSelectedPosition, true);

			final Button accountButton = (Button) this
					.findViewById(R.id.add_account);
			accountButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Register in the background and terminate the activity
					startActivityForResult(new Intent(
							Settings.ACTION_ADD_ACCOUNT), RESULT_CODE);

				}
			});

			final Button loginButton = (Button) this.findViewById(R.id.login);
			loginButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Register in the background and terminate the activity
					mAccountSelectedPosition = listView
							.getCheckedItemPosition();
					TextView account = (TextView) listView
							.getChildAt(mAccountSelectedPosition);
					String accountName = (String) account.getText();

					new LoginTask(accountName).execute();

				}
			});

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_CODE == requestCode) {
			if (RESULT_OK == resultCode) {

				Intent intent = new Intent(mContext, Login.class);
				startActivity(intent);
				finish();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	private List<String> getGoogleAccounts() {
		ArrayList<String> result = new ArrayList<String>();
		Account[] accounts = AccountManager.get(mContext).getAccounts();
		for (Account account : accounts) {
			if (account.type.equals("com.google")) {
				result.add(account.name);
			}
		}

		return result;
	}

	private class LoginTask extends AsyncTask<Void, Void, Response> {
		private String accountName;
		private String authToken;
		private Authenticator mAuthenticator;
		private AccountManager mgr = AccountManager.get(mContext);
		private Account[] accounts = mgr.getAccountsByType("com.google");
		private String mErrorMessage = null;
		private Response response;
		private ProgressDialog dialog;
		private Gson gson = new Gson();
		private String firstName;
		private String lastName;
		private String country;

		public LoginTask(String accountName) {
			this.accountName = accountName;
		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(mContext);
			dialog.setMessage(getString(R.string.please_wait_while_loading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();

		}

		@Override
		protected Response doInBackground(Void... params) {

			for (Account acct : accounts) {
				if (acct.name.equals(accountName)) {
					try {
						mAuthenticator = Authenticator.appEngineAuthenticator(
								mContext, acct, new URL(
										"https://waltgogaaa.appspot.com"));
						authToken = mAuthenticator.token();
						if (authToken == null) {
							mErrorMessage = mAuthenticator.errorMessage();

						} else {
							client = new AppEngineClient(new URL(
									"https://waltgogaaaa.appspot.com"),
									authToken, mContext);
							response = client.post(new URL(
									"https://waltgogaaaa.appspot.com/login"),
									null, accountName.getBytes());
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
			return response;

		}

		@Override
		protected void onPostExecute(Response result) {
			dialog.dismiss();

			if (authToken == null) {
				Toast.makeText(mContext, mErrorMessage, Toast.LENGTH_LONG)
						.show();

			} else if (result == null) {
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
					country = professional.getCountry();
					
					Intent intent = new Intent(mContext, Homepage.class);
					intent.putExtra("authToken", authToken);
					intent.putExtra("firstName", firstName);
					intent.putExtra("lastName", lastName);
					intent.putExtra("country", country);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(mContext, Registration.class);
					intent.putExtra("authToken", authToken);
					intent.putExtra("accountName", accountName);
					startActivity(intent);
					finish();
				}
			}
		}
	}
}