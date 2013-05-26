package session

import chan.ServerChannelWorld._
import fut._
import scalaz.effect.IO
import scalaz.effect.IO._
import chan.SessionState

case class Playing(d: Dungeon, m: Mobile) extends SessionState with Commands {

  def prompt: Action[Unit] =
    write(s"<${m.name}> ")

  def input(s: String): Action[SessionState] =
    cmd(s, d, m).liftIO[Action].map(_ => this)

  def closed: Action[Unit] =
    d.remove(m).liftIO[Action]

}

trait Commands {

  def cmd(c: String, d: Dungeon, m: Mobile): IO[Unit] =
    c.trim.split("\\s+").toList.map(_.toLowerCase) match {
      case s :: ss => handle(expandAlias(s) ::: ss, d, m)
      case Nil     => ioUnit
    }

  def handle(cmd: List[String], d: Dungeon, m: Mobile): IO[Unit] =
    cmd match {
      case "go" :: "north" :: Nil => d.go(m, North)
      case "go" :: "south" :: Nil => d.go(m, South)
      case "go" :: "east" :: Nil  => d.go(m, East)
      case "go" :: "west" :: Nil  => d.go(m, West)
      case "go" :: "up" :: Nil    => d.go(m, Up)
      case "go" :: "down" :: Nil  => d.go(m, Down)
      case "look" :: Nil          => d.look(m)
      case "" :: Nil              => ioUnit
      case _                      => m.report("wat?")

    }

  def expandAlias(s: String): List[String] =
    Aliases.get(s).getOrElse(List(s))

  lazy val Aliases: Map[String, List[String]] = Map(
    "l" -> List("look"),
    "n" -> List("go", "north"),
    "s" -> List("go", "south"),
    "e" -> List("go", "east"),
    "w" -> List("go", "west"),
    "u" -> List("go", "up"),
    "d" -> List("go", "down"),
    "north" -> List("go", "north"),
    "south" -> List("go", "south"),
    "east" -> List("go", "east"),
    "west" -> List("go", "west"),
    "up" -> List("go", "up"),
    "down" -> List("go", "down"))

}