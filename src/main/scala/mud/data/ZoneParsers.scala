
package mud.data

import scalaz.effect.IO
import mud._
import DikuStructs._

object ZoneParsers extends DikuParsers[List[Zone]]  {

  override protected val whiteSpace = """[ \t]+""".r

  override val str: Parser[String] =
    "[^~]*".r ~ "~" <~ nl ^^ (_._1) // strings are terminated with ~

  val nl = "\n"

  val top: Parser[List[Zone]] =
    rep(zone)

  val zone: Parser[Zone] =
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

  val nop: Parser[Nop.type] =
    "*" ^^^ Nop

  val zoneCommand: Parser[ZoneCommand] =
    (loadMobile   | 
     loadObject   | 
     giveObject   | 
     equipObject  | 
     putObject    | 
     setDoorState |
     removeObject |
     nop) ~ ".*".r <~ nl ^^ { case x ~ c => x }

}
