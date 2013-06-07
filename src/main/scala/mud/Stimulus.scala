package mud

sealed trait Stimulus

case class Look(info:GameState#RoomInfo) extends Stimulus
case class EnterGame(m:Mobile) extends Stimulus
case class ExitGame(m:Mobile) extends Stimulus
case class Exit(m:Mobile, d:Direction) extends Stimulus
case class Enter(m:Mobile) extends Stimulus
case class NoExit(d:Direction) extends Stimulus

case object Wat extends Stimulus

