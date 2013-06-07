package mud.data

import scala.util.parsing.combinator.RegexParsers

trait DikuParsers extends RegexParsers {

  val num: Parser[Int] =
    "-?\\d+".r ^^ (_.toInt)

  val str: Parser[String] =
    "[^~]*".r ~ "~" ^^ (_._1) // strings are terminated with ~

}