package com.derdirk.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class AcceptThread extends Thread
{
  private BluetoothAdapter      _bluetoothAdapter = null;
  private BluetoothServerSocket _serverSocket     = null;
  private Handler               _socketConnectedHandler = null;

  public AcceptThread(BluetoothAdapter bluetoothAdapter, Handler socketConnectedHanlder)
  {
    _bluetoothAdapter = bluetoothAdapter;
    _socketConnectedHandler = socketConnectedHanlder;
    
    // Use a temporary object that is later assigned to mmServerSocket,
    // because mmServerSocket is final
    BluetoothServerSocket tmp = null;
    try
    {
      // MY_UUID is the app's UUID string, also used by the client code
      tmp = _bluetoothAdapter.listenUsingRfcommWithServiceRecord(Bluetooth.BT_SERVICE_NAME, UUID.fromString(Bluetooth.BT_SERVICE_UUID));
    }
    catch (IOException e) { }
    _serverSocket = tmp;
  }

  public void run()
  {
    BluetoothSocket socket = null;
    // Keep listening until exception occurs or a socket is returned
    while (true)
    {
      try
      {
        socket = _serverSocket.accept();
      }
      catch (IOException e)
      {
        break;
      }
      // If a connection was accepted
      if (socket != null)
      {
        // Do work to manage the connection (in a separate thread)
        _socketConnectedHandler.obtainMessage(Bluetooth.BT_MESSAGE_SOCKET_CONNECTED, socket).sendToTarget();
        try
        {
          _serverSocket.close();
        } 
        catch (IOException e)
        {
          // Auto-generated catch block
          e.printStackTrace();
        }
        break;
      }
    }
  }

  /** Will cancel the listening socket, and cause the thread to finish */
  public void cancel()
  {
    try
    {
      _serverSocket.close();
    } catch (IOException e)
    {
    }
  }
}