package chan

import ServerChannelWorld._

/** 
 * A trait for actions to be run in response to Netty channel events. These are effectful actions
 * as defined by ServerChannelWorld.
 */
trait ServerChannelActions {
	
  /** Initialize a new channel and prompt for input. */
  def channelActiveAction(initialState: ServerChannelState): Action[Unit] =
    for {
      r <- remoteAddress
      _ <- Log.info(s"Opened: $r").liftIO[Action]
      s <- putState(initialState)
      _ <- s.prompt
    } yield ()

  /** 
   * Route an input line to the current state's handler, potentially resulting in a state 
   * transition. Prompt for more input.
   */
  def messageReceivedAction(msg: String): Action[Unit] =
    for {
      a <- getState
      b <- a.input(msg)
      _ <- putState(b)
      _ <- b.prompt
    } yield ()

  /** Route a closing event to the current state's handler. */
  def channelInactiveAction: Action[Unit] =
    for {
      r <- remoteAddress
      _ <- Log.info(s"Closed: $r").liftIO[Action]
      a <- getState
      _ <- a.closed
    } yield ()

  /** If there is an exception, log it and immediately close the channel. */
  def exceptionCaughtActon(cause: Throwable): Action[Unit] =
    for {
      r <- remoteAddress
      _ <- Log.warning(s"Problem with $r; closing channel.", cause).liftIO[Action]
      _ <- close
    } yield ()
    
}

