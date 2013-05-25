package util

import java.util.UUID
import scalaz.effect.IO

case class Id[A](id: UUID) extends AnyVal

object Id {
  def newRandomId[A]: IO[Id[A]] =
    IO(Id[A](UUID.randomUUID))
}

