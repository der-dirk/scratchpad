package com.derdirk.bluetoothtest;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectThread extends Thread
{
  private BluetoothAdapter      _bluetoothAdapter;
  private final BluetoothSocket _socket;
  private final BluetoothDevice _device;
  private Handler               _socketConnectedHandler = null;

  public ConnectThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice device, Handler socketConnectedHandler)
  {
    _bluetoothAdapter = bluetoothAdapter;
    _socketConnectedHandler = socketConnectedHandler;
    
    // Use a temporary object that is later assigned to mmSocket,
    // because mmSocket is final
    BluetoothSocket tmp = null;
    _device = device;

    // Get a BluetoothSocket to connect with the given BluetoothDevice
    try
    {
      // MY_UUID is the app's UUID string, also used by the server code
      tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MainActivity.BT_SERVICE_UUID));
    }
    catch (IOException e)
    {
    }
    _socket = tmp;
  }

  public void run()
  {
    // Cancel discovery because it will slow down the connection
    _bluetoothAdapter.cancelDiscovery();

    try
    {
      // Connect the device through the socket. This will block
      // until it succeeds or throws an exception
      _socket.connect();
      _socketConnectedHandler.obtainMessage(MainActivity.BT_MESSAGE_SOCKET_CONNECTED, _socket).sendToTarget();
    }
    catch (IOException connectException)
    {
      // Unable to connect; close the socket and get out
      try
      {
        _socket.close();
      }
      catch (IOException closeException)
      {
      }
      return;
    }
  }

  /** Will cancel an in-progress connection, and close the socket */
  public void cancel()
  {
    try
    {
      _socket.close();
    }
    catch (IOException e)
    {
    }
  }
}