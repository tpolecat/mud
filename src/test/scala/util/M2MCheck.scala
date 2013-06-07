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

object M2MCheck extends Specification with ScalaCheck {

  def genM2M[A:Arbitrary, B:Arbitrary]: Gen[M2M[A,B]] = 
    arbitrary[List[(A,B)]].map(ps => (M2M.empty[A,B] /: ps)(_ + _))


  implicit def arbM2M[A:Arbitrary, B:Arbitrary]: Arbitrary[M2M[A,B]] =
    Arbitrary(genM2M[A,B])

  "many-to-many map" should {

    "correctly associate a new member" ! prop { (m2m: M2M[Int,String], n: Int, s:String) =>
      val b = m2m + (n -> s)
      b.left(n).contains(s) && b.right(s).contains(n)
    }

    "be invariant over add andThen remove" ! prop { (m2m: M2M[Int,String], n: Int, s:String) =>
      m2m + (n -> s) - (n -> s) == m2m
    }

    "return left and right sides properly" ! prop { (ab: List[(Int,String)]) => 
      val m = (M2M.empty[Int,String] /: ab)(_ + _)
      ab.foreach {
        case (k, v) => m.left(k).contains(v) && m.right(v).contains(k)
      }
    }

    "return results of apply properly" ! prop { (ab: List[(Int,Int)]) => 
      val m = (M2M.empty[Int,Int] /: ab)(_ + _)
      ab.foreach {
        case (k, v) => m(k).contains(v) && m(v).contains(k)
      }
    }

  }

}