package japgolly.ctenv.plugin

import dotty.tools.dotc.ast.tpd.*
import dotty.tools.dotc.core.Contexts.*
import scala.Console.*

object Debug {
  private inline val enabled = false

  inline def init(): Unit =
    inline if enabled then
      println()

  inline def show(inline name: String, inline t: Tree)(using Context): Unit =
    inline if enabled then
      println(s"${RED_B}${WHITE}${name}:${RESET}")
      show(t)

  inline def show(inline t: Tree)(using Context): Unit =
    inline if enabled then
      var s = t.show
      if !s.contains("package") then s = s.replace('\n', ' ').replaceAll(" {2,}", " ")
      println(s)
      println(t)
      println()

  inline def log(inline msg: String): Unit =
    inline if enabled then println(msg)

  inline def logMethods(a: Any): Unit =
    inline if enabled then
      println()
      a.getClass.getMethods.nn.map(m => s"${m.nn.getName} -- $m").sorted.foreach(println)
      println()
}