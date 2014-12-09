package com.derdirk.gametest;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.derdirk.gametest.GameBoardLink.RemoteListener;

public class GameView extends View implements RemoteListener
{ 
  protected enum FillMode {FillHeight, FillWidth}
  
  protected Handler       _viewHandler     = null;
  protected Handler       _gameOverHandler = null;
  protected GameBoard     _gameBoard       = new GameBoard();
  protected GameBoardLink _gameBoardLink   = null;
  protected Paint         _paintBall       = new Paint();
  protected Paint         _paintPaddle     = new Paint();
  protected Paint         _paintFrame      = new Paint();
  protected FillMode      _fillMode;
  protected float         _toGameboardFactor;
  protected float         _fromGameboardFactor;
  protected float []      _frame;
  
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
    
    // Handlers
    
    final View touchView = (View)findViewById(R.id.game_view);
    touchView.setOnTouchListener(new View.OnTouchListener()
    {
      @Override
      public boolean onTouch(View v, MotionEvent event)
      {
        _gameBoard.setLocalPaddlePos(translateToGameBoard(event.getX()), translateToGameBoard(event.getY()));        
        _gameBoardLink.sendLocalPaddlePosition(new Coordinate(event.getX(), event.getY()));
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
    recalculateGameboardSize();
    
    if (tmpInitBT)
    {
      tmpInitBT = false;      
      _gameBoardLink = new LocalyCoupledGameBoardLink(this);
      _gameBoardLink.init();
    }
  }

  private void recalculateGameboardSize()
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
    
    Ball   ball         = _gameBoard.ball();
    Paddle localPaddle  = _gameBoard.localPaddle();
    Paddle remotePaddle = _gameBoard.remotePaddle();
    
    canvas.drawCircle(translateFromGameBoard(ball.postionX),
                      translateFromGameBoard(ball.postionY),
                      translateFromGameBoard(ball.size()),
                      _paintBall);
    
    canvas.drawRect(translateFromGameBoard(localPaddle.postionX - localPaddle.width()/2f),
                    translateFromGameBoard(localPaddle.postionY - localPaddle.height()/2f),
                    translateFromGameBoard(localPaddle.postionX + localPaddle.width()/2f),
                    translateFromGameBoard(localPaddle.postionY + localPaddle.height()/2f),
                    _paintPaddle);
    
    canvas.drawRect(translateFromGameBoard(remotePaddle.postionX - remotePaddle.width()/2f),
                    translateFromGameBoard(remotePaddle.postionY - remotePaddle.height()/2f),
                    translateFromGameBoard(remotePaddle.postionX + remotePaddle.width()/2f),
                    translateFromGameBoard(remotePaddle.postionY + remotePaddle.height()/2f),
                    _paintPaddle);    
    
    canvas.drawLines(_frame, 0, 16, _paintFrame);
    
    _gameBoard.proceed();
    
    if (!_gameBoard.gameOver())
      _viewHandler.sendEmptyMessageDelayed(0, 10);
//    else
//      _gameOverHandler.sendEmptyMessage(0); 
  }

  @Override
  public void onBallPositionReceived(Coordinate position)
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void onPaddlePositionReceived(Coordinate position)
  {
    _gameBoard.setRemotePaddlePos(translateToGameBoard(position.x), translateToGameBoard(position.y));
  }
}
