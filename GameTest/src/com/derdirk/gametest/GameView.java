package com.derdirk.gametest;
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

public class GameView extends View
{ 
  
  Handler   _viewHandler     = null;
  Handler   _gameOverHandler = null;
  GameBoard _gameBoard       = new GameBoard();
  
  protected Paint _paintBall   = new Paint();
  protected Paint _paintPaddle = new Paint();
  
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
    _paintBall.setColor(Color.GREEN);
    _paintBall.setAntiAlias(true);
    _paintBall.setTextSize(60);
    
    _paintPaddle.setColor(Color.BLUE);
    _paintPaddle.setAntiAlias(true);
    _paintPaddle.setTextSize(60);     
    
    final View touchView = (View)findViewById(R.id.game_view);
    touchView.setOnTouchListener(new View.OnTouchListener()
    {
      @Override
      public boolean onTouch(View v, MotionEvent event)
      {
        _gameBoard.onTouch(translateXToGameBoard(event.getX()), translateXToGameBoard(event.getY()));
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

  public float translateXToGameBoard(float x)
  {
    float translated = (x*_gameBoard.sizeX())/(float)getWidth();
//    Log.d("A", "X toGB: " + String.valueOf(x));
//    Log.d("A", "X toGB: " + String.valueOf(_gameBoard.sizeX()));
//    Log.d("A", "X toGB: " + String.valueOf((float)getWidth()));
//    Log.d("A", "X toGB: " + String.valueOf(translated));
    return translated;
  }
  
  public float translateYToGameBoard(float y)
  {
    float translated = (y*_gameBoard.sizeY())/(float)getHeight();
//    Log.d("A", "Y toGB: " + String.valueOf(translated));
    return translated;
  }
  
  public float translateXFromGameBoard(float x)
  {
    float translated = (x*(float)getWidth())/_gameBoard.sizeX();
//    Log.d("A", "X fromGB: " + String.valueOf(translated));
    return translated;
  }
  
  public float translateYFromGameBoard(float y)
  {
    float translated = (y*(float)getHeight())/_gameBoard.sizeY();
//    Log.d("A", "Y fromGB: " + String.valueOf(translated));
    return translated;
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
    
    canvas.drawCircle(translateXFromGameBoard(ball.postionX),
                      translateYFromGameBoard(ball.postionY),
                      translateXFromGameBoard(ball.size()), // TODO: Translation wrong
                      _paintBall);
    
    canvas.drawRect(translateXFromGameBoard(paddle.postionX - paddle.width()/2f),
                    translateYFromGameBoard(paddle.postionY - paddle.height()/2f),
                    translateXFromGameBoard(paddle.postionX + paddle.width()/2f),
                    translateYFromGameBoard(paddle.postionY + paddle.height()/2f),
                    _paintPaddle);
    
    _gameBoard.proceed();
    
    if (!_gameBoard.gameOver())
      _viewHandler.sendEmptyMessageDelayed(0, 10);
//    else
//      _gameOverHandler.sendEmptyMessage(0); 
  }
  
}
