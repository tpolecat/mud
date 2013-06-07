
package mud.data

import mud._

trait WorldParsers extends DikuParsers with DikuStructs {

  val room: Parser[Room] =
    "#" ~>       // initial
    num ~        // id
    str ~        // name
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
    str ~  // name (when you look)
    str ~  // desc (when you look)
    num ~  // info 
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
    

  val world: Parser[List[Room]] = rep(room)

}
