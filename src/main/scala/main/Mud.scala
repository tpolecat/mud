package main

import scalaz.effect.SafeApp
import scalaz.effect.IO
import scalaz.effect.IO._
import data.DataMain
import fut.Dungeon
import chan.Server
import session.Registration
import util.IOLog

object Mud extends SafeApp {

  val Log = new IOLog("mud.main")
  val port = 6011

  override def runc: IO[Unit] =
    for {
      _ <- Log.info(s"Starting up...")
      d <- DataMain.load.map(new Dungeon(_))
      _ <- Log.info(s"Loaded dungeon with ${d.map.size} rooms.")
      _ <- Server.run(port, Registration(d))
      _ <- Log.info(s"Clean shutdown.")
    } yield ()

}

