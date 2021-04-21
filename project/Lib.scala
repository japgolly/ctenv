import sbt._
import Keys._
import com.jsuereth.sbtpgp.PgpKeys._
import xerial.sbt.Sonatype.autoImport._

object Lib {
  type PE = Project => Project

  def byScalaVersion[A](f: PartialFunction[(Long, Long), Seq[A]]): Def.Initialize[Seq[A]] =
    Def.setting(CrossVersion.partialVersion(scalaVersion.value).flatMap(f.lift).getOrElse(Nil))

  def publicationSettings(ghProject: String): PE =
    _.settings(
      publishTo := sonatypePublishToBundle.value,
      pomExtra :=
        <scm>
          <connection>scm:git:github.com/japgolly/{ghProject}</connection>
          <developerConnection>scm:git:git@github.com:japgolly/{ghProject}.git</developerConnection>
          <url>github.com:japgolly/{ghProject}.git</url>
        </scm>
        <developers>
          <developer>
            <id>japgolly</id>
            <name>David Barri</name>
          </developer>
        </developers>
  )

  def preventPublication: PE =
    _.settings(publish / skip := true)
}
