package tests1

import japgolly.ctenv.CTEnv
import utest._

object Test extends TestSuite {

  private inline val A = "aardvark"
  private inline def Z = null

  private def assertEq(actual: String | Null, expect: String | Null): Unit =
    if actual != expect then
      def fmt(s: String | Null) = if s == null then "null" else ("\"" + s + "\"")
      val e = new java.lang.AssertionError(s"${fmt(actual)} is supposed to be ${fmt(expect)}")
      e.setStackTrace(Array.empty)
      throw e

  override def tests = Tests {
    "getOrNull" - {
      import Usage.getOrNull.*

      "v_a"  - assertEq(v_a, A)
      // "v_z"  - assertEq(v_z, Z)

      "d_a"  - assertEq(d_a, A)
      "d_z"  - {assertEq(d_z, Z); Usage.getOrNull.getClass.getMethod("d_z")}
      "ds_a" - assertEq(d_a, A)
      "ds_z" - {assertEq(d_z, Z); Usage.getOrNull.getClass.getMethod("d_z")}

      "id_a"  - assertEq(id_a, A)
      "id_z"  - assertEq(id_z, Z)
      "ids_a" - assertEq(ids_a, A)
      "ids_z" - assertEq(ids_z, Z)

      "tid_a"  - assertEq(tid_a, A)
      "tid_z"  - assertEq(tid_z, Z)
      "tids_a" - assertEq(tids_a, A)
      "tids_z" - assertEq(tids_z, Z)

      // "n1" - assertEq(n1, Z)
      // "n2" - assertEq(n2, Z)
      // "iv_a" - assertEq(iv_a, A)
      // "iv_z" - assertEq(iv_z, Z)
    }
  }
}
