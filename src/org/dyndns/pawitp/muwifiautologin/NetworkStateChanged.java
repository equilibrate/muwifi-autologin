package org.dyndns.pawitp.muwifiautologin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class NetworkStateChanged extends BroadcastReceiver {

	static final String TAG = "NetworkStateChanged";
	static final String SSID = "MU-WiFi";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// Check network connected
		NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if (!netInfo.isConnected()) {
			return;
		}
		
		// Check SSID
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (!wifi.getConnectionInfo().getSSID().equalsIgnoreCase(SSID)) {
			return;
		}
		
		// Check preference
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (!prefs.getBoolean(Preferences.KEY_ENABLED, false)) {
			return;
		}
		
		Log.v(TAG, "Connected to the correct network");
		
		Intent i = new Intent(context, MuWifiLogin.class);
		context.startService(i);
	}

}
