package chan

import ServerChannelWorld._

trait ServerChannelActions {
	  
  def channelActiveAction(initialState: SessionState): Action[Unit] =
    for {
      r <- remoteAddress
      _ <- Log.info(s"Opened: $r").liftIO[Action]
      s <- putState(initialState)
      _ <- s.prompt
    } yield ()

  def messageReceivedAction(msg: String): Action[Unit] =
    for {
      a <- getState
      b <- a.input(msg)
      _ <- putState(b)
      _ <- b.prompt
    } yield ()

  def channelInactiveAction: Action[Unit] =
    for {
      r <- remoteAddress
      _ <- Log.info(s"Closed: $r").liftIO[Action]
      a <- getState
      _ <- a.closed
    } yield ()

  def exceptionCaughtActon(cause: Throwable): Action[Unit] =
    for {
      r <- remoteAddress
      _ <- Log.warning(s"Problem with $r; closing channel.", cause).liftIO[Action]
      _ <- close
    } yield ()
    
}

