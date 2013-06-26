package mud.data

import scala.util.parsing.combinator.RegexParsers
import scalaz.effect.IO
import java.io.File
import scala.io.Source
import scalaz._

trait DikuParsers[A] extends RegexParsers {
  import DikuStructs._

  def top: Parser[A]

  val num: Parser[Int] =
    "[+-]?\\d+".r ^^ (_.toInt)

  val str: Parser[String] =
    "[^~]*".r ~ "~" ^^ (_._1) // strings are terminated with ~

  val dice: Parser[Dice] =
    num ~ "d" ~ num ~ "+" ~ num ^^ { case n ~ _ ~ s ~ _ ~ p => Dice(n, s, p) }

  val ifFlag: Parser[Boolean] =
    num ^^ (_ != 0)

  def load(f: File): IO[NoSuccess \/ A] = IO {
    val s = Source.fromFile(f, "US-ASCII")
    try {
      // TODO: get rid of the comment exclusion here; it only happens in .zon
      val in = s.getLines.filterNot(_.startsWith("*")).mkString("\n")
      parseAll(top, in) match {
        case Success(a, _)     => \/-(a)
        case f @ Failure(_, _) => -\/(f)
        case e @ Error(_, _)   => -\/(e)
      }
    } finally {
      s.close()
    }
  }

}