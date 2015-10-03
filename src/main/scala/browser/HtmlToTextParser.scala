package browser

import java.text.ParseException

import scala.util.matching.Regex

/**
 * Ethan Petuchowski
 * 10/2/15
 */
object HtmlToTextParser extends App {
    println("this is the tester")
    test()
    def test() {
        val asdf = HtmlToTextParser.parse("asdf")
        println(asdf)
        println(asdf == "asdf")
        val littleP = HtmlToTextParser.parse("<p>asdf</p>")
        println(littleP)
        println(littleP == "\nasdf\n")
    }

    /**
     * format the string `s` according to the tags on the `tagStack`
     * ie. if the stack is [li, ol] --- wow I don't know how to handle that.
     * but if the stack is [li, ul] -- then it should be "* the text"
     * but that's obviously not nearly general enough to be useful....
     */
    def formatText(s: String, tagStack: List[String]): String = {
        ""
    }

    def parse(htmlText: String): String = {
        var tagStack = List.empty[String]
        var textToParse = htmlText
        var outputText = ""
        val START_TAG: Regex = """<([^/>]+)>""".r
        val END_TAG: Regex = """</([^>]+)>""".r
        while (textToParse.nonEmpty) {

            // find the next start and end tags
            val firstStart = START_TAG.findFirstMatchIn(htmlText)
            val firstEnd = END_TAG.findFirstMatchIn(htmlText)

            // figure out whether the *next* tag is a start or an end
            val startIsBeforeEnd = firstStart.isDefined && (
                firstEnd.isEmpty || firstEnd.get.start(0) > firstStart.get.start(0)
            )

            // if a start is first, add the current text to the output,
            // and add the tag to the stack
            if (startIsBeforeEnd) {
                val start       = firstStart.get
                val tagName     = start.group(1)
                val tagStartLoc = start.start(0)
                val tagEndLoc   = start.end(0)
                val textUntilTag = htmlText.take(tagStartLoc)

                outputText += formatText(textUntilTag, tagStack)
                textToParse = htmlText.drop(tagEndLoc)
                tagStack ::= tagName
            } else if (firstEnd.isDefined) {
                val end = firstEnd.get
                val tagName = end.group(1)
                val tagStartLoc = end.start(0)
                val tagEndLoc = end.end(0)
                val textUntilTag = htmlText.take(tagStartLoc)

                // ok so maybe it's more strict than a real browser's html parser...
                if (tagName != tagStack.head) {
                    throw new ParseException(
                        s"tag $tagName doesn't properly close tag ${tagStack.head}",
                        tagStartLoc
                    )
                }

                outputText += formatText(textUntilTag, tagStack)
                textToParse = htmlText.drop(tagEndLoc)
                tagStack = tagStack.tail
            } else {

            }
        }
    }
}
