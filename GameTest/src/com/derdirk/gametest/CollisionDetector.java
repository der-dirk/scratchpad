package com.derdirk.gametest;

import android.graphics.RectF;

class CollisionDetector
{
  private CanCollide _thing1;    
  private CanCollide _thing2;
  private RectF      _cornerRect = new RectF();
  
  public void setThings(CanCollide thing1, CanCollide thing2)
  {
    _thing1 = thing1;
    _thing2 = thing2; 
  }
  
  public boolean collide()
  {
    return RectF.intersects(_thing1.boundingBox(), _thing2.boundingBox());
  }
  
  // Relates to thing1
  public boolean collideLeft()
  {
    // left corner
    _cornerRect.set(_thing1.boundingBox().left, _thing1.boundingBox().top, _thing1.boundingBox().left, _thing1.boundingBox().bottom);
    return RectF.intersects(_cornerRect, _thing2.boundingBox());
  }
  
  // Relates to thing1
  public boolean collideTop()
  {
    // top corner
    _cornerRect.set(_thing1.boundingBox().left, _thing1.boundingBox().top, _thing1.boundingBox().right, _thing1.boundingBox().top);
    return RectF.intersects(_cornerRect, _thing2.boundingBox());
  }
  
  // Relates to thing1
  public boolean collideRight()
  {
    // right corner
    _cornerRect.set(_thing1.boundingBox().right, _thing1.boundingBox().top, _thing1.boundingBox().right, _thing1.boundingBox().bottom);
    return RectF.intersects(_cornerRect, _thing2.boundingBox());
  }
  
  // Relates to thing1
  public boolean collideBottom()
  {
    // bottom corner
    _cornerRect.set(_thing1.boundingBox().left, _thing1.boundingBox().bottom, _thing1.boundingBox().right, _thing1.boundingBox().bottom);
    return RectF.intersects(_cornerRect, _thing2.boundingBox());
  }
  
}