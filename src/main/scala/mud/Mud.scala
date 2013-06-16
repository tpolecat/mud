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
      d <- DikuData.load.map(new Dungeon(_))
      _ <- Log.info(s"Loaded dungeon with ${d.map.size} rooms.")
      _ <- Server.run(Port, Registration(d))
      _ <- Log.info(s"Clean shutdown.")
    } yield ()

}

