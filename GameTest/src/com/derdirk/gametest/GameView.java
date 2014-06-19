package com.derdirk.gametest;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.derdirk.bluetooth.Bluetooth;
import com.derdirk.bluetooth.Bluetooth.BluetoothListener;

public class GameView extends View implements BluetoothListener
{ 
  protected enum FillMode {FillHeight, FillWidth}
  
  protected Handler   _viewHandler     = null;
  protected Handler   _gameOverHandler = null;
  protected GameBoard _gameBoard       = new GameBoard();  
  protected Paint     _paintBall       = new Paint();
  protected Paint     _paintPaddle     = new Paint();
  protected Paint     _paintFrame      = new Paint();
  protected FillMode  _fillMode;
  protected float     _toGameboardFactor;
  protected float     _fromGameboardFactor;
  protected float []  _frame;
  
  private Bluetooth _bluetooth = null;
  
  boolean tmpInitBT = true;
  
  public GameView(Context context)
  {
    super(context);
    init();
  }

  public GameView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public GameView(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    init();
  }
  
  protected void init()
  {
    _gameBoard.setAspectRatio(0.5f);
    
    _paintBall.setColor(Color.GREEN);
    _paintPaddle.setColor(Color.BLUE);
    _paintFrame.setColor(Color.BLACK);
    _bluetooth = new Bluetooth(this);
    
    // Handlers
    
    final View touchView = (View)findViewById(R.id.game_view);
    touchView.setOnTouchListener(new View.OnTouchListener()
    {
      @Override
      public boolean onTouch(View v, MotionEvent event)
      {
        _gameBoard.setPaddlePos(translateToGameBoard(event.getX()), translateToGameBoard(event.getY()));
        
        if (_bluetooth.isConnected())
        {
          byte[] msg = MainActivity.PositionToByteArray(event.getX(), event.getY());        
          _bluetooth.send(msg, msg.length);
        }
        
        return true;
      }
    });
    
    _viewHandler = new Handler()
    {
      @Override
      public void handleMessage(Message msg)
      {
        invalidate();                
      }
    };
  }

  @Override
  protected void onSizeChanged (int w, int h, int oldw, int oldh)
  {
    float viewAspectRatio      = (float)getWidth() / (float)getHeight();
    float gameboardAspectRatio = _gameBoard.width() / _gameBoard.height();
    
    if (viewAspectRatio > gameboardAspectRatio)
    {
      _fillMode = FillMode.FillHeight;
      _toGameboardFactor   = _gameBoard.height() / getHeight();
      _fromGameboardFactor = getHeight() / _gameBoard.height();
    }
    else
    {
      _fillMode = FillMode.FillWidth;
      _toGameboardFactor = _gameBoard.width() / getWidth();
      _fromGameboardFactor = getWidth() / _gameBoard.width();
    }
    
    _frame = new float [] {translateFromGameBoard(0f),                 translateFromGameBoard(0f),                  translateFromGameBoard(_gameBoard.width()), translateFromGameBoard(0f), 
                           translateFromGameBoard(_gameBoard.width()), translateFromGameBoard(0f),                  translateFromGameBoard(_gameBoard.width()), translateFromGameBoard(_gameBoard.height()),
                           translateFromGameBoard(_gameBoard.width()), translateFromGameBoard(_gameBoard.height()), translateFromGameBoard(0f),                 translateFromGameBoard(_gameBoard.height()),
                           translateFromGameBoard(0f),                 translateFromGameBoard(_gameBoard.height()), translateFromGameBoard(0f),                 translateFromGameBoard(0f)};
    
    if (tmpInitBT)
    {
      tmpInitBT = false;
      
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
  }
  
  public float translateToGameBoard(float value)
  {
    return _toGameboardFactor * value;
  }
  
  public float translateFromGameBoard(float value)
  {
    return _fromGameboardFactor * value;
  }
  
  public void setGameOverHandler(Handler gameOverHandler)
  {
    _gameOverHandler = gameOverHandler;
  }
  
  @Override
  protected void onDraw(Canvas canvas)
  {  
    super.onDraw(canvas);
    
    Ball   ball   = _gameBoard.ball();
    Paddle paddle = _gameBoard.paddle();
    
    canvas.drawCircle(translateFromGameBoard(ball.postionX),
                      translateFromGameBoard(ball.postionY),
                      translateFromGameBoard(ball.size()),
                      _paintBall);
    
    canvas.drawRect(translateFromGameBoard(paddle.postionX - paddle.width()/2f),
                    translateFromGameBoard(paddle.postionY - paddle.height()/2f),
                    translateFromGameBoard(paddle.postionX + paddle.width()/2f),
                    translateFromGameBoard(paddle.postionY + paddle.height()/2f),
                    _paintPaddle);
    

    
    canvas.drawLines(_frame, 0, 16, _paintFrame);
    
    _gameBoard.proceed();
    
    if (!_gameBoard.gameOver())
      _viewHandler.sendEmptyMessageDelayed(0, 10);
//    else
//      _gameOverHandler.sendEmptyMessage(0); 
  }
  
  @Override
  public void onConnected()
  {
    Log.i("MainActivity", "Connected");
  }

  @Override
  public void onDisconnected()
  {
    Log.i("MainActivity", "Disconnected");
  }

  @Override
  public void onReceive(byte[] msg, int count)
  {
    if (msg.length == 8)
    {
      float x = MainActivity.ByteArrayToPositionX(msg);
      float y = MainActivity.ByteArrayToPositionY(msg);
      _gameBoard.setPaddlePos(x, y);
    }
  }
}
