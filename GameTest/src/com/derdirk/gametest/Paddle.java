package com.derdirk.gametest;

import android.graphics.RectF;

class Paddle extends MovingThing implements CanCollide
{
  protected float _width;
  protected float _height;
  protected RectF _boundingBox = new RectF();
  
  Paddle(float sizeX, float sizeY)
  {
    _width = sizeX;
    _height = sizeY;
  }
  
  public float width()  {return _width;}
  public float height() {return _height;}
  
  @Override
  public RectF boundingBox()
  {
    _boundingBox.set(postionX - _width/2f, postionY - _height/2f, postionX + _width/2f, postionY + _height/2f);
    return _boundingBox;
  }
  
}