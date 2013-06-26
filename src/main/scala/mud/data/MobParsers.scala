
package mud.data

import scalaz.effect.IO
import DikuStructs._

object MobParsers extends DikuParsers[List[Mobile]] {

  val top: Parser[List[Mobile]] =
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

}
