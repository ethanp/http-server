package server

import scala.collection.mutable

/**
 * Ethan Petuchowski
 * 9/30/15
 */
object HTTP {

    val CRLF = "\r\n"

    sealed trait Method {
        def name = getClass.getSimpleName.init.toUpperCase
    }
    object Method {
        def parse(str: String) = Methods.find(_.getClass.getSimpleName.init.equalsIgnoreCase(str)).get
    }
    sealed trait Idempotent extends Method
    sealed trait HasBody extends Method
    case object Get extends Idempotent
    case object Head extends Idempotent
    case object Options extends Idempotent
    case object Put extends Idempotent
    case object Delete extends Idempotent
    case object Trace extends Idempotent
    case object Post extends HasBody
    val Methods = Seq(Get, Head, Options, Put, Delete, Trace, Post)

    class Headers(val headers: mutable.Map[String, String] = mutable.Map.empty[String, String]) {
        def addPair(pair: (String, String)): Unit = {
            headers(pair._1) = pair._2
        }

        def parseAndAdd(line: String): Unit = {
            println(line)
            val firstColonIdx = line.indexOf(':')
            val headerKey = (line take firstColonIdx).trim
            val headerVal = (line drop firstColonIdx+1).trim
            headers += headerKey → headerVal
        }

        def httpString = headers.map{ case (k, v) => s"$k: $v" }.mkString(CRLF) + CRLF
    }
}
