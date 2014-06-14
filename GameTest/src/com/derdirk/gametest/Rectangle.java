package com.derdirk.gametest;

public class Rectangle
{
  protected float _width;
  protected float _height;
  
  Rectangle(float width, float height)
  {
    _width  = width;
    _height = height;
  }
  
  public float width()  {return _width;}
  public float height() {return _height;}
}
