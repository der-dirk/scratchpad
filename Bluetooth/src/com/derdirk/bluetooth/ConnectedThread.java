package com.derdirk.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

public class ConnectedThread extends Thread implements Callback
{
  private Handler               _readHandler;
  private Handler               _writeHandler;
  private final BluetoothSocket _socket;
  private final InputStream     _inStream;
  private final OutputStream    _outStream;

  public ConnectedThread(BluetoothSocket socket, Handler readHandler)
  {
    _socket             = socket;
    _readHandler        = readHandler;
    InputStream  tmpIn  = null;
    OutputStream tmpOut = null;

    _writeHandler = new Handler(this);
    
    try
    {
      // Get the input and output streams, using temp objects because member streams are final      
      tmpIn  = socket.getInputStream();
      tmpOut = socket.getOutputStream();
    }
    catch (IOException e) {}

    _inStream  = tmpIn;
    _outStream = tmpOut;
  }

  public Handler writeHandler()
  {
    return _writeHandler;
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
        _readHandler.obtainMessage(Bluetooth.BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
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

  @Override
  public boolean handleMessage(Message msg)
  {
    byte[] message = (byte[]) msg.obj;
    write(message);
    return true;
    
    //return false;
  }
}