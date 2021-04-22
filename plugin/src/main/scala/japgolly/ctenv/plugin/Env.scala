package japgolly.ctenv.plugin

final case class Env(asMap: Map[String, String]) {
  def apply(key: String): Option[String] =
    asMap.get(key)
}

object Env {

  def parse(options: Iterable[String]): Env = {
    var m = Map.empty[String, String]
    for (s <- options.iterator) {
      val i = s.indexOf('=')
      if i > 0 then
        // -P:ctenv:key=value
        val key = s.take(i)
        val value = s.drop(i + 1)
        m = m.updated(key, value)
      else if i < 0 then
        // -P:ctenv:key
        val key = s
        if !m.contains(key) then m = m.updated(key, "")
    }
    new Env(m)
  }
}
