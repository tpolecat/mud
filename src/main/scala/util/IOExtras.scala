package util

import scala.concurrent.{ ExecutionContext, Future }
import scalaz.effect.IO

object IOExtras {

  /** Schedule an IO action for eventual execution. */
  def sparkIO(a: IO[Unit])(implicit ec: ExecutionContext): IO[Unit] =
    IO(Future(a.unsafePerformIO))

  /** Immediately fork an IO action on a dedicated thread. */
  def forkIO(a: IO[Unit]): IO[Unit] = IO {
    new Thread {
      override def run: Unit = 
        a.unsafePerformIO
    }.start()
  }

}