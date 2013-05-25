
// room to player
package util

case class O2M[A, B] private (ab: Map[A, Set[B]], ba: Map[B, A]) {

  def +(pair:(A,B)): O2M[A, B] = {
    val (a, b) = pair
    O2M(ba.get(b).fold(ab)(r => ab + (r -> (ab(r) - b))) + (a -> (ab(a) + b)), ba + (b -> a))
  }
  
  def -(pair:(A,B)): O2M[A, B] = {
    val (a, b) = pair
    O2M(ab + (a -> (ab(a) - b)), ba - b)
  }
  
  def left(a: A): Set[B] =
    ab(a)

  def right(b: B): Option[A] =
    ba.get(b)

  override def toString = s"$productPrefix(${ab.size}:${ba.size})"

}

object O2M {

  private def emptySetMap[A, B]: Map[A, Set[B]] =
    Map[A, Set[B]]() withDefaultValue Set()

  def empty[A, B]: O2M[A, B] =
    O2M(emptySetMap[A, B], Map[B, A]())

  def apply[A, B](pairs: (A, B)*): O2M[A, B] =
    (empty[A, B] /: pairs)(_ + _)

}

