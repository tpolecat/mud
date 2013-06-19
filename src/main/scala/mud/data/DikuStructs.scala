package mud.data

import mud.Direction

trait DikuStructs {

  trait Described {
    def names: String
    def desc: String
  }

  case class Room(
    id: Int, 
    name: String, 
    desc: String, 
    zone: Int, 
    flags: Int, 
    sector: Int, 
    exits: List[Either[Exit, Extra]])
  
  case class Extra(names: String, desc: String) extends Described
  
  case class Exit(
    dir: Direction, 
    desc: String, 
    names: String, 
    flags: Int, 
    key: Int, 
    toRoom: Int) extends Described 

  sealed abstract class ExitFlag(bit: Byte)
  object ExitFlag {
    case object IsDoor    extends ExitFlag(1)
    case object Closed    extends ExitFlag(2)
    case object Locked    extends ExitFlag(4)
    case object RsClosed  extends ExitFlag(8)
    case object RsLocked  extends ExitFlag(16)
    case object PickProof extends ExitFlag(32)
  }

  // 5d12+120
  case class Dice(num: Int, sides: Int, plus: Int)

  case class Mobile(
    num: Int,
    names: String,
    shortDesc: String,
    longDesc: String,
    desc: String,
    actionFlags: Int,
    affectionFlags: Int,
    alignment: Int,
    level: Int,
    thac0: Int,
    ac: Int,
    hp: Dice,
    damage: Dice,
    gold: Int,
    exp: Int,
    pos: Int,
    defaultPos: Int,
    sex: Int)


  case class Zone(
    name: String, 
    top: Int, 
    lifespan: Int, 
    resetMode: Int, 
    cmds: List[ZoneCommand])


  trait ZoneCommand {
    def ifFlag: Boolean
  }

  // M (load a mobile): 
  //          Format: 'M' <if-flag> <mobile nr> <max existing> <room nr>
  //    mobile nr and room nr should be self-explanatory. The 'max
  //    existing' parameter specifies the maximum permissible number of
  //    existing units. In other words: If you only want one manifestation
  //    of a given monster, you just specify the number '1'. If the max
  //    number is about to be exceeded, the command won't be executed.
  case class LoadMobile(ifFlag: Boolean, mob: Int, max: Int, room: Int) extends ZoneCommand

  // O (load an object):
  //    Format: 'O' <if-flag> <object nr> <max existing> <room nr>
  //     Load an object and place it in a room.  (NOT -1)
  case class LoadObject(ifFlag: Boolean, obj: Int, max: Int, room: Int) extends ZoneCommand

  // G (give object to mobile):
  //    Format: 'G' <if-flag> <object nr> <max existing>
  //     Loads an object, and gives it to the last monster referenced (ie. by the
  //     M-command).
  //    Of course, this command doesn't make sense if a new mobile+object
  //    pair has not just been created, which is where the if-flag comes
  //    in handy.   :)
  case class GiveObject(ifFlag: Boolean, obj: Int, max: Int) extends ZoneCommand

  // E (object to equipment list of mobile)
  //   Format: 'E' <if-flag> <object nr> <max existing> <equipment position>
  //  Loads object and places it in the Equipment list of the last monster
  //   referenced.
  //   Note that it is NOT necessary to precede this command with a 'G' command.
  //   Equipment position is one of:
  //     WEAR_LIGHT      0
  //     WEAR_FINGER_R   1
  //     WEAR_FINGER_L   2
  //     WEAR_NECK_1     3
  //     WEAR_NECK_2     4
  //     WEAR_BODY       5
  //     WEAR_HEAD       6
  //     WEAR_LEGS       7
  //     WEAR_FEET       8
  //     WEAR_HANDS      9
  //     WEAR_ARMS      10
  //     WEAR_SHIELD    11
  //     WEAR_ABOUT     12
  //     WEAR_WAISTE    13
  //     WEAR_WRIST_R   14
  //     WEAR_WRIST_L   15
  //     WIELD          16
  //     HOLD           17
  case class EquipObject(ifFlag: Boolean, obj: Int, max: Int, pos: Int) extends ZoneCommand

  // P (put object in object):
  //    Format: 'P' <if-flag> <object_nr1> <max existing> <object nr2>
  //   Loads object1 and places it in object2.
  case class PutObject(ifFlag: Boolean, obj: Int, max: Int, targetObj: Int) extends ZoneCommand

  // D (set state of door)
  //   Format: 'D' <if-flag> <room nr> <exit nr> <state>
  //   State being one of:
  //   0: Open.
  //   1: Closed.
  //   2: Closed and locked.
  case class SetDoorState(ifFlag: Boolean, room: Int, exit: Int, state: Int) extends ZoneCommand

  // R (remove object from room)
  //    Format: 'R' <if-flag> <room_nr> <object_nr>
  case class RemoveObject(ifFlag: Boolean, room: Int, obj: Int) extends ZoneCommand

  case object Nop extends ZoneCommand {
    val ifFlag = true
  }

}