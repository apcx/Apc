package apc.jar

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.nio.file.Path
import javax.xml.parsers.SAXParserFactory

class UnusedResources(lint: Path, private val process: (String) -> Unit = ::println) : DefaultHandler() {

    init {
        SAXParserFactory.newInstance().newSAXParser().parse(lint.toFile(), this)
    }

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        if (qName == "location") process(attributes.getValue(0))
    }
}