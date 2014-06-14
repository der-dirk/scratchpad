package com.derdirk.gametest;



class GameBoard
  {
    protected float   _sizeX    = 1.0f;
    protected float   _sizeY    = 1.3f;
    protected Ball    _ball     = new Ball(0.1f);
    protected Paddle  _paddle   = new Paddle(0.4f, 0.1f);
    protected Boolean _gameOver = false;
    
    public float   sizeX()    {return _sizeX;}
    public float   sizeY()    {return _sizeY;}    
    public Ball    ball()     {return _ball;}
    public Paddle  paddle()   {return _paddle;}
    public Boolean gameOver() {return _gameOver;}
    
    
    public void onTouch(float x, float y)
    {
      _paddle.postionX = x;
    }
    
    public void proceed()
    {
      _paddle.postionY = 0.97f;
      
      float size = _ball.size();
      
      Boolean touchTop    = (_ball.postionY - size/2) < 0;
      Boolean touchBottom = (_ball.postionY + size/2) > _sizeY;
      Boolean touchLeft   = (_ball.postionX + size/2) < 0;
      Boolean touchRight  = (_ball.postionX + size/2) > _sizeX;
      
//      if ( touchTop || touchBottom || touchLeft || touchRight)
//        _paint.setColor(Color.RED);
      
      if (touchBottom)
      { 
        _gameOver = true;
      }
      else if (touchTop)
      { 
        _ball.postionY = size/2f;
        _ball.swapVertical();
      }
      if (touchLeft)
      { 
        _ball.postionX = size/2f;
        _ball.swapHorizontal();
      }
      else if (touchRight)
      { 
        _ball.postionX = _sizeX - size/2f;
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
        
      if (!_gameOver)
      {
        _ball.move();
        _paddle.move();

      }             
    }
  }