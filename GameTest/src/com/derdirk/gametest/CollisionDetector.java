package com.derdirk.gametest;

import android.graphics.RectF;

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
    return RectF.intersects(_thing1.boundingBox(), _thing2.boundingBox());
  }
  
  // Relates to thing1
  public Boolean collideLeft()
  {
    RectF leftCorner = new RectF(_thing1.boundingBox().left, _thing1.boundingBox().top, _thing1.boundingBox().left, _thing1.boundingBox().bottom);
    return RectF.intersects(leftCorner, _thing2.boundingBox());
  }
  
  // Relates to thing1
  public Boolean collideTop()
  {
    RectF topCorner = new RectF(_thing1.boundingBox().left, _thing1.boundingBox().top, _thing1.boundingBox().right, _thing1.boundingBox().top);
    return RectF.intersects(topCorner, _thing2.boundingBox());
  }
  
  // Relates to thing1
  public Boolean collideRight()
  {
    RectF rightCorner = new RectF(_thing1.boundingBox().right, _thing1.boundingBox().top, _thing1.boundingBox().right, _thing1.boundingBox().bottom);
    return RectF.intersects(rightCorner, _thing2.boundingBox());
  }
  
  // Relates to thing1
  public Boolean collideBottom()
  {
    RectF bottomCorner = new RectF(_thing1.boundingBox().left, _thing1.boundingBox().bottom, _thing1.boundingBox().right, _thing1.boundingBox().bottom);
    return RectF.intersects(bottomCorner, _thing2.boundingBox());
  }
  
}