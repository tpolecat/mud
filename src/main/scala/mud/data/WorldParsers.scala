
package mud.data

import scalaz.effect.IO
import DikuStructs._
import mud.Direction
import Direction._

object WorldParsers extends DikuParsers[List[Room]] {

  lazy val room: PackratParser[Room] =
    "#" ~>       // initial
    num ~        // id
    str ~        // names
    str ~        // desc
    num ~        // zone
    num ~        // flags
    num ~        // sector
    rep(exitOrExtra) <~ // exits and extras (mixed)
    "S" ^^       // terminal
      {
        case id ~ name ~ desc ~ zone ~ flags ~ sector ~ exits =>
          Room(id, name, desc, zone, flags, sector, exits)
      }

  lazy val dir: PackratParser[Direction] =
      "D0" ^^^ North |
      "D1" ^^^ East  |
      "D2" ^^^ South |
      "D3" ^^^ West  |
      "D4" ^^^ Up    |
      "D5" ^^^ Down

  lazy val exit: PackratParser[Exit] =
    dir ~  // direction 
    str ~  // desc (when you look)
    str ~  // names (when you look)
    num ~  // flags 
    num ~  // key
    num ^^ // destination
      { 
        case d ~ s ~ s2 ~ a ~ b ~ c => 
          Exit(d, s, s2, a, b, c) 
      }

  lazy val extra: PackratParser[Extra] =
    "E" ~> // initial
    str ~  // name
    str ^^ // desc
    {
      case name ~ desc =>
        Extra(name, desc)
    }

  lazy val exitOrExtra: PackratParser[Either[Exit, Extra]] =
    exit  ^^ (Left(_)) |
    extra ^^ (Right(_))
    

  lazy val top: PackratParser[List[Room]] = 
    rep(room)

}
