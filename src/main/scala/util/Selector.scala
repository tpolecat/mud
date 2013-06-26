package util

import scala.annotation.tailrec
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz._
import Scalaz._
import scalaz.effect.IO
import scalaz.effect.IO._

object Selector {
  import IOExtras._

  def apply(s: IO[(Option[IO[Unit]], Long)]): IO[Unit] =
    IO {

      val go: IO[Long] =
        for {
          p <- s; (o, t) = p
          _ <- o.fold(ioUnit)(sparkIO)
          _ <- IO(Thread.sleep(t))
        } yield t

      new Thread {
        @tailrec override def run(): Unit = {
          go.unsafePerformIO
          run()
        }
      }.start()
    
    }

}