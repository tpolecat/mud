package util

case class M2O[A, B] private (o2m: O2M[B, A]) {

  def +(p: (A, B)): M2O[A, B] =
    M2O(o2m + p.swap)

  def -(p: (A, B)): M2O[A, B] =
    M2O(o2m - p.swap)

  def left(a: A): Option[B] =
    o2m.right(a)

  def right(b: B): Set[A] =
    o2m.left(b)

}

object M2O {

  def empty[A, B]: M2O[A, B] =
    M2O(O2M.empty[B, A])

}