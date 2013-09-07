package com.example.receiptme;

import android.os.Bundle;
import android.view.Menu;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.BroadcastReceiver;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.bump.api.IBumpAPI;
import com.bump.api.BumpAPIIntents;

/**
 * Following https://github.com/bumptech/bump-api-android for setting up bump.
 */
public class MainActivity extends Activity {

	private IBumpAPI api;



	private final ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			Log.i("BumpTest", "onServiceConnected");
			api = IBumpAPI.Stub.asInterface(binder);
			try {
				api.configure("997576e85d3243f2a43358d347c7f848",
						"Bump User");
			} catch (RemoteException e) {
				Log.w("BumpTest", e);
			}
			Log.d("Bump Test", "Service connected");
		}
    };

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			try {
				if (action.equals(BumpAPIIntents.DATA_RECEIVED)) {
					Log.i("Bump Test", "Received data from: " + api.userIDForChannelID(intent.getLongExtra("channelID", 0)));
					Log.i("Bump Test", "Data: " + new String(intent.getByteArrayExtra("data")));
				} else if (action.equals(BumpAPIIntents.MATCHED)) {
					long channelID = intent.getLongExtra("proposedChannelID", 0);
					Log.i("Bump Test", "Matched with: " + api.userIDForChannelID(channelID));
					api.confirm(channelID, true);
					Log.i("Bump Test", "Confirm sent");
				} else if (action.equals(BumpAPIIntents.CHANNEL_CONFIRMED)) {
					long channelID = intent.getLongExtra("channelID", 0);
					Log.i("Bump Test", "Channel confirmed with " + api.userIDForChannelID(channelID));
					api.send(channelID, "Hello, world!".getBytes());
				} else if (action.equals(BumpAPIIntents.NOT_MATCHED)) {
					Log.i("Bump Test", "Not matched.");
				} else if (action.equals(BumpAPIIntents.CONNECTED)) {
					Log.i("Bump Test", "Connected to Bump...");
					api.enableBumping();
				}
			} catch (RemoteException e) {}
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		bindService(new Intent(IBumpAPI.class.getName()),
				connection, Context.BIND_AUTO_CREATE);
		Log.i("BumpTest", "boot");

		IntentFilter filter = new IntentFilter();
		filter.addAction(BumpAPIIntents.CHANNEL_CONFIRMED);
		filter.addAction(BumpAPIIntents.DATA_RECEIVED);
		filter.addAction(BumpAPIIntents.NOT_MATCHED);
		filter.addAction(BumpAPIIntents.MATCHED);
		filter.addAction(BumpAPIIntents.CONNECTED);
		registerReceiver(receiver, filter);
	}

	public void onStart() {
		Log.i("BumpTest", "onStart");
		super.onStart();
	}

	public void onRestart() {
		Log.i("BumpTest", "onRestart");
		super.onRestart();
	}

	public void onResume() {
		Log.i("BumpTest", "onResume");
		super.onResume();
	}

	public void onPause() {
		Log.i("BumpTest", "onPause");
		super.onPause();
	}

	public void onStop() {
		Log.i("BumpTest", "onStop");
		super.onStop();
	}

	public void onDestroy() {
		Log.i("BumpTest", "onDestroy");
		unbindService(connection);
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
