package chan

import ServerChannelWorld._
import io.netty.channel.ChannelHandlerContext
import scalaz.syntax.monad._
import util.IOLog

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
      s <- getState
      s <- s.input(msg) // new state!
      _ <- putState(s)
      _ <- s.prompt
    } yield ()

  def channelInactiveAction: Action[Unit] =
    for {
      r <- remoteAddress
      _ <- Log.info(s"Closed: $r").liftIO[Action]
    } yield ()

  def exceptionCaughtActon(cause: Throwable): Action[Unit] =
    for {
      r <- remoteAddress
      _ <- Log.warning(s"Problem with $r; closing channel.", cause).liftIO[Action]
      _ <- close
    } yield ()
    
}

