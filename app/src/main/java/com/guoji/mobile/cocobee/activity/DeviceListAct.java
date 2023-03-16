package com.guoji.mobile.cocobee.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;

import java.util.Set;

/**
 * 蓝牙设备搜索列表页面
 * This Activity appears as a dialog. It lists any paired devices and devices
 * detected in the area after discovery. When a device is chosen by the user,
 * the MAC address of the device is sent back to the parent Activity in the
 * result Intent.
 */
public class DeviceListAct extends BaseAct {
	// Debugging
	// private static final String TAG = "DeviceListAct";

	// Return Intent extra
	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	// Member fields
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;

	private TextView title = null;
	private ProgressBar loading = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);

		// Set result CANCELED in case the user backs out
		setResult(Activity.RESULT_CANCELED);

		// Initialize array adapters. One for already paired devices and
		// one for newly discovered devices
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		title = (TextView) super.findViewById(R.id.dialog_title);
		loading = (ProgressBar) super.findViewById(R.id.loading);

		// Find and set up the ListView for paired devices
		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);
		// Find and set up the ListView for newly discovered devices
		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);

		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);
		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		findViewById(R.id.title_paired_devices);
		findViewById(R.id.title_new_devices);
		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0) {
			// for(BluetoothDevice device : pairedDevices){
			// mPairedDevicesArrayAdapter.add(device.getName() + "      " +
			// device.getAddress());
			// }
		} else {
			mPairedDevicesArrayAdapter.add(getResources().getString(R.string.not_get_match_device));
			
		}

		// If we're already discovering, stop it
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}
		// Request discover from BluetoothAdapter
		mBtAdapter.startDiscovery();

		title.setText(getResources().getString(R.string.getting_device));
		loading.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onDestroy() {


		// Make sure we're not doing discovery anymore
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}

		// Unregister broadcast listeners
		this.unregisterReceiver(mReceiver);

		super.onDestroy();
	}

	// The on-click listener for all devices in the ListViews
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// Cancel discovery because it's costly and we're about to connect
			mBtAdapter.cancelDiscovery(); //连接前取消发现
			// Get the device MAC address, which is the last 17 chars in the
			// View
			String info = ((TextView) v).getText().toString();
			if (!info.equals(getResources().getString(R.string.not_get_other_device))
					&& !info.equals(getResources().getString(R.string.not_get_match_device))) {

				String address = info.substring(info.length() - 17);
				// Create the result Intent and include the MAC address
				Intent intent = new Intent();

				intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
				
				Log.v("TAGTAG", "address:" +address);
				
				// Set result and finish this Activity
				setResult(Activity.RESULT_OK, intent);
			}
			finish();
		}
	};

	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// new device found
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					boolean add_flag = true;
					// if the adapter had, just not add.
					for (int i = 0; i < mNewDevicesArrayAdapter.getCount(); i++) {
						if (mNewDevicesArrayAdapter.getItem(i).contains(device.getAddress())) {
							add_flag = false;
							break;
						}
					}
					if (add_flag) {
						mNewDevicesArrayAdapter.add(device.getName() + "      "+ device.getAddress());
						
						Log.v("TAGTAG", "mNewDevicesArrayAdapter.add:" +device.getName() + "      "+ device.getAddress());
					}
					// paired history device found
				} else {
					boolean add_flag = true;
					// if the adapter had, just not add.
					for (int i = 0; i < mPairedDevicesArrayAdapter.getCount(); i++) {
						if (mPairedDevicesArrayAdapter.getItem(i).contains(device.getAddress())) {
							add_flag = false;
							break;
						}
					}
					if (add_flag) {
						mPairedDevicesArrayAdapter.add(device.getName()+ "      " + device.getAddress());
						
						Log.v("TAGTAG", "mPairedDevicesArrayAdapter.add:" +device.getName() + "      "+ device.getAddress());
					}
				}

				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				title.setText(getResources().getString(R.string.select_device));
				loading.setVisibility(View.GONE);

				if (mNewDevicesArrayAdapter.getCount() == 0) {
					mNewDevicesArrayAdapter.add(getResources().getString(R.string.not_get_other_device));
					
				}
				if (mPairedDevicesArrayAdapter.getCount() == 0) {

					mPairedDevicesArrayAdapter.add(getResources().getString(R.string.not_get_match_device));
					
				}
			}
		}
	};
}
