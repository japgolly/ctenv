ThisBuild / homepage     := Some(url("https://github.com/japgolly/ctenv"))
ThisBuild / licenses     := ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0")) :: Nil
ThisBuild / organization := "com.github.japgolly.ctenv"
ThisBuild / shellPrompt  := ((s: State) => Project.extract(s).currentRef.project + "> ")
ThisBuild / startYear    := Some(2021)

lazy val root   = Build.root
lazy val plugin = Build.plugin
lazy val api    = Build.api
lazy val tests1 = Build.tests1
lazy val tests2 = Build.tests2
