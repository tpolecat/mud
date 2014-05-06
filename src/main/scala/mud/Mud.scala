package mud

import scalaz.effect.SafeApp
import scalaz.effect.IO
import scalaz.effect.IO._
import mud.data.DikuData
import chan.Server
import session.Registration
import util.IOLog
import java.io.File

object Mud extends SafeApp {

  val Log = new IOLog("mud", "main")
  val Port = 6011

  override val runc: IO[Unit] =
    for {
      _ <- Log.info(s"Starting up...")
      e <- DikuData.load(new File("data"), "tinyworld")
      _ <- e.fold(f => die(f.toString), runServer)
    } yield ()

  def die(s:String): IO[Unit] =
    for {
      _ <- Log.warning(s)
      _ <- Log.warning("Can't continue. Shutting down.")
      } yield ()

  def runServer(d: Dungeon): IO[Unit] =
    for {
      _ <- Idler.start(d)
      _ <- Server.run(Port, Registration(d))
      _ <- Log.info(s"Clean shutdown.")
    } yield ()
}

