import sbt._
import sbt.Keys._
import com.jsuereth.sbtpgp.PgpKeys
import sbtrelease.ReleasePlugin.autoImport._

object Build {
  import Dependencies._
  import Lib._

  private val publicationSettings =
    Lib.publicationSettings("ctenv")

  private def scalacCommonFlags = Seq(
    "-deprecation",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-unchecked",                                    // Enable additional warnings where generated code depends on assumptions.
    "-Wconf:msg=may.not.be.exhaustive:e",            // Make non-exhaustive matches errors instead of warnings
    "-Wdead-code",                                   // Warn when dead code is identified.
    "-Wunused:explicits",                            // Warn if an explicit parameter is unused.
    "-Wunused:implicits",                            // Warn if an implicit parameter is unused.
    "-Wunused:imports",                              // Warn if an import selector is not referenced.
    "-Wunused:locals",                               // Warn if a local definition is unused.
    "-Wunused:nowarn",                               // Warn if a @nowarn annotation does not suppress any warnings.
    "-Wunused:patvars",                              // Warn if a variable bound in a pattern is unused.
    "-Wunused:privates",                             // Warn if a private member is unused.
    "-Xlint:adapted-args",                           // An argument list was modified to match the receiver.
    "-Xlint:constant",                               // Evaluation of a constant arithmetic expression resulted in an error.
    "-Xlint:delayedinit-select",                     // Selecting member of DelayedInit.
    "-Xlint:deprecation",                            // Enable -deprecation and also check @deprecated annotations.
    "-Xlint:eta-zero",                               // Usage `f` of parameterless `def f()` resulted in eta-expansion, not empty application `f()`.
    "-Xlint:implicit-not-found",                     // Check @implicitNotFound and @implicitAmbiguous messages.
    "-Xlint:inaccessible",                           // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                              // A type argument was inferred as Any.
    "-Xlint:missing-interpolator",                   // A string literal appears to be missing an interpolator id.
    "-Xlint:nonlocal-return",                        // A return statement used an exception for flow control.
    "-Xlint:nullary-unit",                           // `def f: Unit` looks like an accessor; add parens to look side-effecting.
    "-Xlint:option-implicit",                        // Option.apply used an implicit view.
    "-Xlint:poly-implicit-overload",                 // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",                         // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                            // In a pattern, a sequence wildcard `_*` should match all of a repeated parameter.
    "-Xlint:valpattern",                             // Enable pattern checks in val definitions.
    "-Xmixin-force-forwarders:false",                // Only generate mixin forwarders required for program correctness.
    "-Xno-forwarders",                               // Do not generate static forwarders in mirror classes.
    "-Ycache-macro-class-loader:last-modified",
    "-Ycache-plugin-class-loader:last-modified",
    "-Yjar-compression-level", "9",                  // compression level to use when writing jar files
    "-Yno-generic-signatures",                       // Suppress generation of generic signatures for Java.
    "-Ypatmat-exhaust-depth", "off",
  )

  private def scalac2Flags = Seq(
    "-opt:l:inline",
    "-opt-inline-from:japgolly.**",
    "-opt-inline-from:scala.**",
  )

  private def scalac3Flags = Seq(
    "-new-syntax",
    "-Yexplicit-nulls",
    "-Yindent-colons",
  )

  private val commonSettings: PE =
    _.settings(
      scalaVersion                  := Ver.Scala3,
      crossScalaVersions            := Seq(Ver.Scala212, Ver.Scala213, Ver.Scala3),
      scalacOptions                ++= scalacCommonFlags,
      scalacOptions                ++= byScalaVersion {
                                         case (2, _) => scalac2Flags
                                         case (3, _) => scalac3Flags
                                       }.value,
      testFrameworks                := Nil,
      incOptions                    := incOptions.value,
      updateOptions                 := updateOptions.value.withCachedResolution(true),
      releasePublishArtifactsAction := PgpKeys.publishSigned.value,
      releaseTagComment             := s"v${(ThisBuild / version).value}",
      releaseVcsSign                := true,
    )

  private def utestSettings: PE =
    _.settings(
      libraryDependencies += Dep.MTest.value % Test,
      testFrameworks      += new TestFramework("utest.runner.Framework"),
    )

  // ===================================================================================================================

  lazy val root = project
    .in(file("."))
    .configure(commonSettings, preventPublication)
    .aggregate(
      compilerPlugin,
    )

  lazy val compilerPlugin = project
    .in(file("compiler-plugin"))
    .configure(commonSettings, publicationSettings, utestSettings)
    .settings(
      moduleName := "compiler-plugin",
      libraryDependencies += Dep.ScalaCompiler.value % Provided,
    )
}
