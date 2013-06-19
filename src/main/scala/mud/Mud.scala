package mud

import scalaz.effect.SafeApp
import scalaz.effect.IO
import scalaz.effect.IO._
import mud.data.DikuData
import chan.Server
import session.Registration
import util.IOLog

object Mud extends SafeApp {

  val Log = new IOLog("mud", "main")
  val Port = 6011

  override def runc: IO[Unit] =
    for {
      _ <- Log.info(s"Starting up...")
      s <- DikuData.load
      _ <- Server.run(Port, Registration(new Dungeon(s)))
      _ <- Log.info(s"Clean shutdown.")
    } yield ()

}

