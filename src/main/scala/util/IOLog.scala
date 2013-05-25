package util

import java.util.logging.Logger
import java.util.logging.Level
import scalaz.effect.IO

class IOLog(name:String) {

  private val Log = Logger.getLogger(name)
  
  def log(level:Level, msg:String, cause:Throwable):IO[Unit] =
    IO(Log.log(level, msg, cause))
  
  def log(level:Level, msg:String):IO[Unit] =
    IO(Log.log(level, msg))
  
  def info(s:String):IO[Unit] =
    log(Level.INFO, s)
    
  def warning(s:String):IO[Unit] =
    log(Level.WARNING, s)
  
  def warning(s:String, t:Throwable):IO[Unit] =
    log(Level.WARNING, s, t)
  
}