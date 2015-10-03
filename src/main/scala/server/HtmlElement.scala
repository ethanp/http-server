package server

/**
 * For now, all children HtmlElements are rendered after the element's `text`
 *      I haven't had a use-case for anything different
 *
 * Also, for now, this thing is _mutable_
 *      I haven't had a use-case for anything different
 */
case class HtmlElement(
        tagName     : String                = "p",
    var attributes  : Map[String, String]   = Map.empty,
    var text        : String                = "",
    var children    : List[HtmlElement]     = List.empty,
    var indentation : Int                   = 0
) {
    def indent(i: Int): Unit = {
        indentation = i
        children foreach (_.indent(i + 1))
    }

    def addChild(htmlElement: HtmlElement): HtmlElement = {
        children ::= htmlElement
        htmlElement indent indentation + 1
        this
    }

    def render: String = {
        val attrString = (attributes foldLeft "") { case (cur, (k, v)) => s"$cur $k=$v" }
        val childrenString = (children foldLeft "") (_ + "\n" + _.render)
        val indentTabs = (0 until indentation).foldLeft("") ((tabs, cnt) => tabs + "\t")
        val indentedText = text match {
            case "" => ""
            case _ => s"\n$indentTabs\t$text"
        }
        s"$indentTabs<$tagName$attrString>$indentedText $childrenString\n$indentTabs</$tagName>"
    }
}
