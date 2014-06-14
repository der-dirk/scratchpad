package com.derdirk.gametest;

import android.graphics.RectF;

class Ball extends MovingThing implements CanCollide
{
  protected float _size;
  
  Ball(float size)
  {
    _size = size;
    
    postionX  = 0.5f;
    postionY  = 0.5f;
    momentumX = 0.015f;
    momentumY = 0.015f;
  }
  
  public float size()
  {
    return _size;
  }

  @Override
  public RectF boundingBox()
  {
    return new RectF(postionX - _size/2f, postionY - _size/2f, postionX + _size/2f, postionY + _size/2f);
  }
}