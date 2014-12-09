package com.derdirk.gametest;

interface GameBoardLink
{
  public interface RemoteListener
  {
    public abstract void onBallPositionReceived(Coordinate position);
    public abstract void onPaddlePositionReceived(Coordinate position);
  }

  public abstract void init();
  public abstract void sendBallPosition(Coordinate position);
  public abstract void sendLocalPaddlePosition(Coordinate position);
}
