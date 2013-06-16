package mud

import mud.data.DikuData

case class Room(name: String, desc: String, extras: Map[String, String])

object Room {

  def apply(r: DikuData.Room): Room = {
    val extras = for {
      e <- r.exits.flatMap(_.right.toOption)
      k <- e.name.split("\\s+")
    } yield (k -> e.desc) 
    Room(r.name, r.desc, extras.toMap)
  }

}

