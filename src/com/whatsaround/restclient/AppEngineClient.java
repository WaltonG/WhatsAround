package com.whatsaround.restclient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.content.Context;
import android.os.Build;

import com.whatsaround.activities.R;


/**
 * Performs GET and POST operations against a Google App Engine app,
 * authenticating with a Google account on the device.
 */

public class AppEngineClient {
	private Authenticator mAuthenticator;
	private Context mContext;
	private String mErrorMessage;
	 static final String Tag = "AppEngineClient";
	 
	/**
	 * Constructs a client, which may be used for multiple requests. This
	 * constructor may be called on the UI thread.
	 * 
	 * @param appURI
	 *            The URI of the App Engine app you want to interact with, e.g.
	 *            your-app.appspot.com
	 * @param account
	 *            The android.accounts.Account representing the Google account
	 *            to authenticate with.
	 * @param context
	 *            Used, if necessary, to prompt user for authentication
	 */
	public AppEngineClient(URL appURI, Account account, Context context) {
		// HTTP connection reuse which was buggy pre-froyo
		if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
		mAuthenticator = Authenticator.appEngineAuthenticator(context, account,
				appURI);
		mContext = context;
	}

	/**
	 * Constructs a client, which may be used for multiple requests, and which
	 * will never prompt the user for authentication input. This constructor may
	 * be called on the UI thread but is suitable for use on background threads
	 * with no access to an Activity.
	 * 
	 * @param appURI
	 *            The URI of the App Engine app you want to interact with, e.g.
	 *            your-app.appspot.com
	 * @param authToken
	 *            AccountManager authtoken, e.g. from the token() method
	 * @param context
	 *            Used to look up strings
	 */
	public AppEngineClient(URL appURI, String authToken, Context context) {
		mAuthenticator = Authenticator.appEngineAuthenticator(context, appURI,
				authToken);
		mContext = context;
	}

	/**
	 * Performs an HTTP GET request. The request is performed inline and this
	 * method must not be called from the UI thread.
	 * 
	 * @param uri
	 *            The URI you're sending the GET to
	 * @param headers
	 *            Any extra HTTP headers you want to send; may be null
	 * @return a Response structure containing the status, headers, and body.
	 *         Returns null if the request could not be launched due to an IO
	 *         error or authentication failure; in which case use errorMessage()
	 *         to retrieve diagnostic information.
	 */
	public Response get(URL uri, Map<String, String> headers) {
		GET get = new GET(uri, headers);
		return getOrPost(get);
	}

	/**
	 * Performs an HTTP POST request. The request is performed inline and this
	 * method must not be called from the UI thread.
	 * 
	 * @param uri
	 *            The URI you're sending the POST to
	 * @param headers
	 *            Any extra HTTP headers you want to send; may be null
	 * @param body
	 *            The request body to transmit
	 * @return a Response structure containing the status, headers, and body.
	 *         Returns null if the request could not be launched due to an IO
	 *         error or authentication failure; in which case use errorMessage()
	 *         to retrieve diagnostic information.
	 */
	public Response post(URL uri, Map<String, String> headers, byte[] body) {
		POST post = new POST(uri, headers, body);
		return getOrPost(post);
	}

	/**
	 * Provides an error message should a get() or post() return null.
	 * 
	 * @return the message
	 */
	public String errorMessage() {
		return mErrorMessage;
	}

	// request structs
	private class Request {
		public URL uri;
		public Map<String, String> headers;

		public Request(URL uri, Map<String, String> headers) {
			this.uri = uri;
			this.headers = headers;
		}
	}

	private class GET extends Request {
		public GET(URL uri, Map<String, String> headers) {
			super(uri, headers);
		}
	}

	private class POST extends Request {
		public byte[] body;

		public POST(URL uri, Map<String, String> headers, byte[] body) {
			super(uri, headers);
			this.body = body;
		}
	}

	private Response getOrPost(Request request) {
		mErrorMessage = null;
		HttpURLConnection conn = null;
		Response response = null;
		try {
			conn = (HttpURLConnection) request.uri.openConnection();
			if (!mAuthenticator.authenticate(conn)) {
				mErrorMessage = str(R.string.aerc_authentication_failed);
			} else {
				if (request.headers != null) {
					for (String header : request.headers.keySet()) {
						String value = request.headers.get(header);
						conn.addRequestProperty(header, value);

					}
				}
				if (request instanceof POST) {
					byte[] payload = ((POST) request).body;
					conn.setDoOutput(true);
					conn.setFixedLengthStreamingMode(payload.length);
					conn.getOutputStream().write(payload);
					int status = conn.getResponseCode();
					if (status / 100 != 2)
						response = new Response(status,
								new Hashtable<String, List<String>>(), conn
										.getResponseMessage().getBytes());
				}

				if (response == null) {
					BufferedInputStream in = new BufferedInputStream(
							conn.getInputStream());
					byte[] body = readStream(in);
					response = new Response(conn.getResponseCode(),
							conn.getHeaderFields(), body);
				}
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
			mErrorMessage = ((request instanceof POST) ? "POST " : "GET ")
					+ str(R.string.aerc_failed);
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return response;
	}

	// utilities
	private static byte[] readStream(InputStream in) throws IOException {
		byte[] buf = new byte[1024];
		int count = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		while ((count = in.read(buf)) != -1)
			out.write(buf, 0, count);
		return out.toByteArray();
	}

	private String str(int id) {
		return mContext.getString(id);
	}

	public Authenticator getmAuthenticator() {
		return mAuthenticator;
	}

	public void setmAuthenticator(Authenticator mAuthenticator) {
		this.mAuthenticator = mAuthenticator;
	}

}
