package com.derdirk.gametest;

import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.derdirk.bluetooth.Bluetooth;
import com.derdirk.bluetooth.Bluetooth.BluetoothListener;

public class BluetoothGameBoardLink implements GameBoardLink, BluetoothListener
{
  private Bluetooth      _bluetooth = null;
  private RemoteListener _remoteListener = null;
  
  public BluetoothGameBoardLink(RemoteListener remoteListener)
  {
    _bluetooth = new Bluetooth(this);
    _remoteListener = remoteListener;
  }

  public void init()
  {
    if (_bluetooth.isEnabled())
    {
      BluetoothDevice deviceToConnect = null;
      Set<BluetoothDevice> devices = _bluetooth.getBondedDevices();
      
      if (devices.size() == 1) // TODO
        deviceToConnect = devices.iterator().next();
      else if (devices.size() > 0)
      {
        // Loop through paired devices
        for (BluetoothDevice device : devices)
        {
          if (device.getName() == "Nexus 4")
          {
            deviceToConnect = device;
            break;
          }
        }
      }
      
      if (deviceToConnect != null)
        _bluetooth.connect(deviceToConnect);
    }
  }
  
  public void sendBallPosition(Coordinate position)
  {
    if (_bluetooth.isConnected())
    {
      byte[] msg = MainActivity.PositionToByteArray(position.x, position.y);        
      _bluetooth.send(msg, msg.length);
    }
  }

  public void sendLocalPaddlePosition(Coordinate position)
  {
    return;
  }
  
  @Override
  public void onConnected()
  {
    Log.i("GameBoardBluetoothLink", "Connected");
  }

  @Override
  public void onDisconnected()
  {
    Log.i("GameBoardBluetoothLink", "Disconnected");
  }

  @Override
  public void onReceive(byte[] msg, int count)
  {
    if (msg.length == 8)
    {
      float x = MainActivity.ByteArrayToPositionX(msg);
      float y = MainActivity.ByteArrayToPositionY(msg);
      _remoteListener.onPaddlePositionReceived(new Coordinate(x, y));
    }
  }  
}
