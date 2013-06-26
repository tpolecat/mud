package mud

import util._
import scalaz.Scalaz._
import scalaz.effect.IO
import scalaz.effect.IO.ioMonadCatchIO
import scalaz.effect.IO.ioUnit

/** 
 * The dungeon, which encapsulates a `GameState` and provides actions for manipulating it. These
 * actions are all in IO and are atomic where needed.
 */
case class Dungeon(state: GameState) {
  import state._

  // Private reporting helpers

  private def report(m: Mobile)(s: Stimulus): IO[Unit] =
    for {
      a <- avatar(m).run
      _ <- a.fold(ioUnit)(_.report(s))
    } yield ()

  private def reportAll(ms: Set[Mobile])(s: => Stimulus): IO[Unit] =
    ms.toList.traverse(report(_)(s)) >> ioUnit

  private def reportMany(ms: Set[Mobile], except: Mobile)(s: => Stimulus): IO[Unit] =
    reportAll(ms.filterNot(_ == except))(s)




  /** Sets the `Avatar` for the given `Mobile`, removing any existing `Avatar`. */
  def setAvatar(m: Mobile, a: Avatar): IO[Unit] =
    attachAvatar(m, a).run

  def look(m: Mobile): IO[Unit] =
    roomInfo(m).run.map(Look) >>= report(m)

  def examine(m: Mobile, what: String): IO[Unit] =
    findMobile(m).run.map(r => Examine(r.extras.get(what))) >>= report(m)

  def intro(m: Mobile): IO[Unit] =
    teleport(m, Start, EnterGame(m))

  def remove(m: Mobile): IO[Unit] =
    for {
      i <- (for {
        r <- findMobile(m)
        i <- roomInfo(r)
        _ <- removeMobile(m)
      } yield i).run
      _ <- reportMany(i.mobiles, m)(ExitGame(m))
    } yield ()

  def teleport(m: Mobile, r: Room, msg: Stimulus): IO[Unit] =
    for {
      i <- (move(m, r) >> roomInfo(r)).run
      _ <- report(m)(Look(i))
      _ <- reportMany(i.mobiles, m)(msg)
    } yield ()

  def go(m: Mobile, d: Direction): IO[Unit] =
    tryMove(m, d).run >>= {

      // Not a legal move
      case None =>
        report(m)(NoExit(d))

      // Done. Tell everyone
      case Some((a, b)) =>
        for {
          _ <- reportAll(a.mobiles)(Exit(m, d))
          _ <- reportAll(b.mobiles)(Enter(m))
          _ <- report(m)(Look(b))
        } yield ()

    }

  def playerExists(s: String): IO[Boolean] =
    lookupMobile(s.trim).map(_.isDefined).run

  def wat(m:Mobile): IO[Unit] =
    report(m)(Wat)


}





