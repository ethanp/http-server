package server

import java.io.{File, PrintStream, InputStreamReader, BufferedReader}
import java.net.Socket

import akka.actor.{Props, Actor}
import server.HTTP._

import scala.collection.mutable

/**
 * Ethan Petuchowski
 * 9/30/15
 */
class ClientConnection(socket: Socket) extends Actor {
    override def receive = { case x => println(s"received something?: $x") }
    val readIn = new BufferedReader(new InputStreamReader(socket.getInputStream))
    val writeOut = new PrintStream(socket.getOutputStream)
    def readRequest(): Option[Request] = {
        println("reading request")

        val (method, path) = {
            val requestLine = readIn.readLine()
            if (requestLine == null) {
                context.stop(self)
                return None
            }
            val firstWord = requestLine.takeWhile(_ != ' ')
            val method = Method.parse(firstWord)
            // \s is whitespace, \S is non-whitespace
            val pathPattern = """^\S+\s+(\S+)""".r
            val parsedPath: String = pathPattern
                .findFirstMatchIn(requestLine)
                .map(_.group(1))
                .getOrElse("/")

            method → parsedPath
        }

        val headers = {
            var line = readIn.readLine()
            val headers = new Headers
            while (line != null && !line.isEmpty) {
                headers.addParsed(line)
                line = readIn.readLine()
            }
            headers
        }

        val body = method match {
            case _: HasBody =>
                val bodyBuilder = new StringBuilder
                var line = readIn.readLine()
                while (line != null && !line.isEmpty) {
                    bodyBuilder.append(s"$line\n")
                    line = readIn.readLine()
                }
                bodyBuilder.toString()
            case _ => ""
        }

        Some(Request(method, path, headers, body))
    }

    def respond(optRequest: Option[Request]): Unit = {
        if (optRequest.isEmpty) return
        val request = optRequest.get
        val statusCode = "200 OK"
        val responseStatus = s"HTTP/1.1 $statusCode"
        val responseHeaders = {
            new Headers(mutable.Map[String, String](
                "Content-Type" → "text/html; charset=UTF-8",
                "Accept-Ranges" → "bytes",
                "Connection" → "close" // no persistence at this time.
            ))
        }
        val body = s"<h1>aww yeah.</h1><p>${request.path}</p>" + request.dirListing
        responseHeaders.addPair("Content-Length" → body.length.toString)
        val responseComponents = Seq(responseStatus, responseHeaders.httpString, body)
        val response = responseComponents.mkString("", CRLF, CRLF)
        println(response)
        writeOut.print(response)

        // LowPriorityTodo eventually allow persistent connections
        writeOut.close()
    }

    val request = readRequest()
    respond(request)

    // LowPriorityTodo eventually allow persistent connections
    socket.close()

    context.stop(self)
}

object ClientConnection {
    val BASE_LOC = new File(".")
    def props(socket: Socket) = Props(classOf[ClientConnection], socket)
}


case class Request(method: HTTP.Method, path: String, headers: Headers, body: String) {

    def readFile(file: File): String = {
        io.Source.fromFile(file).getLines().map { line =>
            HtmlElement("pre", text = line).render
        }.mkString("\n")
    }

    def dirListing(): String = {
        println(s"fetching: $path")
        val pathFile = new File(ClientConnection.BASE_LOC, path)
        if (!pathFile.exists()) return "File DNE"
        if (!pathFile.isDirectory) return readFile(pathFile)
        val dirListing = HtmlElement("ul")
        pathFile.listFiles() foreach { f =>
            val li = HtmlElement("li")
            val prefix = path match {
                case "/" => "." + path
                case _   => path + "/"
            }
            dirListing addChild (
                li addChild HtmlElement(
                    tagName     = "a",
                    attributes  = Map("href" → (prefix+f.getName)),
                    text        = f.getName
                )
            )
        }
        dirListing.render
    }
}


