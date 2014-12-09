package com.derdirk.gametest;


public class LocalyCoupledGameBoardLink implements GameBoardLink
{
  private RemoteListener _remoteListener = null;
  
  public LocalyCoupledGameBoardLink(RemoteListener remoteListener)
  {
    _remoteListener = remoteListener;
  }
  
  @Override
  public void init() {}

  @Override
  public void sendBallPosition(Coordinate position)
  {
    _remoteListener.onBallPositionReceived(position);
  }

  @Override
  public void sendLocalPaddlePosition(Coordinate position)
  {
    _remoteListener.onPaddlePositionReceived(position);
  }

}
