package fut

import scalaz.effect.IO

trait Avatar {
  def report(s: Stimulus): IO[Unit]
}