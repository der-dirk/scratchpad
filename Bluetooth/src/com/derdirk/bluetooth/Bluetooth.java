package com.derdirk.bluetooth;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

public class Bluetooth implements Callback
{
  public interface BluetoothListener
  {
    public abstract void onConnected();
    public abstract void onDisconnected();
    public abstract void onReceive(byte [] msg, int count);
  }
  
  public final static String BT_SERVICE_NAME     = "BT_TEXT_SERVICE";
  public final static String BT_SERVICE_UUID     = "08a9f970-eff5-11e3-ac10-0800200c9a66";
  public final static int    BT_MESSAGE_READ     = 1;
  public final static int    BT_MESSAGE_WRITE    = 2;
  public final static int    BT_MESSAGE_SOCKET_CONNECTED    = 3;
  public final static int    BT_MESSAGE_SOCKET_DISCONNECTED = 4;
  
  private BluetoothAdapter     _bluetoothAdapter = null;
  private Handler              _btSocketConnectedHandler = null;
  private Handler              _btSocketDisconnectedHandler = null;
  private Handler              _btReadHandler  = null;
  private Handler              _btWriteHandler = null;
  private AcceptThread         _acceptThread;
  private ConnectThread        _connectThread;
  private ConnectedThread      _connectedThread;
  
  private BluetoothListener     _bluetoothListener;
  
  public Bluetooth(BluetoothListener bluetoothListener)
  {
    _bluetoothListener           = bluetoothListener;
    _btReadHandler               = new Handler(this);
    _btSocketConnectedHandler    = new Handler(this);
    _btSocketDisconnectedHandler = new Handler(this);
    _bluetoothAdapter            = BluetoothAdapter.getDefaultAdapter();
  }
  
  public boolean isAvailabled()
  {
    return _bluetoothAdapter != null;
  }
  
  public boolean isEnabled()
  {
    return _bluetoothAdapter != null && _bluetoothAdapter.isEnabled();
  }
  
  public Set<BluetoothDevice> getBondedDevices()
  {
    return _bluetoothAdapter.getBondedDevices();
  }
  
  public void startListen()
  {
    if (_acceptThread == null)
    {
      _acceptThread = new AcceptThread(_bluetoothAdapter, _btSocketConnectedHandler);
      _acceptThread.start();
    }
    else
      Log.w("Bluetooth", "Tried to start listening with an already started AcceptThread");
  }
  
  public void stopListen()
  {
    if (_acceptThread != null)
    {
      _acceptThread.cancel();
      _acceptThread = null;
    }
  }
  
  public void connect(BluetoothDevice device)
  {
    stopListen();
    
    if (!isConnected())
    {
      if (_connectThread == null)
      {    
        _connectThread = new ConnectThread(_bluetoothAdapter, device, _btSocketConnectedHandler);
        _connectThread.start();
      }
      else
        Log.w("Bluetooth", "Tried to connect with an already started ConnectThread");
    }
    else
      Log.i("Bluetooth", "Tried to connect when already connected");
  }
  
  public void disconnect()
  {
    if (_connectedThread != null)
    {
      _connectedThread.cancel();
      _connectedThread = null;
    }
  }
  
  public boolean isConnected()
  {
    return _connectedThread != null;
  }
  
  public void send(byte[] msg, int count)
  {
    if (_btWriteHandler != null)
      _btWriteHandler.obtainMessage(BT_MESSAGE_WRITE, msg).sendToTarget();
  }
  
  @Override
  public boolean handleMessage(Message msg)
  {
    if (msg.what == BT_MESSAGE_READ)
    {
      int    bytes   = msg.arg1;
      byte[] buffer  = (byte[]) msg.obj;
      _bluetoothListener.onReceive(buffer, bytes);
      
      return true;
    }
    else if (msg.what == BT_MESSAGE_SOCKET_CONNECTED)
    {
      if (!isConnected())
      {
        BluetoothSocket socket = (BluetoothSocket) msg.obj;
        _connectedThread = new ConnectedThread(socket, _btReadHandler, _btSocketDisconnectedHandler);
        _btWriteHandler  = _connectedThread.writeHandler();
        _connectedThread.start();        
        _bluetoothListener.onConnected();
      }
      else
        Log.w("Bluetooth", "Received a connection callback with an already started ConnectedThread");
        
      return true;
    }
    else if (msg.what == BT_MESSAGE_SOCKET_DISCONNECTED)
    {
      _bluetoothListener.onDisconnected();

      return true;
    }
    
    return false;
  }
}
