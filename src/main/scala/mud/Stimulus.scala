package mud

sealed trait Stimulus

/** You have looked around, here is what you see. */
case class Look(info: GameState#RoomInfo) extends Stimulus

/** You have asked to examine a specific thing. */
case class Examine(desc: Option[String]) extends Stimulus

/** Someone has entered the game (possibly you). */
case class EnterGame(m: Mobile) extends Stimulus

/** Someone has left the game (possibly you). */
case class ExitGame(m: Mobile) extends Stimulus

/** Someone has left the room (possibly you). */
case class Exit(m: Mobile, d: Direction) extends Stimulus

/** Someone has entered the room (possibly you). */
case class Enter(m: Mobile) extends Stimulus

/** You can't go that way. */
case class NoExit(d: Direction) extends Stimulus

/** I don't understand. */
case object Wat extends Stimulus

