package chan

import ServerChannelWorld._

/** Trait for application-specific states. */
trait SessionState {
  def prompt: Action[Unit]
  def input(s: String): Action[SessionState]
  def closed: Action[Unit]
}

object SessionState {

  object Closed extends SessionState {

    def prompt: Action[Unit] =
      unit()

    def input(s: String): Action[SessionState] =
      close.map(_ => this) // we should never see input, but on the off chance we do, close.

    def closed: Action[Unit] =
      unit()

  }

}

