package data

import fut.Direction

trait DikuStructs {

  case class Room(id: Int, name: String, desc: String, zone: Int, flags: Int, sector: Int, exits: List[Either[Exit, Extra]])
  case class Extra(name: String, desc: String)
  case class Exit(dir: Direction, name: String, desc: String, exitInfo: Int, key: Int, toRoom: Int)

}