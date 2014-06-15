package com.derdirk.gametest;

import android.graphics.RectF;

class Ball extends MovingThing implements CanCollide
{
  protected float _size;
  protected RectF _boundingBox = new RectF();
  
  Ball(float size)
  {
    _size = size;
  }
  
  public float size()
  {
    return _size;
  }

  @Override
  public RectF boundingBox()
  {
    _boundingBox.set(postionX - _size/2f, postionY - _size/2f, postionX + _size/2f, postionY + _size/2f);
    return _boundingBox;
  }
}