package mud

import scalaz.effect.IO

/** Each mobile can have an `Avatar` that can react to stimuli. */
trait Avatar {
  def report(s: Stimulus): IO[Unit]
}