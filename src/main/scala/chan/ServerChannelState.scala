package chan

import ServerChannelWorld._

/** Trait for application-specific states. */
trait ServerChannelState {

  /** Action to provide a prompt to the user. */
  def prompt: Action[Unit]

  /** Action to respond to input from the user, returning the new state. */
  def input(s: String): Action[ServerChannelState]

  /** Action in response to the channel closing. */
  def closed: Action[Unit]

}

object ServerChannelState {

  /** A general-purpose closed state. */
  object Closed extends ServerChannelState {

    val prompt: Action[Unit] = 
      close

    override def input(s: String): Action[ServerChannelState] = 
      close.map(_ => this)
    
    val closed: Action[Unit] = 
      close

  }

}

