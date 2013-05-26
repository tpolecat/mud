package util

import java.util.logging.Logger
import java.util.logging.Level
import scalaz.effect.IO
import scalaz.effect.IO._

class IOLog(name: String) {

  def log(level: Level, msg: String, cause: Throwable): IO[Unit] =
    for {
      _ <- log(level, msg)
      _ <- IO(cause.printStackTrace)
    } yield ()

  def log(level: Level, msg: String): IO[Unit] =
    putStrLn(f"$name%15s$level%8s $msg")

  def info(s: String): IO[Unit] =
    log(Level.INFO, s)

  def warning(s: String): IO[Unit] =
    log(Level.WARNING, s)

  def warning(s: String, t: Throwable): IO[Unit] =
    log(Level.WARNING, s, t)

}