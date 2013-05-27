package fut

import scalaz.effect.IO
import scalaz.effect.IO.ioUnit
import scalaz.syntax.std.boolean._

// Stimuli are translated to delightful text here, and only here.
class DefaultTextAvatar(mobile: Mobile, out: String => IO[Unit]) extends Avatar {

  def report(s: Stimulus): IO[Unit] =
    text(s).fold(ioUnit)(out)

  def text(s: Stimulus): Option[String] = s match {

    case Look(info) =>
      Some(s"""|
            |== ${info.room.name} ==
            |
            |${info.room.desc.trim}
            |${info.mobiles.filterNot(_ == mobile).map(_.name).map(_ + " is standing here.").mkString("\n", "\n", "")}
            |${info.portals.map { case (d, p) => s"${d} : ${p.dest.name}" }.mkString("\n", "\n", "")}
            |""".stripMargin
        .replaceAll("\n\n\n", "\n\n")
        .replaceAll("\n\n\n", "\n\n")
        .replaceAll("\n\n\n", "\n\n"))

    case EnterGame(m) =>
      Some((m == mobile) ? s"Welcome ${m.name}!" | s"${m.name} has entered the game.")

    case ExitGame(m) =>
      Some((m == mobile) ? s"Goodbye." | s"${m.name} has left the game.")

    case Exit(m, d) =>
      Some((m == mobile) ? s"You leave ${d.toString.toLowerCase}" | s"${m.name} leaves ${d.toString.toLowerCase}.")

    case Enter(m) =>
      Some(s"${m.name} has arrived.").filterNot(_ => m == mobile)

    case NoExit(d) =>
      Some(s"You can't go ${d.toString.toLowerCase} from here.")

    case Wat =>
      Some("wat?")

  }

}