package org.dyndns.pawitp.muwifiautologin;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class MuWifiLogin {

	static final String TAG = "MuWifiLogin";
	static final int LOGIN_ERROR_ID = 1;
	
	private Context mContext;
	private SharedPreferences mPrefs;

	public MuWifiLogin(Context context, SharedPreferences prefs) {
		mContext = context;
		mPrefs = prefs;
	}
	
	public void login() {
		MuWifiClient loginClient = new MuWifiClient(mPrefs.getString(Preferences.KEY_USERNAME, null), mPrefs.getString(Preferences.KEY_PASSWORD, null));
		
		try {
			if (loginClient.loginRequired()) {
				Log.v(TAG, "Login required");
				
				loginClient.login();
				Toast.makeText(mContext, R.string.login_successful, Toast.LENGTH_SHORT).show();
				Log.v(TAG, "Login successful");
			} else {
				Log.v(TAG, "No login required");
			}
		} catch (LoginException e) {
			Log.v(TAG, "Login failed");
			
			Intent notificationIntent = new Intent(mContext, ErrorWebView.class);
			notificationIntent.putExtra(ErrorWebView.EXTRA_CONTENT, e.getMessage());
			createNotification(notificationIntent);
		} catch (IOException e) {
			Log.v(TAG, "Login failed: IOException");
			
			Intent notificationIntent = new Intent(mContext, ErrorTextView.class);
			notificationIntent.putExtra(ErrorTextView.EXTRA_CONTENT, e.toString());
			createNotification(notificationIntent);
		}
	}
	
	private void createNotification(Intent notificationIntent) {
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
		
		// TODO better icon
		Notification notification = new Notification(android.R.drawable.stat_sys_warning, mContext.getString(R.string.ticker_login_error), System.currentTimeMillis());
		notification.setLatestEventInfo(mContext, mContext.getString(R.string.notification_login_error_title), mContext.getString(R.string.notification_login_error_text), contentIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		NotificationManager notifMan = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notifMan.notify(LOGIN_ERROR_ID, notification);
	}
	
}
