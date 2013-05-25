package fut

import scalaz.effect.IO

case class Mobile(report: String => IO[Unit], name: String)
