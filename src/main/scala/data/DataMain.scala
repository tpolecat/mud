package data

import scala.io._
import java.io.File
import scala.util.parsing.combinator.RegexParsers
import fut._
import scalaz.effect.IO

object DataMain extends WorldParsers {

  def load: IO[Map[fut.Room, Map[Direction, Portal]]] = IO {
    val in = Source.fromFile(new File("data/tinyworld.wld"), "US-ASCII").reader
    parseAll(world, in) match {
      case Success(rooms, _) =>
        val idToRoom = rooms.map(r => (r.id, r)).toMap
        val id2realRoom = idToRoom.map { case (k, r) => (k, fut.Room(r.name, r.desc)) } // strict
        idToRoom.map {
          case (id, dr) =>
            val r = id2realRoom(id)
            val e = dr.exits.flatMap(_.left.toOption).filterNot(_.toRoom == -1).map { e =>              
              e.dir -> Portal(id2realRoom(e.toRoom))
            }.toMap
            (r -> e)
        }
      case x => sys.error(x.toString) // TODO: something sane
    }
  }

}