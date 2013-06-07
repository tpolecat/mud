

package util

case class M2M[A, B] private (ab: Map[A, Set[B]], ba: Map[B, Set[A]]) {

  def +(pair: (A, B)): M2M[A, B] = {
    val (a, b) = pair
    val (sb, sa) = (left(a) + b, right(b) + a)
    M2M(ab + (a -> sb), ba + (b -> sa))
  }
    
  def -(pair: (A, B)): M2M[A, B] = {
    val (a, b) = pair
    val (sb, sa) = (left(a) - b, right(b) - a)
    (sb.isEmpty, sa.isEmpty) match {
      case (false, false) => M2M(ab + (a -> sb), ba + (b -> sa))
      case (false, true)  => M2M(ab + (a -> sb), ba - b)
      case (true, false)  => M2M(ab - a, ba + (b -> sa))
      case (true, true)   => M2M(ab - a, ba - b)
    }
  }
    
  def left(a: A): Set[B] =
    ab.get(a).getOrElse(Set())

  def right(b: B): Set[A] =
    ba.get(b).getOrElse(Set())

  def apply(a: A)(implicit ev: A =:= B): Set[A] =
    left(a).asInstanceOf[Set[A]] ++ right(a)

  override def toString = s"$productPrefix(${ab.size}:${ba.size})"

}

object M2M {

  private def emptySetMap[A, B]: Map[A, Set[B]] =
    Map[A, Set[B]]()

  def empty[A, B]: M2M[A, B] = 
    M2M(emptySetMap[A, B], emptySetMap[B, A])

  def apply[A, B](pairs: (A, B)*): M2M[A, B] =
    (empty[A, B] /: pairs)(_ + _)

}

