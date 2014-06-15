package com.derdirk.gametest;



class GameBoard
  {
    protected float   _width    = 1.0f;
    protected float   _height   = 1.0f;
    protected Ball    _ball     = new Ball(0.1f);
    protected Paddle  _paddle   = new Paddle(0.4f, 0.1f);
    protected boolean _gameOver = false;
    protected CollisionDetector _cd = new CollisionDetector();
    
    public float   width()    {return _width;}
    public float   height()   {return _height;}    
    public Ball    ball()     {return _ball;}
    public Paddle  paddle()   {return _paddle;}
    public boolean gameOver() {return _gameOver;}
    
    public GameBoard()
    {
      _ball.postionX  = 0.5f;
      _ball.postionY  = 0.5f;
      _ball.momentumX = 0.015f;
      _ball.momentumY = 0.015f;
     
      _paddle.postionX  = 0.5f;
      _paddle.postionY  = 0.5f;
      _paddle.momentumX = 0f;
      _paddle.momentumY = 0f;
    }
    
    public void setAspectRatio(float aspectRatio)
    {
      // The smaller side is always normalized to 1.0
      if (aspectRatio > 1f)
      {
        _width  = aspectRatio;
        _height = 1.0f;
      }
      else
      {
        _width  = 1.0f;
        _height = 1.0f / aspectRatio;        
      }
      
      _paddle.postionY  = 0.9f * _height;
    }
    
    public void onTouch(float x, float y)
    {
      x = Math.min(_width - _paddle.width()/2f, Math.max(_paddle.width()/2f, x));
      _paddle.postionX = x;
    }
    
    public void proceed()
    {
      float size = _ball.size();
      
      boolean touchTop    = (_ball.postionY - size/2f) < 0;
      boolean touchBottom = (_ball.postionY + size/2f) > _height;
      boolean touchLeft   = (_ball.postionX + size/2f) < 0;
      boolean touchRight  = (_ball.postionX + size/2f) > _width;
      
//      if ( touchTop || touchBottom || touchLeft || touchRight)
//        _paint.setColor(Color.RED);
      
      if (touchBottom)
      { 
        //_gameOver = true;
        _ball.postionY = _height - size/2f;
        _ball.swapVertical();
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
        _ball.postionX = _width - size/2f;
        _ball.swapHorizontal();
      }
      else
      {
        _cd.setThings(_paddle, _ball);
        if (_cd.collide())
        {
          if ((_cd.collideLeft() || _cd.collideRight()) && (_cd.collideTop() || _cd.collideBottom()))
          {
            _ball.swapHorizontal();
            _ball.swapVertical();
          }
          else if (_cd.collideLeft() || _cd.collideRight())
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