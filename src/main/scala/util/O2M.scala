
// room to player
package util

case class O2M[A, B] private (ab: Map[A, Set[B]], ba: Map[B, A]) {

  def +(pair: (A, B)): O2M[A, B] = {
    val (a, b) = pair
    val sb = left(a) + b
    O2M(ab + (a -> sb), ba + (b -> a))
  }
    
  def -(pair: (A, B)): O2M[A, B] = {
    val (a, b) = pair
    val sb = left(a) - b
    sb.isEmpty match {
      case false => O2M(ab + (a -> sb), ba - b)
      case true  => O2M(ab - a,         ba - b)
    }
  }
  
  def left(a: A): Set[B] =
    ab.get(a).getOrElse(Set())

  def right(b: B): Option[A] =
    ba.get(b)

  override def toString = s"$productPrefix(${ab.size}:${ba.size})"

}

object O2M {

  private def emptySetMap[A, B]: Map[A, Set[B]] =
    Map[A, Set[B]]()

  def empty[A, B]: O2M[A, B] =
    O2M(emptySetMap[A, B], Map[B, A]())

  def apply[A, B](pairs: (A, B)*): O2M[A, B] =
    (empty[A, B] /: pairs)(_ + _)

}

