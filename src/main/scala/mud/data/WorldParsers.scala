
package mud.data

import scalaz.effect.IO
import DikuStructs._
import mud.Direction
import Direction._

object WorldParsers extends DikuParsers[List[Room]] {

  val room: Parser[Room] =
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

  val dir: Parser[Direction] =
      "D0" ^^^ North |
      "D1" ^^^ East  |
      "D2" ^^^ South |
      "D3" ^^^ West  |
      "D4" ^^^ Up    |
      "D5" ^^^ Down

  val exit: Parser[Exit] =
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

  val extra: Parser[Extra] =
    "E" ~> // initial
    str ~  // name
    str ^^ // desc
    {
      case name ~ desc =>
        Extra(name, desc)
    }

  val exitOrExtra: Parser[Either[Exit, Extra]] =
    exit  ^^ (Left(_)) |
    extra ^^ (Right(_))
    

  val top: Parser[List[Room]] = 
    rep(room)

}
