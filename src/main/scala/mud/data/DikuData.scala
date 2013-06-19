package mud.data

import scala.io._
import java.io.File
import scala.util.parsing.combinator.RegexParsers
import mud._
import scalaz.effect.IO
import util.M2O

object DikuData extends WorldParsers {

  def load: IO[mud.GameState] = 
    for {
      rs <- loadFile(world, "tinyworld.wld")
      ms <- loadFile(mobs, "tinyworld.mob")
    } yield {

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
      val mobs = (M2O.empty[mud.Mobile, mud.Room] /: ms) { case (map, mob) => 
        id2realRoom.get(mob.pos) match {
          case None => map // println("Can't place " + mob); map
          case Some(room) => 
            println("Placed: " + mob)
            map + (mud.Mobile(mob.shortDesc) -> room)
        }
      }

      // Game state
      new mud.GameState(map, mobs)

    }

  def loadFile[A](p: Parser[A], name: String): IO[A] = 
    loadFile(p, new File(s"data/$name"))

  def loadFile[A](p: Parser[A], f: File): IO[A] = IO {
    val s = Source.fromFile(f, "US-ASCII")
    try {
      val in = s.getLines.filterNot(_.startsWith("*")).mkString("\n")
      parseAll(p, in) match {
        case Success(a, _) => a
        case x => sys.error(x.toString) // TODO: something sane
      }
    } finally {
      s.close()
    }
  }

}