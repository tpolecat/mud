package chan

import ServerChannelWorld._

/** Trait for application-specific states. */
trait ServerChannelState {
  def prompt: Action[Unit]
  def input(s: String): Action[ServerChannelState]
  def closed: Action[Unit]
}

object ServerChannelState {

  object Closed extends ServerChannelState {

    def prompt: Action[Unit] =
      close
      
    override def input(s: String): Action[ServerChannelState] =
      close.map(_ => this)

    def closed: Action[Unit] =
      close

  }

}

