package fut

import scalaz.Scalaz._
import scalaz.effect.IO
import scalaz.effect.IO.ioMonadCatchIO
import scalaz.effect.IO.ioUnit

class Dungeon(val map: Map[Room, Map[Direction, Portal]]) {

  val state = new State(map)
  import state._

  def reportRoom(m: Mobile)(info: RoomInfo): IO[Unit] =
    m.report {
      s"""|
          |== ${info.room.name} ==
          |
          |${info.room.desc.trim}
          |${info.mobiles.filterNot(_ == m).map(_.name).map(_ + " is standing here.").mkString("\n", "\n", "")}
          |${info.portals.map { case (d, p) => s"${d} : ${p.dest.name}" }.mkString("\n", "\n", "")}
          |""".stripMargin
        .replaceAll("\n\n\n", "\n\n")
        .replaceAll("\n\n\n", "\n\n")
        .replaceAll("\n\n\n", "\n\n")
    }

  def reportMany(ms: Set[Mobile], except: Mobile)(s: => String): IO[Unit] =
    ms.filterNot(_ == except).toList.traverse(_.report(s)) >> ioUnit

  def look(m: Mobile): IO[Unit] =
    roomInfo(m).run >>= reportRoom(m)

  def intro(m: Mobile): IO[Unit] =
    teleport(m, Start, s"${m.name} has entered the game.")

  def remove(m: Mobile): IO[Unit] =
    for {
      i <- (for {
        r <- findMobile(m)
        i <- roomInfo(r)
        _ <- removeMobile(m)
      } yield i).run
      _ <- reportMany(i.mobiles, m)(s"${m.name} freezes and slowly fades from view.")
    } yield ()

  def teleport(m: Mobile, r: Room, msg: String): IO[Unit] =
    for {
      i <- (move(m, r) >> roomInfo(r)).run
      _ <- reportRoom(m)(i)
      _ <- reportMany(i.mobiles, m)(msg)
    } yield ()

  def go(m: Mobile, d: Direction): IO[Unit] =
    tryMove(m, d).run >>= {

      // Not a legal move
      case None =>
        m.report(s"You can't go ${d.toString.toLowerCase} from here.")

      // Done. Tell everyone
      case Some((a, b)) =>
        for {
          _ <- m.report(s"You leave ${d.toString.toLowerCase}")
          _ <- reportMany(a.mobiles, m)(s"${m.name} leaves ${d.toString.toLowerCase}.")
          _ <- reportMany(b.mobiles, m)(s"${m.name} has arrived.")
          _ <- reportRoom(m)(b)
        } yield ()

    }

  def playerExists(s: String): IO[Boolean] =
    lookupMobile(s.trim).map(_.isDefined).run

}





