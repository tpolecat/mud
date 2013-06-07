
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.stm.InTxn
import scala.concurrent.stm.atomic

import scalaz.Scalaz._
import scalaz.effect.IO
import scalaz.effect.IO.ioMonadCatchIO
import scalaz.effect.IO.ioUnit

package object mud {

  case class Portal(dest: Room)

  case class Item(name: String)

}