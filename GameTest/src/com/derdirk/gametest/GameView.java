package com.derdirk.gametest;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View
{ 
  
  interface Paintable
  {
    abstract void paint(Canvas canvas);
  }
  
  interface CanCollide
  {
    public abstract Rect boundingBox();
  }

  class CollisionDetector
  {
    private CanCollide _thing1;    
    private CanCollide _thing2;
    
    CollisionDetector(CanCollide thing1, CanCollide thing2)
    {
      _thing1 = thing1;
      _thing2 = thing2;
    }
    
    public Boolean collide()
    {
      return Rect.intersects(_thing1.boundingBox(), _thing2.boundingBox());
    }
    
    // Relates to thing1
    public Boolean collideLeft()
    {
      Rect leftCorner = new Rect(_thing1.boundingBox().left, _thing1.boundingBox().top, _thing1.boundingBox().left, _thing1.boundingBox().bottom);
      return Rect.intersects(leftCorner, _thing2.boundingBox());
    }
    
    // Relates to thing1
    public Boolean collideTop()
    {
      Rect topCorner = new Rect(_thing1.boundingBox().left, _thing1.boundingBox().top, _thing1.boundingBox().right, _thing1.boundingBox().top);
      return Rect.intersects(topCorner, _thing2.boundingBox());
    }
    
    // Relates to thing1
    public Boolean collideRight()
    {
      Rect rightCorner = new Rect(_thing1.boundingBox().right, _thing1.boundingBox().top, _thing1.boundingBox().right, _thing1.boundingBox().bottom);
      return Rect.intersects(rightCorner, _thing2.boundingBox());
    }
    
    // Relates to thing1
    public Boolean collideBottom()
    {
      Rect bottomCorner = new Rect(_thing1.boundingBox().left, _thing1.boundingBox().bottom, _thing1.boundingBox().right, _thing1.boundingBox().bottom);
      return Rect.intersects(bottomCorner, _thing2.boundingBox());
    }
    
  }
  
  class MovingThing 
  {   
    public float postionX;
    public float postionY;
    public float momentumX;
    public float momentumY;
    
    public void swapHorizontal() {momentumX = -momentumX;}
    public void swapVertical()   {momentumY = -momentumY;}
    
    public void move()
    {
      postionY  = postionY + momentumY;
      postionX  = postionX + momentumX;
    }
  }
  
  class Ball extends MovingThing implements Paintable, CanCollide
  {
    protected int   _size;
    protected Paint _paint = new Paint();
    
    Ball(int size)
    {
      _size = size;
      
      postionX  = 100;
      postionY  = 100;
      momentumX =  10;
      momentumY =  10;
      
      _paint.setColor(Color.GREEN);
      _paint.setAntiAlias(true);
      _paint.setTextSize(60);
    }
    
    public int size()
    {
      return _size;
    }
    
    @Override
    public void paint(Canvas canvas)
    {
      canvas.drawCircle(postionX, postionY, _size, _paint);
      
      //String text = Float.toString(_postionX) + "/" + Float.toString(_postionY);
      //canvas.drawText(text, 0, text.length(), _postionX, _postionY + 60, paint);
    }

    @Override
    public Rect boundingBox()
    {
      return new Rect((int)(postionX - _size/2), (int)(postionY - _size/2), (int)(postionX + _size/2), (int)(postionY + _size/2));
    }
  }
  
  class Paddle extends MovingThing implements Paintable, CanCollide
  {
    protected int _sizeX;
    protected int _sizeY;
    protected Paint _paint = new Paint();
    
    Paddle(int sizeX, int sizeY)
    {
      postionX     = 200;
      postionY     = 200;
      momentumX    =   0;
      momentumY    =   0;
     
      _sizeX = sizeX;
      _sizeY = sizeY;
      
      _paint.setColor(Color.BLUE);
      _paint.setAntiAlias(true);
      _paint.setTextSize(60);      
    }
    
    @Override
    public void paint(Canvas canvas)
    {
      canvas.drawRect(postionX - _sizeX/2, postionY - _sizeY/2, postionX + _sizeX/2, postionY + _sizeY/2, _paint);
    }

    @Override
    public Rect boundingBox()
    {
      return new Rect((int)(postionX - _sizeX/2), (int)(postionY - _sizeY/2), (int)(postionX + _sizeX/2), (int)(postionY + _sizeY/2));
    }
    
  }
  
  Handler _viewHandler     = null;
  Handler _gameOverHandler = null;
  Ball    _ball            = new Ball(60);
  Paddle  _paddle          = new Paddle(200, 60);  
  
  public GameView(Context context)
  {
    super(context);
    initViewHandler();
  }

  public GameView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    initViewHandler();
  }

  public GameView(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    initViewHandler();
  }
  
  public void setGameOverHandler(Handler gameOverHandler)
  {
    _gameOverHandler = gameOverHandler;
  }
  
  protected void initViewHandler()
  {
    final View touchView = (View)findViewById(R.id.game_view);
    touchView.setOnTouchListener(new View.OnTouchListener()
      {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
          _paddle.postionX = event.getX();
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
  protected void onDraw(Canvas canvas)
  {  
    super.onDraw(canvas);
    
    _paddle.postionY = canvas.getHeight() - 100;
    
    _ball.paint(canvas);
    _paddle.paint(canvas);
    
    int size = _ball.size();
    
    Boolean touchTop    = (_ball.postionY - size/2) < 0;
    Boolean touchBottom = (_ball.postionY + size/2) > canvas.getHeight();
    Boolean touchLeft   = (_ball.postionX + size/2) < 0;
    Boolean touchRight  = (_ball.postionX + size/2) > canvas.getWidth();
    
//    if ( touchTop || touchBottom || touchLeft || touchRight)
//      _paint.setColor(Color.RED);
    
    Boolean gameOver = false;
    
    if (touchBottom)
    { 
      gameOver = true;
    }
    else if (touchTop)
    { 
      _ball.postionY = size/2 + 1;
      _ball.swapVertical();
    }
    if (touchLeft)
    { 
      _ball.postionX = size/2 + 1;
      _ball.swapHorizontal();
    }
    else if (touchRight)
    { 
      _ball.postionX = canvas.getWidth() - (size/2 + 1);
      _ball.swapHorizontal();
    }
    else
    {
      CollisionDetector cd = new CollisionDetector(_paddle, _ball);
      if (cd.collide())
      {
        if ((cd.collideLeft() || cd.collideRight()) && (cd.collideTop() || cd.collideBottom()))
        {
          _ball.swapHorizontal();
          _ball.swapVertical();
        }
        else if (cd.collideLeft() || cd.collideRight())
          _ball.swapHorizontal();
        else
          _ball.swapVertical();
      }
    }
      
    if (!gameOver)
    {
      _ball.move();
      _paddle.move();
      
      _viewHandler.sendEmptyMessageDelayed(0, 10);
    }
    else if (_gameOverHandler != null)
      _gameOverHandler.sendEmptyMessage(0);
  }
  
}
