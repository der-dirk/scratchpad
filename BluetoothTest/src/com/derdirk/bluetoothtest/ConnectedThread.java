package com.derdirk.bluetoothtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectedThread extends Thread
{
  private Handler               _handler;
  private final BluetoothSocket _socket;
  private final InputStream     _inStream;
  private final OutputStream    _outStream;

  public ConnectedThread(BluetoothSocket socket, Handler handler)
  {
    _socket = socket;
    _handler = handler;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    // Get the input and output streams, using temp objects because
    // member streams are final
    try
    {
      tmpIn = socket.getInputStream();
      tmpOut = socket.getOutputStream();
    } catch (IOException e)
    {
    }

    _inStream = tmpIn;
    _outStream = tmpOut;
  }

  public void run()
  {
    byte[] buffer = new byte[1024]; // buffer store for the stream
    int bytes; // bytes returned from read()

    // Keep listening to the InputStream until an exception occurs
    while (true)
    {
      try
      {
        // Read from the InputStream
        bytes = _inStream.read(buffer);
        // Send the obtained bytes to the UI activity
        _handler.obtainMessage(MainActivity.BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
      } catch (IOException e)
      {
        break;
      }
    }
  }

  /* Call this from the main activity to send data to the remote device */
  public void write(byte[] bytes)
  {
    try
    {
      _outStream.write(bytes);
    } catch (IOException e)
    {
    }
  }

  /* Call this from the main activity to shutdown the connection */
  public void cancel()
  {
    try
    {
      _socket.close();
    } catch (IOException e)
    {
    }
  }
}