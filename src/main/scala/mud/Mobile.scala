package mud

import scalaz.effect.IO

trait Mobile {
  def intro: String
  def name: String
}

case class Player(name: String) extends Mobile {
  def intro = s"$name is standing here."
}

case class Monster(name: String, intro: String) extends Mobile
