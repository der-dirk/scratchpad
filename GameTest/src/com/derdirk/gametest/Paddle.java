package com.derdirk.gametest;

import android.graphics.RectF;

class Paddle extends MovingThing implements CanCollide
{
  protected float _width;
  protected float _height;
  
  Paddle(float sizeX, float sizeY)
  {
    postionX     = 0.5f;
    postionY     = 0.95f;
    momentumX    = 0f;
    momentumY    = 0f;
   
    _width = sizeX;
    _height = sizeY;
  }
  
  public float width()  {return _width;}
  public float height() {return _height;}
  
  @Override
  public RectF boundingBox()
  {
    return new RectF(postionX - _width/2f, postionY - _height/2f, postionX + _width/2f, postionY + _height/2f);
  }
  
}