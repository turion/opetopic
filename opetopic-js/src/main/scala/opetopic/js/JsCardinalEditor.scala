/**
  * JsCardinalEditor.scala - Generic client side CardinalEditor
  * 
  * @author Eric Finster
  * @version 0.1 
  */

package opetopic.js

import scala.collection.mutable.ListBuffer

import org.scalajs.dom
import org.scalajs.jquery._
import scalatags.JsDom.all._
import scala.scalajs.js.Dynamic.{literal => lit}

import opetopic._
import JsDomFramework._
import JQuerySemanticUI._

abstract class JsCardinalEditor[A[_ <: Nat]] { thisJsEditor =>

  implicit val vf: VisualizableFamily[A]

  //============================================================================================
  // INSTANCE CLASS
  //

  class EditorInstance {

    val ce = CardinalEditor[A]

    ce.onSelectAsRoot = (boxsig: Sigma[EditorBox]) => {

      @natElim
      def runSelectEvent[N <: Nat](n: N)(box: EditorBox[N]) : Unit = {
        case (Z, box) => onObjectSelect(ce)(box)
        case (S(p: P), box) => onCellSelect[P](ce)(box)
      }

      rootBox = Some(boxsig)
      runSelectEvent(boxsig.n)(boxsig.value)

    }

    ce.onRefresh = () => {

      ce.galleryViewport.width = tabWidth
      ce.galleryViewport.height = tabHeight
      ce.galleryViewport.setBounds(ce.bounds)

    }

    ce.refreshAll

    type EditorBox[N <: Nat] = ce.CardinalCellBox[N]
    var rootBox : Option[Sigma[EditorBox]] = None

    // trait BoxAction[A] {
    //   def objectAction(box : EditorBox[_0]) : EditorM[A]
    //   def cellAction[P <: Nat](p : P)(box: EditorBox[S[P]]) : EditorM[A]
    // }
    
    // @natElim
    // def dispatchAction[A, N <: Nat](n: N)(box: EditorBox[N], action: BoxAction[A]) : EditorM[A] = {
    //   case (Z, box, action) => action.objectAction(box)
    //   case (S(p), box, action) => action.cellAction(p)(box)
    // }

    // def withSelection[A](action: BoxAction[A]) : EditorM[A] =
    //   for {
    //     boxsig <- attempt(rootBox, "No box selected")
    //     a <- dispatchAction(boxsig.n)(boxsig.value, action)
    //   } yield a

  }

  //============================================================================================
  // ACTION CLASS
  //


  //============================================================================================
  // SELECTION HANDLERS
  //

  def onObjectSelect(editor: CardinalEditor[A])(box: editor.CardinalCellBox[_0]) : Unit = ()
  def onCellSelect[P <: Nat](editor: CardinalEditor[A])(box: editor.CardinalCellBox[S[P]]) : Unit = ()

  //============================================================================================
  // EDITOR MANAGEMENT
  //

  val instances: ListBuffer[EditorInstance] = ListBuffer()

  var editorCount: Int = 0
  var activeEditor: Option[EditorInstance] = None

  def newEditor: Unit = {

    val editor = new EditorInstance
    editorCount += 1

    val cntStr = editorCount.toString
    val tabName = "tab-" ++ cntStr

    val tabItem = a(cls := "item", "data-tab".attr := tabName)(cntStr).render
    val tab = div(cls := "ui tab", "data-tab".attr := tabName)(
      editor.ce.element.uiElement
    ).render

    jQuery(paginationMenu).append(tabItem)
    jQuery(tabPane).append(tab)

    jQuery(tabItem).tab(lit(
      onVisible = (s: String) => { activeEditor = Some(editor) }
    ))

    jQuery(tabItem).click()

  }

  //============================================================================================
  // UI STATE
  //

  var tabWidth : Int = 0
  var tabHeight : Int = 0

  //============================================================================================
  // UI ELEMENTS
  //

  val tabPane = div(cls := "ui middle attached nofocus segment", tabindex := 0, style := "padding: 0px; min-height: 300px").render
  val paginationMenu = div(cls := "ui pagination menu").render
  
  val topMenu = 
    div(cls := "ui top attached menu")(
      a(cls := "item")("Shape", i(cls := "dropdown icon"))
    ).render


  val bottomMenu = 
    div(cls := "ui bottom attached segment")(
      div(cls := "ui grid")(
        div(cls := "fourteen wide column")(
          paginationMenu
        ),
        div(cls := "two wide right aligned column")(
          button(cls := "ui icon button", onclick := { () => newEditor })(i(cls := "add icon"))
        )
      )
    ).render

  val uiElement = 
    div(topMenu, tabPane, bottomMenu).render

  //============================================================================================
  // INITIALIZATION
  //

  def initialize: Unit = {

    // The idea is that we're going to hook the resize event
    // on the parent element and manage the viewport ourselves..

    tabWidth = jQuery(tabPane).width.toInt
    tabHeight = jQuery(tabPane).height.toInt

    println("Tab dimensions are: (" + tabWidth.toString + ", " + tabHeight.toString + ")")

    // Install the key handler
    jQuery(uiElement).keypress((e : JQueryEventObject) => {
      e.which match {
        case 101 => for { editor <- activeEditor } { editor.ce.extrudeSelection }
        case 100 => for { editor <- activeEditor } { editor.ce.extrudeDrop }
        case 112 => for { editor <- activeEditor } { editor.ce.sprout }
        case _ => ()
      }
    })


    newEditor

  }

}
