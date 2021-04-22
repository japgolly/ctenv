package japgolly.ctenv.plugin

import dotty.tools.dotc.plugins.*

class CTEnvPlugin extends StandardPlugin {

  override val name        = "ctenv"
  override val description = "A compile-time environment configurable by users via scalac flags."
  // TODO: val optionsHelp: Option[String] = None

  // def runsBefore: Set[String] = Set.empty

  def init(options: List[String]): List[PluginPhase] =
    val env = Env.parse(options)
    InterceptApi.phases(env)
}
