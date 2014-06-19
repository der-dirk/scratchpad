package com.derdirk.gametest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.derdirk.bluetooth.Bluetooth;
import com.derdirk.bluetooth.Bluetooth.BluetoothListener;

public class MainActivity extends ActionBarActivity
{
  public Handler _gameOverHandler = new Handler()
  {
    @Override
    public void handleMessage(Message msg)
    {
      gameOver();                
    }
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState == null)
    {
      getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
    }
    
//    GameView gameView = (GameView)findViewById(R.id.game_view);
//    gameView.setGameOverHandler(_gameOverHandler);
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

  /**
   * A placeholder fragment containing a simple view.
   */
  public static class PlaceholderFragment extends Fragment
  {

    public PlaceholderFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
      View rootView = inflater.inflate(R.layout.fragment_main, container, false);
      return rootView;
    }
  }
  
  public void gameOver()
  {
    TextView gameOverTextView = (TextView)findViewById(R.id.game_over_text_view);
    gameOverTextView.setVisibility(TextView.VISIBLE);
  }

  static public byte[] PositionToByteArray(float x, float y)
  {
    byte[] bytes = new byte[8];
    
    int xBits = Float.floatToIntBits(x);
    bytes[0] = (byte) (xBits        & 0xff);
    bytes[1] = (byte)((xBits >> 8)  & 0xff);
    bytes[2] = (byte)((xBits >> 16) & 0xff);
    bytes[3] = (byte)((xBits >> 24) & 0xff);
    
    int yBits = Float.floatToIntBits(y);
    bytes[4] = (byte) (yBits        & 0xff);
    bytes[5] = (byte)((yBits >> 8)  & 0xff);
    bytes[6] = (byte)((yBits >> 16) & 0xff);
    bytes[7] = (byte)((yBits >> 24) & 0xff);
    
    return bytes;
  }
  
  static public float ByteArrayToPositionX(byte[] bytes)
  {
    if (bytes.length != 8)
      return 0f;
    
    int xBits =     (int) (bytes[0]       );
    xBits = xBits | (int)((bytes[1] <<  8));
    xBits = xBits | (int)((bytes[2] << 16));
    xBits = xBits | (int)((bytes[3] << 24));
    
    return Float.intBitsToFloat(xBits);
  }
  
  static public float ByteArrayToPositionY(byte[] bytes)
  {
    if (bytes.length != 8)
      return 0f;

    int yBits =     (int) (bytes[4]       );
    yBits = yBits | (int)((bytes[5] <<  8));
    yBits = yBits | (int)((bytes[6] << 16));
    yBits = yBits | (int)((bytes[7] << 24));
    
    return Float.intBitsToFloat(yBits);
  }
  
}
