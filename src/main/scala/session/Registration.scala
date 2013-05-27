package session

import chan.ServerChannelWorld._
import fut._
import chan.SessionState

case class Registration(d: Dungeon) extends SessionState {

  def prompt: Action[Unit] =
    write(s"What is your name? ")

  def input(s: String): Action[SessionState] =
    if (s.trim.isEmpty)
      kick("Ok, nevermind.").map(_ => SessionState.Closed)
    else
      for {
        b <- d.playerExists(s).liftIO[Action]
        s <- if (b) tryAgain(s) else create(s)
      } yield s

  def tryAgain(s: String): Action[SessionState] =
    writeLn(s"Hmm, $s is already playing. Try again.").map(_ => this)

  def create(s: String): Action[SessionState] =
    for {
      m <- unit(Mobile(s))
      w <- writer
      _ <- d.setAvatar(m, new DefaultTextAvatar(m, w)).liftIO[Action]
      _ <- d.intro(m).liftIO[Action]
      r <- remoteAddress
      _ <- Log.info(s"Registered: $r as ${m.name}").liftIO[Action]
    } yield Playing(d, m)

  def closed: Action[Unit] =
    unit()

}


