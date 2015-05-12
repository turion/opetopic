/**
  * Zipper.scala - Derivatives and Zippers
  * 
  * @author Eric Finster
  * @version 0.1 
  */

package opetopic

import scala.language.higherKinds
import scala.language.implicitConversions

import scalaz.Monad
import scalaz.syntax.monad._

import TypeDefs._

trait ZipperFunctions {

  object rootAddr extends NatCaseSplit0 {

    type Out[N <: Nat] = Address[N]

    def caseZero : Address[_0] = ()
    def caseSucc[P <: Nat](p : P) : Address[S[P]] = Nil

  }

  object plug extends NatCaseSplit1 {

    type Out[N <: Nat, A] = (Derivative[A, N], A) => Tree[A, N]

    def caseZero[A] : Out[_0, A] = {
      case (_, a) => Pt(a)
    }

    def caseSucc[P <: Nat, A](p : P) : Out[S[P], A] = {
      case ((sh, cntxt), a) => close(S(p))(cntxt, Node(a, sh))
    }

  }

  object close extends NatCaseSplit1 {

    type Out[N <: Nat, A] = (Context[A, N], Tree[A, N]) => Tree[A, N]

    def caseZero[A] : Out[_0, A] = {
      case (_, tr) => tr
    }

    def caseSucc[P <: Nat, A](p : P) : Out[S[P], A] = {
      case (Nil, tr) => tr
      case ((a, d) :: cs, tr) => close(S(p))(cs, Node(a, plug(p)(d, tr)))
    }

  }

  def visit[A, N <: Nat](n : N)(zp : Zipper[A, S[N]], dir : Address[N]) : ShapeM[Zipper[A, S[N]]] = 
    (new NatCaseSplit0 {

      type Out[N <: Nat] = (Zipper[A, S[N]], Address[N]) => ShapeM[Zipper[A, S[N]]]

      def caseZero : Out[_0] = {
        case ((Leaf(_), _), ()) => fail(new ShapeError("Cannot visit a leaf"))
        case ((Node(hd, Pt(tl)), c), ()) => Monad[ShapeM].pure((tl, (hd, ()) :: c))
      }

      def caseSucc[P <: Nat](p : P) : (ZipperDblSucc[A, P], Address[S[P]]) => ShapeM[ZipperDblSucc[A, P]] = {
        case ((Leaf(_), _), _) => fail(new ShapeError("Cannot visit a leaf"))
        case ((Node(a, sh), c), d) => 
          for {
            shZp <- seek[Tree[A, S[S[P]]], S[P]](S(p))((sh, Nil), d)
            res <- (
              shZp match {
                case (Leaf(_), _) => fail(new ShapeError("Ran out of room in shell"))
                case (Node(tr, hsh), c0) => Monad[ShapeM].pure((tr, (a, (hsh, c0)) :: c))
              }
            )
          } yield res
      }


    })(n)(zp, dir)

  def seek[A, N <: Nat](n : N)(zp : Zipper[A, N], addr : Address[N]) : ShapeM[Zipper[A, N]] = 
    (new NatCaseSplit0 {

      type Out[N <: Nat] = (Zipper[A, N], Address[N]) => ShapeM[Zipper[A, N]]

      def caseZero : Out[_0] = {
        case (zp, ()) => Monad[ShapeM].pure(zp)
      }

      def caseSucc[P <: Nat](p : P) : Out[S[P]] = {
        case (zp, Nil) => Monad[ShapeM].pure(zp)
        case (zp, d :: ds) =>
          for {
            zpP <- caseSucc(p)(zp, ds)
            zpN <- visit(p)(zpP, d)
          } yield zpN
      }

    })(n)(zp, addr)

//   def parentWhich[N <: Nat, A](n : N)(zp : Zipper[N, A])(f : A => Boolean) : Option[Zipper[N, A]] = 
//     (new NatCaseSplit {

//       type Out[N <: Nat] = Zipper[N, A] => Option[Zipper[N, A]]

//       def caseZero : Out[_0] = 
//         zp => None

//       def caseSucc[P <: Nat](p : P) : Out[S[P]] = {
//         case (fcs, Nil) => None
//         case (fcs, (a, deriv) :: cs) => {
//           val parent : Zipper[S[P], A] = (Node(a, plug(p)(deriv, fcs)), cs)
//           if (f(a)) Some(parent) else parentWhich(S(p))(parent)(f)
//         }
//       }

//     })(n)(zp)

  def globDerivative[A, N <: Nat](n : N) : Derivative[A, N] = 
    (new NatCaseSplit0 {

      type Out[N <: Nat] = Derivative[A, N]

      def caseZero : Out[_0] = ()

      def caseSucc[P <: Nat](p : P) : Out[S[P]] = 
        (plug(p)(globDerivative(p), Leaf(S(p))), Nil)

    })(n)

}

object Zipper extends ZipperFunctions