

package util

case class M2M[A, B] private (ab: Map[A, Set[B]], ba: Map[B, Set[A]]) {

  def +(pair: (A, B)): M2M[A, B] = {
    val (a, b) = pair
    M2M(ab + (a -> (ab(a) + b)), ba + (b -> (ba(b) + a)))
  }
    
  def -(pair: (A, B)): M2M[A, B] = {
    val (a, b) = pair
    M2M(ab + (a -> (ab(a) - b)), ba + (b -> (ba(b) - a)))
  }
    
  def left(a: A): Set[B] =
    ab(a)

  def right(b: B): Set[A] =
    ba(b)

  def apply(a: A)(implicit ev: A =:= B): Set[A] =
    left(a).asInstanceOf[Set[A]] ++ right(a)

  override def toString = s"$productPrefix(${ab.size}:${ba.size})"

}

object M2M {

  private def emptySetMap[A, B]: Map[A, Set[B]] =
    Map[A, Set[B]]() withDefaultValue Set()

  def empty[A, B]: M2M[A, B] = 
    M2M(emptySetMap[A, B], emptySetMap[B, A])

  def apply[A, B](pairs: (A, B)*): M2M[A, B] =
    (empty[A, B] /: pairs)(_ + _)

}

