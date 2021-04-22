package tests1

import japgolly.ctenv.CTEnv

object Usage {

  type SN = String | Null

  object getOrNull {

    val v_a      = CTEnv.getOrNull("a")
    val v_z      = CTEnv.getOrNull("z")
    val vs_a: SN = CTEnv.getOrNull("a")
    val vs_z: SN = CTEnv.getOrNull("z")

    inline val iv_a = CTEnv.getOrNull("a")
    // TODO: https://github.com/lampepfl/dotty/issues/12177
    // inline val iv_z = CTEnv.getOrNull("z")

    def d_a      = CTEnv.getOrNull("a")
    def d_z      = CTEnv.getOrNull("z")
    def ds_a: SN = CTEnv.getOrNull("a")
    def ds_z: SN = CTEnv.getOrNull("z")

    inline def id_a      = CTEnv.getOrNull("a")
    inline def id_z      = CTEnv.getOrNull("z")
    inline def ids_a: SN = CTEnv.getOrNull("a")
    inline def ids_z: SN = CTEnv.getOrNull("z")

    transparent inline def tid_a      = CTEnv.getOrNull("a")
    transparent inline def tid_z      = CTEnv.getOrNull("z")
    transparent inline def tids_a: SN = CTEnv.getOrNull("a")
    transparent inline def tids_z: SN = CTEnv.getOrNull("z")
  }
}
