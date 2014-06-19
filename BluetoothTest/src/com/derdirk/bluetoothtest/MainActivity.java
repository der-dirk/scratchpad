package com.derdirk.bluetoothtest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.crypto.spec.PSource;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.derdirk.bluetooth.Bluetooth;
import com.derdirk.bluetooth.Bluetooth.BluetoothListener;

public class MainActivity extends ActionBarActivity implements OnClickListener, BluetoothListener
{
  private final static int REQUEST_ENABLE_BT = 1;
  
  private Bluetooth        _bluetooth;
  
  private List<String>         _devicesList = new ArrayList<String>();
  private ArrayAdapter<String> _devicesListAdapter = null;
  
  private Spinner          _deviceSpinner         = null;
  private Button           _connectButton         = null;
  private Button           _disconnectButton      = null;
  private Button           _submitButton          = null;
  private TextView         _remoteMessageTextView = null;
  private EditText         _localMessageEditText  = null;
  private TextView         _connectedTextView     = null;
  
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
				
		_devicesListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _devicesList);
		
		_bluetooth = new Bluetooth(this);
		
		if (_bluetooth.isAvailabled())
		{
      if (!_bluetooth.isEnabled())
      {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
      }
      else
        _bluetooth.startListen();
		}
    else
      Toast.makeText(getApplicationContext(), "No bluetooth available", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override	
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
	  if (requestCode == REQUEST_ENABLE_BT)
	  {
	    if (resultCode == RESULT_OK)
	    {
	      populateDevicesList();
	      _bluetooth.startListen();
	    }
	    else
	    {
  	    Toast.makeText(getApplicationContext(), "Bluetooth was not enabled", Toast.LENGTH_SHORT).show();
	    }
	  }
	}
	
  protected void populateDevicesList()
  {
    _devicesListAdapter.clear();
    Set<BluetoothDevice> pairedDevices = _bluetooth.getBondedDevices();    
    // If there are paired devices
    if (pairedDevices.size() > 0)
    {
      // Loop through paired devices
      for (BluetoothDevice device : pairedDevices)
      {
        // Add the name and address to an array adapter to show in a ListView
        _devicesListAdapter.add(device.getName() /*+ "\n" + device.getAddress()*/);
      }
    }
    else
      _devicesListAdapter.add("<none>");
  }
  
	@Override
	public void onClick(View view)
	{
	  if (view.getId() == R.id.connect_button && _bluetooth.isEnabled())
	  {
	    BluetoothDevice selectedDevice = null;
  	  Set<BluetoothDevice> pairedDevices = _bluetooth.getBondedDevices();
      if (pairedDevices.size() > 0)
      {
        // Loop through paired devices
        for (BluetoothDevice device : pairedDevices)
        {
          String nameSelected = (String) _deviceSpinner.getSelectedItem();
          String nameDevice   = device.getName();
          if (nameDevice.equals(nameSelected))
          {
            selectedDevice = device;
            break;
          }
        }
      }  	  
	    
      if (selectedDevice != null)
    	  _bluetooth.connect(selectedDevice);
	  }
	  else if (view.getId() == R.id.disconnect_button)
	  {
	    _bluetooth.disconnect();
	  }
	  else if (view.getId() == R.id.submit_button)
	  {
	    String message = _localMessageEditText.getText().toString();
      byte [] bytes = message.getBytes();
      int     count = message.length();
      
      _bluetooth.send(bytes, count);
	  }
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
	  
	  public PlaceholderFragment()
	  {
	  }
	  
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState)
	  {
	    View rootView = inflater.inflate(R.layout.fragment_main, container,
	        false);
	    
	    MainActivity act = (MainActivity) getActivity();	    

	    act._deviceSpinner = (Spinner) rootView.findViewById(R.id.devices_spinner);
	    act._deviceSpinner.setAdapter(act._devicesListAdapter);
	    
	    act._connectedTextView = (TextView) rootView.findViewById(R.id.connected_text_view);
	    
	    act._connectButton = (Button) rootView.findViewById(R.id.connect_button);
	    act._connectButton.setOnClickListener(act);

	     act._disconnectButton = (Button) rootView.findViewById(R.id.disconnect_button);
	     act._disconnectButton.setOnClickListener(act);
	      
	    act._submitButton = (Button) rootView.findViewById(R.id.submit_button);
      act._submitButton.setOnClickListener(act);
	    
	    act._remoteMessageTextView = (TextView) rootView.findViewById(R.id.remote_message_text_view);
	    
	    act._localMessageEditText = (EditText) rootView.findViewById(R.id.local_message_edit_text);
	    
	    act.populateDevicesList();
	    
	    return rootView;
	  }
	}

  @Override
  public void onConnected()
  {
    _connectedTextView.setText("Connected");
    
    _connectButton.setEnabled(false);
    _deviceSpinner.setEnabled(false);
    _disconnectButton.setEnabled(true);
    _submitButton.setEnabled(true);
    _localMessageEditText.setEnabled(true);
  }
  
  @Override
  public void onDisconnected()
  {
    _connectedTextView.setText("Disconnected");
    
    _connectButton.setEnabled(true);
    _deviceSpinner.setEnabled(true);
    _disconnectButton.setEnabled(false);
    _submitButton.setEnabled(false);
    _localMessageEditText.setEnabled(false);
    
    _bluetooth.startListen();
  }  
  
  @Override
  public void onReceive(byte[] msg, int count)
  {
    String message = new String(msg);    
    _remoteMessageTextView.setText(message);
  }
  
}
