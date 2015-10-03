package browser

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import java.net.Socket
import java.util.Scanner

import server.HTTP.{Get, Headers}
import server.Request

import scala.collection.mutable

/**
 * Ethan Petuchowski
 * 10/2/15
 */
class TextBrowser {

}

object TextBrowser extends App {
    val sc = new Scanner(System.in)
    while (true) {
        println("enter an Earl")
        val earl = sc.nextLine()
        println("don't matter, you're getting localhost:8080 ! (LOLOLOLOL)")
        val getReq = s"GET / HTTP/1.1"
        val headers = new Headers(mutable.Map(
            "Host" → "localhost:8080",
            "Connection" → "close"
        ))
        val socket = new Socket("localhost", 8080)
        val out = new OutputStreamWriter(socket.getOutputStream)

        val request = Request(
            method  = Get,
            path    = "/",
            headers = headers,
            body    = ""
        )
        val reqString = request.httpString
        println(reqString)
        out.write(reqString)
        out.flush()

        val in = new BufferedReader(new InputStreamReader(socket.getInputStream))
        var line = in.readLine()
        while (line != null) {
            println(s"[response] $line")
            line = in.readLine()
        }
        socket.close()
    }
}
