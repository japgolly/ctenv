package japgolly.ctenv

import scala.compiletime.error
import scala.annotation.compileTimeOnly


object CTEnv:

  // @compileTimeOnly("Illegal reference to `japgolly.ctenv.CTEnv.getOrNull`")
  // inline def getOrNull(inline key: String): String | Null =
  //   ???

  @compileTimeOnly("Illegal reference to `japgolly.ctenv.CTEnv.getOrNull`")
  transparent inline def getOrNull(inline key: String): String | Null =
    "CTENV:135ac5f7-7653-406c-8bd8-a8b87945c090"
    // null
    // error("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy")
    //   "`CTEnv.getOrNull` was not evaluated by the compiler plugin.\n" +
    //   "You probably haven't added this to your sbt:\n" +
    //   "\n" +
    //   "  libraryDependencies += addCompilerPlugin(\"com.github.japgolly.ctenv\" %% \"plugin\" % VER)\n" +
    //   " "
    // )

  // @compileTimeOnly("Illegal reference to `japgolly.ctenv.CTEnv.get`")
  // transparent inline def get_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx(inline key: String): Option[String] =
  //   inline getOrNull(key) match
  //     case null => None
  //     case v    => Some[v.type](v)

  // @compileTimeOnly("Illegal reference to `japgolly.ctenv.CTEnv.getOrElse`")
  // transparent inline def getOrElse(inline key: String, inline default: String): String =
  //   inline getOrNull(key) match
  //     case null => default
  //     case v    => v
