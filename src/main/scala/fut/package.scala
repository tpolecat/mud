
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.stm.InTxn
import scala.concurrent.stm.atomic

import scalaz.Scalaz._
import scalaz.effect.IO
import scalaz.effect.IO.ioMonadCatchIO
import scalaz.effect.IO.ioUnit

package object fut {

  sealed trait Direction
  case object North extends Direction
  case object South extends Direction
  case object East extends Direction
  case object West extends Direction
  case object Up extends Direction
  case object Down extends Direction

  case class Portal(dest: Room)

  case class Item(name: String)

}