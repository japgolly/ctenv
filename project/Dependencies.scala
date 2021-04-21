import sbt._
import sbt.Keys._
import Lib.byScalaVersion

object Dependencies {

  object Ver {

    // Externally observable
    val Scala212 = "2.12.13"
    val Scala213 = "2.13.5"
    val Scala3   = "3.0.0-RC3"

    // Internal
    val MTest    = "0.7.9"
  }

  object Dep {
    val ScalaCompiler = Def.setting {
      val ver = scalaVersion.value
      CrossVersion.partialVersion(ver).get match {
        case (2, _) => "org.scala-lang"  % "scala-compiler"  % ver
        case (3, _) => "org.scala-lang" %% "scala3-compiler" % ver
      }
    }
    val MTest = Def.setting("com.lihaoyi" %% "utest" % Ver.MTest)
  }

}
