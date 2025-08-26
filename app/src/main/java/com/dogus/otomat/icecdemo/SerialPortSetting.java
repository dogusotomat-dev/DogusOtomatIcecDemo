package com.dogus.otomat.icecdemo;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;

import com.tcn.icecboard.control.TcnShareUseData;
import com.tcn.icecboard.control.TcnVendIF;

import android_serialport_api.SerialPortFinder;

public class SerialPortSetting extends PreferenceActivity {
	private static final String TAG = "SerialPortSetting";
	private SerialPortFinder mSerialPortFinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TcnVendIF.getInstance().LoggerDebug(TAG, "onCreate()");
		mSerialPortFinder = TcnVendIF.getInstance().getSerialPortFinder();

		if (null == mSerialPortFinder) {
			TcnVendIF.getInstance().LoggerDebug(TAG, "onCreate() mSerialPortFinder null");
			return;
		}

		addPreferencesFromResource(R.xml.serial_port_preferences);
		setContentView(R.layout.preference_set);

		TcnVendIF.getInstance().LoggerDebug(TAG, "onCreate() 2");

		// Devices
		final ListPreference devices = (ListPreference) findPreference("MAINDEVICE");
		String[] entries = mSerialPortFinder.getAllDevices();
		String[] entryValues = mSerialPortFinder.getAllDevicesPath();
		devices.setEntries(entries);
		devices.setEntryValues(entryValues);
		devices.setSummary(devices.getValue());
		devices.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary((String) newValue);
				return true;
			}
		});
		devices.setValue(TcnShareUseData.getInstance().getBoardSerPortFirst());

		// Baud rates
		final ListPreference baudrates = (ListPreference) findPreference("MAINBAUDRATE");
		baudrates.setSummary(baudrates.getValue());
		baudrates.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary((String) newValue);
				return true;
			}
		});

		baudrates.setValue(TcnShareUseData.getInstance().getBoardBaudRate());

		// Server Devices
		final ListPreference serverPreference = (ListPreference) findPreference("SERVERDEVICE");
		String[] entriesServer = mSerialPortFinder.getAllDevices();
		String[] entryValuesServer = mSerialPortFinder.getAllDevicesPath();
		serverPreference.setEntries(entriesServer);
		serverPreference.setEntryValues(entryValuesServer);
		serverPreference.setSummary(TcnShareUseData.getInstance().getBoardSerPortSecond());
		serverPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary((String) newValue);
				TcnShareUseData.getInstance().setBoardSerPortSecond((String) newValue);
				return true;
			}
		});
		serverPreference.setValue(TcnShareUseData.getInstance().getBoardSerPortSecond());

	}

	public void serial_back(View v) {
		// TcnVendIF.getInstance().reqSlotNoInfoOpenSerialPort();
		this.finish();
	}
}
