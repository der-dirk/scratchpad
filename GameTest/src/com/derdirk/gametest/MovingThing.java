package com.derdirk.gametest;

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