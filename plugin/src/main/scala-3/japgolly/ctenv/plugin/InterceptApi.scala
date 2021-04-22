package japgolly.ctenv.plugin

// import ast.tpd.Tree
// import Constants.Constant
// import Decorators._
// import Flags._
// import Names._
// import StdNames.nme
// import SymDenotations._
import dotty.tools.dotc.*
import dotty.tools.dotc.ast.tpd.*
import dotty.tools.dotc.core.*
import dotty.tools.dotc.core.Constants._
import dotty.tools.dotc.core.Contexts.*
import dotty.tools.dotc.core.Decorators._
import dotty.tools.dotc.core.Symbols.Symbol
import dotty.tools.dotc.core.Types._
import dotty.tools.dotc.plugins.*
import dotty.tools.dotc.transform.PostTyper
import dotty.tools.dotc.typer.FrontEnd

object InterceptApi:

  def phases(env: Env): List[PluginPhase] =
    InterceptApi1(env) :: InterceptApi2(env) :: Nil

  private val NamePrefix = "CTEnv.InterceptApi"
  val Pass1Name = NamePrefix + ".1"
  val Pass2Name = NamePrefix + ".2"

  object ConstantValue {
    def unapply(tree: Tree)(using Context): Option[Any] =
      tree match
        case Typed(expr, _) => unapply(expr)
        case Inlined(_, Nil, expr) => unapply(expr)
        case Block(Nil, expr) => unapply(expr)
        case _ =>
          tree.tpe.widenTermRefExpr.normalized match
            case ConstantType(Constant(x)) => Some(x)
            case _ => None
  }

  object PlaceholderType {
    def unapply(tpe: Type)(using Context): Boolean =
      tpe match
        case ConstantType(Constant("CTENV:135ac5f7-7653-406c-8bd8-a8b87945c090")) => true
        case _ => false
  }

  def getConstantValue(t: Tree)(using Context): Option[Any] =
    t match
      case ConstantValue(v)                            => Some(v)
      case Inlined(_, Nil, Typed(ConstantValue(v), _)) => Some(v)
      case _                                           => None

  def reportErrorContentNeeded(t: Tree)(using Context): Any =
    report.error(em"expected a constant value but found: $t", t.srcPos)

  def valueLiteral(value: String | Null, pos: Tree)(using Context): Literal =
    val c = if value == null then Constant(null) else Constant(value)
    Literal(c).withSpan(pos.span)

  def valueType(value: String | Null)(using Context): TypeTree =
    if value == null then
      // TypeTree(ctx.definitions.NullClass.typeRef) // TODO: works in theory but java.lang.NoSuchMethodError at runtime
      TypeTree(ctx.definitions.StringClass.typeRef) // TODO: use when null and not-inline. Maybe Raise bug
    else
      TypeTree(ConstantType(Constant(value)))

end InterceptApi

import InterceptApi.{phases => _, *}

// =====================================================================================================================

/** Intercept methods in non-inline scopes. */
final class InterceptApi1(env: Env) extends PluginPhase:

  override val phaseName = Pass1Name
  override val runsAfter = Set(FrontEnd.name)

  private var initialised = false
  private var CtEnv          : Symbol = _
  private var CtEnv_getOrNull: Symbol = _

  override def prepareForUnit(tree: Tree)(using Context): Context =
    if !initialised then
      Debug.init()
      CtEnv           = Symbols.requiredPackage("japgolly.ctenv.CTEnv").moduleClass
      CtEnv_getOrNull = CtEnv.requiredMethod("getOrNull")
      initialised     = true
    ctx

  override def transformUnit(tree: Tree)(using Context): Tree =
    Debug.show("[1] Unit", tree)
    tree

  // Intercept calls outside of inline contexts
  override def transformInlined(tree: Inlined)(using Context): Tree =
    tree match
      case i @ Inlined(apply @ Apply(applyTree, arg :: Nil), bindings, _) =>
        val method = applyTree.symbol
        if method == CtEnv_getOrNull then
          getConstantValue(arg) match
            case Some(key: String) =>
              val value = env(key).orNull
              val lit = valueLiteral(value, i)
              val replacement = cpy.Inlined(i)(apply, bindings, lit)
              return replacement
            case _ => reportErrorContentNeeded(arg)
        end if
      case _ =>
    tree

  // override def transformDefDef(tree: DefDef)(using Context): Tree =
  //   if tree.symbol.flags.is(Flags.Inline) then
  //     Debug.show("[1] DefDef", tree)
  //   tree

end InterceptApi1

// =====================================================================================================================

final class InterceptApi2(env: Env) extends PluginPhase:

  override val phaseName = Pass2Name
  override val runsAfter = Set(PostTyper.name)

  private var initialised = false
  private var CtEnv          : Symbol = _
  private var CtEnv_getOrNull: Symbol = _

  private var replacements = Map.empty[Tree, String | Null]

  override def prepareForUnit(tree: Tree)(using Context): Context =
    if !initialised then
      Debug.init()
      CtEnv           = Symbols.requiredPackage("japgolly.ctenv.CTEnv").moduleClass
      CtEnv_getOrNull = CtEnv.requiredMethod("getOrNull")
      initialised     = true
    ctx

  override def transformUnit(tree: Tree)(using Context): Tree =
    Debug.show("[2] Unit", tree)
    tree

  // Intercept calls in non-transparent inline contexts
  override def transformTyped(tree: Typed)(using Context): Tree =
    tree match
      case t @ Typed(apply @ Apply(applyTree, arg :: Nil), _) =>
        val method = applyTree.symbol
        if method == CtEnv_getOrNull then
          getConstantValue(arg) match
            case Some(key: String) =>
              val value = env(key).orNull
              val lit = valueLiteral(value, apply)
              val tpt = valueType(value)
              val bdy = Inlined(apply, Nil, lit)
              val replacement = cpy.Typed(t)(bdy, tpt)
              replacements = replacements.updated(replacement, value)
              return replacement
            case _ => reportErrorContentNeeded(arg)
        end if
      case _ =>
    tree

  // Intercept calls in transparent inline contexts
  override def transformApply(tree: Apply)(using Context): Tree =
    tree match
      case apply @ Apply(applyTree, arg :: Nil) =>
        val method = applyTree.symbol
        if method == CtEnv_getOrNull then
          getConstantValue(arg) match
            case Some(key: String) =>
              val value = env(key).orNull
              val lit = valueLiteral(value, apply)
              val replacement = Inlined(apply, Nil, lit)
              replacements = replacements.updated(replacement, value)
              return replacement
            case _ => reportErrorContentNeeded(arg)
        end if
      case _ =>
    tree

  // Refine `inline def` return types
  override def transformDefDef(tree: DefDef)(using Context): Tree =
    inline def isInline           = tree.symbol.flags.is(Flags.Inline)
    inline def isTransparent      = tree.symbol.flags.is(Flags.Transparent)
    inline def inferredReturnType = tree.tpt.startPos == tree.tpt.endPos
    if isInline && (isTransparent || inferredReturnType) then
      for (value <- replacements.get(tree.rhs))
        val tpt = valueType(value)
        val replacement = cpy.DefDef(tree)(tpt = tpt)
        return replacement
    tree

end InterceptApi2
