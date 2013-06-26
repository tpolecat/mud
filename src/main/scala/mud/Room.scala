package mud

import mud.data.DikuStructs

case class Room(name: String, desc: String, extras: Map[String, String])

object Room {

  def apply(r: DikuStructs.Room): Room = {    
    val extras = for {
      e <- r.exits.map(_.fold(identity, identity))
      k <- e.names.split("\\s+").filterNot(_.isEmpty)
    } yield (k -> e.desc) 
    Room(r.name.trim, r.desc.trim, extras.toMap)
  }

}

