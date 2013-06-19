package mud.data

import scala.util.parsing.combinator.RegexParsers

trait DikuParsers extends DikuStructs with RegexParsers {

  val num: Parser[Int] =
    "[+-]?\\d+".r ^^ (_.toInt)

  val str: Parser[String] =
    "[^~]*".r ~ "~" ^^ (_._1) // strings are terminated with ~

  val dice: Parser[Dice] =
    num ~ "d" ~ num ~ "+" ~ num ^^ { case n ~ _ ~ s ~ _ ~ p => Dice(n, s, p) }

  val ifFlag: Parser[Boolean] =
    num ^^ (_ != 0)

}