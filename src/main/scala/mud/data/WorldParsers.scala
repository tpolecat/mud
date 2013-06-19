
package mud.data

import mud._

trait WorldParsers extends DikuParsers with DikuStructs {

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
    

  val world: Parser[List[Room]] = rep(room)

  val mobs: Parser[List[Mobile]] =
    rep(mob)

  val mob: Parser[Mobile] =
    "#" ~>      // initial
    num ~       // vnum
    str ~       // names
    str ~       // short description
    str ~       // long description
    str ~       // description
    num ~       // action flags
    num ~       // affection flags
    num ~      // alignment
    "S" ~      // simple flag
    num ~       // level
    num ~       // thac0
    num ~       // ac
    dice ~      // hp
    dice ~      // damage
    num ~       // gold
    num ~       // exp
    num ~       // pos
    num ~       // default pos
    num ^^      // sec
    {
      case id ~ ns ~ sd ~ ld ~ d ~ ac ~ af ~ al ~ _ ~ le ~ th ~ ax ~ hp ~ da ~ go ~ ex ~ po ~ dp ~ s =>
        Mobile(id, ns, sd, ld, d, ac, af, al, le, th, ax, hp, da, go, ex, po, dp, s)
    }

  val zones: Parser[List[Zone]] =
    rep(zone)

  val zone: Parser[Zone] =
    "#" ~>      // initial
    num ~> // ignored
    str ~ // name
    num ~ // top
    num ~ // lifespan
    num ~ // resetMode
    rep(zoneCommand) <~ // commands
    "S" ^^ { // end
      case name ~ top ~ lifespan ~ resetMode ~ cmds =>
        Zone(name, top, lifespan, resetMode, cmds)
    }



  val cmd3: Parser[(Boolean, Int, Int)] =
    ifFlag ~ num ~ num ^^ {
      case ifFlag ~ a ~ b => (ifFlag, a, b)        
    }

  val cmd4: Parser[(Boolean, Int, Int, Int)] =
    ifFlag ~ num ~ num ~ num ^^ {
      case ifFlag ~ a ~ b ~ c => (ifFlag, a, b, c)        
    }

  val loadMobile: Parser[LoadMobile] = 
    "M" ~> cmd4 ^^ (LoadMobile.apply _).tupled

  val loadObject: Parser[LoadObject] = 
    "O" ~> cmd4 ^^ (LoadObject.apply _).tupled

  val giveObject: Parser[GiveObject] = 
    "G" ~> cmd3 ^^ (GiveObject.apply _).tupled

  val equipObject: Parser[EquipObject] = 
    "E" ~> cmd4 ^^ (EquipObject.apply _).tupled

  val putObject: Parser[PutObject] = 
    "P" ~> cmd4 ^^ (PutObject.apply _).tupled

  val setDoorState: Parser[SetDoorState] = 
    "D" ~> cmd4 ^^ (SetDoorState.apply _).tupled

  val removeObject: Parser[RemoveObject] = 
    "R" ~> cmd3 ^^ (RemoveObject.apply _).tupled

  val zoneCommand: Parser[ZoneCommand] =
    (loadMobile   | 
                 loadObject   | 
                 giveObject   | 
                 equipObject  | 
                 putObject    | 
                 setDoorState |
                 removeObject) ~ "[A-Za-z0-9' .?()\\-,_\"|]*".r ^^ { case x ~ c => println((x, c)); x }

}
