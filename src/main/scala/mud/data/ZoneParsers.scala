
package mud.data

import scalaz.effect.IO
import mud._
import DikuStructs._

object ZoneParsers extends DikuParsers[List[Zone]]  {

  override protected val whiteSpace = """[ \t]+""".r

  override lazy val str: PackratParser[String] =
    "[^~]*".r ~ "~" <~ nl ^^ (_._1) // strings are terminated with ~

  lazy val nl = "\n"

  lazy val top: PackratParser[List[Zone]] =
    rep(zone)

  lazy val zone: PackratParser[Zone] =
    "#" ~> // initial
    num ~> // ignored
    nl  ~>
    str ~ // name
    num ~ // top
    num ~ // lifespan
    num ~
    nl  ~ // resetMode
    rep(zoneCommand) <~ // commands
    "S" <~ nl ^^ { // end
      case name ~ top ~ lifespan ~ resetMode ~ _ ~ cmds =>
        Zone(name, top, lifespan, resetMode, cmds)
    }

  lazy val cmd3: PackratParser[(Boolean, Int, Int)] =
    ifFlag ~ num ~ num ^^ {
      case ifFlag ~ a ~ b => (ifFlag, a, b)        
    }

  lazy val cmd4: PackratParser[(Boolean, Int, Int, Int)] =
    ifFlag ~ num ~ num ~ num ^^ {
      case ifFlag ~ a ~ b ~ c => (ifFlag, a, b, c)        
    }

  lazy val loadMobile: PackratParser[LoadMobile] = 
    "M" ~> cmd4 ^^ (LoadMobile.apply _).tupled

  lazy val loadObject: PackratParser[LoadObject] = 
    "O" ~> cmd4 ^^ (LoadObject.apply _).tupled

  lazy val giveObject: PackratParser[GiveObject] = 
    "G" ~> cmd3 ^^ (GiveObject.apply _).tupled

  lazy val equipObject: PackratParser[EquipObject] = 
    "E" ~> cmd4 ^^ (EquipObject.apply _).tupled

  lazy val putObject: PackratParser[PutObject] = 
    "P" ~> cmd4 ^^ (PutObject.apply _).tupled

  lazy val setDoorState: PackratParser[SetDoorState] = 
    "D" ~> cmd4 ^^ (SetDoorState.apply _).tupled

  lazy val removeObject: PackratParser[RemoveObject] = 
    "R" ~> cmd3 ^^ (RemoveObject.apply _).tupled

  lazy val nop: PackratParser[Nop.type] =
    "*" ^^^ Nop

  lazy val zoneCommand: PackratParser[ZoneCommand] =
    (loadMobile   | 
     loadObject   | 
     giveObject   | 
     equipObject  | 
     putObject    | 
     setDoorState |
     removeObject |
     nop) ~ ".*".r <~ nl ^^ { case x ~ c => x }

}
