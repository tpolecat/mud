package util

import scalaz._
import Scalaz._
import org.scalacheck._
import org.scalacheck.Gen._
import org.scalacheck.Arbitrary._
import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

object O2MCheck extends Specification with ScalaCheck {

  def genM2M[A:Arbitrary, B:Arbitrary]: Gen[O2M[A,B]] = 
    arbitrary[List[(A,B)]].map(ps => (O2M.empty[A,B] /: ps)(_ + _))

  implicit def arbM2M[A:Arbitrary, B:Arbitrary]: Arbitrary[O2M[A,B]] =
    Arbitrary(genM2M[A,B])

  "one-to-many map" should {

    "correctly associate a new member" ! prop { (m2m: O2M[Int,String], n: Int, s:String) =>
      val b = m2m + (n -> s)
      b.left(n).contains(s) && b.right(s) == Some(n)
    }

    "be invariant over add andThen in trivial case" ! prop { (n: Int, s:String) =>
      val m2o = M2O.empty[Int,String]
      m2o + (n -> s) - (n -> s) == m2o
    }

    "return left and right sides properly" ! prop { (ab: List[(Int,String)]) => 
      val m = (O2M.empty[Int,String] /: ab)(_ + _)
      ab.foreach {
        case (k, v) => m.left(k).contains(v) && m.right(v) == Some(k)
      }
    }

  }

}

