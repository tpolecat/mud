package fut

import scala.concurrent.stm._
import util.M2O
import util.World
import scalaz.effect.IO
import scalaz._
import Scalaz._

// Our mutable state is hidden in an effect world whose actions compose transactionally and can be run only in IO.
final class State(map: Map[Room, Map[Direction, Portal]]) extends World {

  // Find limbo; we need it
  val Limbo = map.keys.find(_.name == "Limbo").getOrElse(sys.error("Fatal: can't find Limbo"))
  val Start = map.keys.find(_.name == "The Grunting Boar").getOrElse(sys.error("Fatal: can't find The Grunting Boar"))

  // Actions pass a transaction as their state
  protected type State = InTxn

  // But our world also encapsulates mutable state
  private val portals: Ref[Map[Room, Map[Direction, Portal]]] = Ref(map)
  private val mobiles: Ref[M2O[Mobile, Room]] = Ref(M2O.empty)
  private val items: Ref[M2O[Item, Room]] = Ref(M2O.empty)

  // And because our actions can modify mutable state, they can only be run in IO
  implicit class RunnableAction[A](a: Action[A]) {
    def run: IO[A] = IO(atomic(runWorld(a, _)._2))
  }

  // Primitive actions

  def findRoom(s: String): Action[Option[Room]] =
    effect { implicit t => portals().keys.find(_.name equalsIgnoreCase s) }

  def findMobile(m: Mobile): Action[Room] =
    effect { implicit t => mobiles().left(m).getOrElse(Limbo) }

  def lookupMobile(s:String): Action[Option[Mobile]] =
    effect { implicit t => mobiles().o2m.ba.keySet.find(_.name equalsIgnoreCase s) }

  def mobilesInRoom(r: Room): Action[Set[Mobile]] =
    effect { implicit t => mobiles().right(r) }

  def move(m: Mobile, dest: Room): Action[Unit] =
    effect { implicit t => mobiles() = mobiles() + (m -> dest) }

  def portals(r: Room): Action[Map[Direction, Portal]] =
    effect { implicit t => portals().get(r).getOrElse(Map()) }

  def unit[A](a: A): Action[A] =
    super.unit(a)

  // Derived actions

  case class RoomInfo(room: Room, mobiles: Set[Mobile], portals: Map[Direction, Portal])

  def roomInfo(room: Room): Action[RoomInfo] =
    (mobilesInRoom(room) |@| portals(room))(RoomInfo(room, _, _))

  def roomInfo(mobile: Mobile): Action[RoomInfo] =
    findMobile(mobile) >>= roomInfo

  def tryMove(m: Mobile, d: Direction): Action[Option[(RoomInfo, RoomInfo)]] =
    for {
      r <- findMobile(m)
      i <- portals(r).map(_.get(d)) >>= {

        // No such exit
        case None =>
          unit(None)

        // The exit is legal
        case Some(p) =>
          for {
            _ <- move(m, p.dest)
            a <- roomInfo(r)
            b <- roomInfo(p.dest)
          } yield Some(a, b)

      }
    } yield i

}