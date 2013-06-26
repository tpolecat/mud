package mud.data

import scalaz._
import Scalaz._
import scala.io._
import java.io.File
import scala.util.parsing.combinator.RegexParsers
import mud._
import scalaz.effect.IO
import scalaz.effect.IO._
import util.M2O
import shapeless._
import shapeless.contrib.scalaz._
import Tuples._
import language.existentials
import scala.util.parsing.combinator.Parsers

object DikuData {
  import DikuStructs._


  def load(dir: File, prefix: String): IO[Parsers#NoSuccess \/ mud.Dungeon] = {

    def load[A](p: DikuParsers[A], suffix: String): IO[Parsers#NoSuccess \/ A] =
      p.load(new File(dir, prefix + suffix))

    val t = (load(WorldParsers, ".wld"),
             load(MobParsers,   ".mob"),
             load(ZoneParsers,  ".zon"))

    // sequence(t).map(sequence(_).map(initialDungeon))
    sequence(t.hlisted).map(sequence(_).map(_.tupled)
                                       .map(initialDungeon))

  }

  def initialDungeon: Tuple3[List[Room], List[Mobile], List[Zone]] => Dungeon = { case (rs, ms, zs) =>

    // Map from id to diku room
    val idToRoom = rs.map(r => (r.id, r)).toMap
    
    // Id to real room
    val id2realRoom = idToRoom.map { case (k, r) => (k, mud.Room(r)) }
    
    // Map with exits
    val map = idToRoom.map {
      case (id, dr) =>
        val r = id2realRoom(id)
        val e = dr.exits.flatMap(_.left.toOption).filterNot(_.toRoom == -1).map { e =>              
          e.dir -> Door(id2realRoom(e.toRoom))
        }.toMap                           
        (r -> e)
    }

    // Mobiles
    val mobs = ms.map(m => (m.id, m)).toMap

    // Apply zone commands.
    val (mobMap, _) = ((M2O.empty[mud.Mobile, mud.Room], true) /: zs.flatMap(_.cmds)) { 
      case ((s, b), LoadMobile(ifFlag, mobId, max, roomId)) =>
        val r0 = for {
          m <- mobs.get(mobId)
          r <- id2realRoom.get(roomId)
        } yield (s + (mud.Monster(m.shortDesc.trim, m.longDesc.trim) -> r), b)
        r0.getOrElse { println("****"); (s, b) }
      case ((s, b), Nop) => (s, b)
      case ((s, b), c)   => (s, b)
    }

    // Game state
    mud.Dungeon(new mud.GameState(map, mobMap))

  }


}