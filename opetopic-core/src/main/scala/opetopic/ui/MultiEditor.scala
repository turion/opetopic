/**
  * MultiEditor.scala - An editor for Multitopes
  * 
  * @author Eric Finster
  * @version 0.1 
  */

package opetopic.ui

import opetopic._
import opetopic.mtl._

sealed trait Multicard[+A]
case class Root[+A](card: SCardinal[A]) extends Multicard[A]
case class Level[+A](cmpl: SComplex[Multicard[A]]) extends Multicard[A]

object Multicard {

  // Great, so if we have a multitope of these guys, we can collapse
  // it down.  This should be what happens when we finish a level edit.
  def stack[A](m: Multitope[Multicard[A]]): Multicard[A] =
    m match {
      case Base(c) => Level(c)
      case Up(c) => Level(c.map(stack(_)))
    }

  // Great, so this lifts the multicardinal structure up.
  def splitLevel[A](m: Multicard[A], i: Int): Option[Multicard[Multicard[A]]] =
    m match {
      case Root(card) => None
      case Level(cmplx) =>
        if (i == 0) Some(Root(SCardinal(cmplx))) else {
          for {
            childCmplx <- cmplx.traverse(splitLevel(_, i-1))
          } yield Level(childCmplx)
        }
    }

  // This means we can apply, say, an extrusion to each of the cardinals
  // at the leaves of our multicardinal.
  def traverseRoots[A](m: Multicard[A])(f: SCardinal[A] => Option[SCardinal[A]]): Option[Multicard[A]] =
    m match {
      case Root(card) => f(card).map(Root(_))
      case Level(cmplx) => cmplx.traverse(traverseRoots(_)(f)).map(Level(_))
    }

  implicit def multiRenderable[A](implicit r: Renderable[A]): Renderable[Multicard[A]] =
    new Renderable[Multicard[A]] {
      def render(f: UIFramework)(m: Multicard[A]): f.CellRendering = {
        import f._
        import isNumeric._
        f.CellRendering(
          spacer(Bounds(fromInt(0), fromInt(0), fromInt(600), fromInt(600)))
        )
      }
    }

  def fullRenderable[A](implicit r: Renderable[A]): Renderable[Multicard[A]] =
    new Renderable[Multicard[A]] {
      def render(f: UIFramework)(m: Multicard[A]): f.CellRendering = {
        m match {
          case Root(card) => {
            val viewer = new SimpleStaticGallery[Polarity[A], f.type](f)(card.cardinalComplex)
            f.CellRendering(viewer.boundedElement)
          }
          case Level(cmplx) => {
            val viewer = new SimpleStaticGallery[Multicard[A], f.type](f)(cmplx)(fullRenderable(r))
            f.CellRendering(viewer.boundedElement)
          }
        }
      }
    }

}

class MultiEditor[A: Renderable, F <: ActiveFramework](frmwk: F) {

  import Multicard._
  type OptA = Option[A]

  // Okay.  We want to make a bunch of level viewers, as well as the main cardinal
  // viewer and make these available to the javascript implementation.

  val valueEditor = new StableEditor[A, F](frmwk)(SCardinal(||(SDot(None))))
  val layerEditor = new StableEditor[Multicard[OptA], F](frmwk)(SCardinal(||(SDot(None))))

  // Right-o.  Next step should be to think about how we are going to organize
  // this thing.  Maybe we should call them "layers" instead of levels....

  // This will be the main state of things.
  var multicard : Multicard[OptA] = Root(SCardinal(||(SDot(None))))
  var layers: Seq[Layer] = Seq()

  def extrude: Unit = {
    state match {
      case ValueEdit => {

        // Perform the same extrusion for all faces ...

        for {
          pr <- valueEditor.extrudeSelectionWith(None, None)
          (addr, msk) = pr
          mc <- traverseRoots(multicard)(c => {
            val extC = if (addr.dim == c.dim) c.extend(None) else c
            extC.extrudeWithMask(addr, None, None)(msk)
          })
        } { multicard = mc }

      }
      case LayerEdit(layer, model, splitting) => {

        // The model is a blanked out copy of what is attached to lower
        // dimensions.  We will always extrude new faces with a copy of
        // this model, ensuring that our multitope is well-formed.

        for {
          pr <- layerEditor.extrudeSelectionWith(Some(model), Some(model))
          (addr, msk) = pr
          mc <- traverseRoots(splitting)(c => {
            val extC = if (addr.dim == c.dim) c.extend(model) else c
            extC.extrudeWithMask(addr, model, model)(msk)
          })
        } {

          // Right, but now we have to update the state
          state = LayerEdit(layer, model, mc)

        }

      }
    }
  }

  def addLayer: Unit = {

    multicard = Level(||(SDot(multicard)))
    layers = Layer(layers.length) +: layers

  }

  def editLayer(layer: Layer): Unit = {

    // Split the current multicardinal at the requested layer.
    val splitIndex = layers.length - layer.index - 1

    // Now, extract the model for this layer and load it into the layer editor
    for {
      msplit <- splitLevel(multicard, splitIndex)
    } {

      def extractLayerModel(m: Multicard[Multicard[OptA]]): Option[(SCardinal[Multicard[OptA]], Multicard[OptA])] =
        m match {
          case Root(card) =>
            for {
              rv <- card.initial.rootValue // This isn't done! You have to blank out the current contents ...
            } yield (card, rv.baseValue)
          case Level(cmplx) => extractLayerModel(cmplx.head.baseValue)
        }

      for {
        pr <- extractLayerModel(msplit)
        (card, model) = pr
      } {

        layerEditor.cardinal = card.map(Some(_))
        state = LayerEdit(layer, model, msplit)

      }

    }

  }

  def closeLayer: Unit = {

    def closeWithFaces(m: Multicard[Multicard[OptA]],
      ps: Suite[(Multicard[OptA], Multicard[OptA])],
      dim: Int, addr: SAddr
    ): Option[Multicard[OptA]] =
      m match {
        case Root(card) =>
          for {
            cmplx <- card.toComplex(ps)
            f <- cmplx.face(dim)(addr)
          } yield Level(f)
        case Level(cmplx) =>
          for {
            m <- cmplx.traverse(closeWithFaces(_, ps, dim, addr))
          } yield Level(m)
      }

    state match {
      case LayerEdit(layer, model, splitting) => {
        layerEditor.selectionRoot match {
          case None => ()
          case Some(cell) => {
            for {
              face <- cell.face
              caddr = cell.cardinalAddress
              dummy = Root(SCardinal(||(SDot(None))))
              ps = layerEditor.panels.map(_ => (dummy, dummy))
              mc <- closeWithFaces(splitting, ps, caddr.dim, caddr.complexAddress)
            } {

              layer.viewer.setComplex(face.map(_ => None))
              layer.viewer.renderAll
              layer.selectInitial

              multicard = mc

              state = ValueEdit

            }
          }
        }
      }
      case _ => ()
    }
  }

  case class Layer(val index: Int) {

    var layerComplex: SComplex[OptA] = ||(SDot(None))

    val viewer: SimpleActiveGallery[OptA, F] =
      new SimpleActiveGallery[OptA, F](frmwk)(layerComplex)

    import viewer.framework.isNumeric._

    viewer.layoutWidth = bnds => fromInt(200)
    viewer.layoutHeight = bnds => fromInt(100)

    selectInitial

    def selectInitial: Unit = {
      for {
        el <- viewer.panels.initial.boxNesting.firstDotValue
      } { el.selectAsRoot }
    }

  }

  sealed trait EditorState
  case object ValueEdit extends EditorState
  case class LayerEdit(
    val layer: Layer,
    val model: Multicard[OptA],
    val splitting: Multicard[Multicard[OptA]]
  ) extends EditorState

  var state: EditorState = ValueEdit

  def fullRender: SimpleStaticGallery[Multicard[OptA], F] =
    new SimpleStaticGallery[Multicard[OptA], F](frmwk)(||(SDot(multicard)))(fullRenderable)

}

