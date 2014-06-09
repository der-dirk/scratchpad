package com.derdirk.bluetoothtest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener, Callback
{
  public final static String BT_SERVICE_NAME     = "BT_TEXT_SERVICE";
  public final static String BT_SERVICE_UUID     = "08a9f970-eff5-11e3-ac10-0800200c9a66";
  public final static int    BT_MESSAGE_READ     = 1;
  public final static int    BT_MESSAGE_SOCKET_CONNECTED = 2;
  
  private final static int REQUEST_ENABLE_BT = 1;
  
  private BluetoothAdapter     _bluetoothAdapter = null;
  private List<String>         _devicesList = new ArrayList<String>();
  private ArrayAdapter<String> _devicesListAdapter = null;
  private Handler              _btSocketConnectedHandler;
  private Handler              _btReadHandler;
  private AcceptThread         _acceptThread;
  private ConnectThread        _connectThread;
  private ConnectedThread      _connectedThread;
  
  private ListView         _deviceListView        = null;
  private Button           _connectButton         = null;
  private TextView         _remoteMessageTextView = null;
  
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
		_btReadHandler = new Handler(this);
		
    _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (_bluetoothAdapter != null)
    {
      if (!_bluetoothAdapter.isEnabled())
      {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
      }
      else
      {        
        _acceptThread = new AcceptThread(_bluetoothAdapter, _btSocketConnectedHandler);
        _acceptThread.start();
      }
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
	    }
	    else
	    {
  	    Toast.makeText(getApplicationContext(), "Bluetooth was not enabled", Toast.LENGTH_SHORT).show();
  	    _bluetoothAdapter = null;
	    }
	  }
	}
	
  protected void populateDevicesList()
  {
    _devicesListAdapter.clear();
    Set<BluetoothDevice> pairedDevices = _bluetoothAdapter.getBondedDevices();    
    // If there are paired devices
    if (pairedDevices.size() > 0)
    {
      // Loop through paired devices
      for (BluetoothDevice device : pairedDevices)
      {
        // Add the name and address to an array adapter to show in a ListView
        _devicesListAdapter.add(device.getName() + "\n" + device.getAddress());
      }
    }
    else
      _devicesListAdapter.add("<none>");
  }
  
	@Override
	public void onClick(View arg0)
	{
	  Set<BluetoothDevice> pairedDevices = _bluetoothAdapter.getBondedDevices();
	  if (pairedDevices.iterator().hasNext())
	  {
	    _connectThread = new ConnectThread(_bluetoothAdapter, pairedDevices.iterator().next(), _btSocketConnectedHandler);
	    _connectThread.start();
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

	    act._deviceListView = (ListView) rootView.findViewById(R.id.devices_list_view);
	    act._deviceListView.setAdapter(act._devicesListAdapter);
	    
	    act._connectButton = (Button) rootView.findViewById(R.id.connect_button);
	    act._connectButton.setOnClickListener(act);
	    
	    act._remoteMessageTextView = (TextView) rootView.findViewById(R.id.remote_message_text_view);
	    
	    return rootView;
	  }
	}

  @Override
  public boolean handleMessage(Message msg)
  {
    if (msg.what == BT_MESSAGE_READ)
    {
      int    bytes   = msg.arg1;
      byte[] buffer  = (byte[]) msg.obj;
      String message = new String(buffer);
      
      _remoteMessageTextView.setText(message);
      
      return true;
    }
    else if (msg.what == BT_MESSAGE_SOCKET_CONNECTED)
    {
      BluetoothSocket socket = (BluetoothSocket) msg.obj;
      _connectedThread = new ConnectedThread(socket, _btReadHandler);
      return true;
    }
    
    return false;
  }

  
}
