package js3.util;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This is our simple SAX parser for handling response XML from
 * S3.  In every case where we parse an XML response, we're looking
 * for the contents of a single tag which may occur multiple times,
 * such as "key" tags when listing bucket contents or "message" tags
 * when parsing error responses.  This class assumes that the target
 * tags don't contain any subtags.
 **/
public final class ObjectListParser extends DefaultHandler {
    private final String m_lookfor;
    private final List<String> m_ids = new ArrayList<String>();
    private boolean m_storeChars = false;
    private StringBuilder m_keyName;

    /**
     * Constructs an object list parser that gather the contents of
     * tags with the given name.
     *
     * @param lookfor The element name to gather character data from
     **/
    public ObjectListParser(final String lookfor) {
        m_lookfor = lookfor;
    }

    /**
     * Once parsing is complete, this retrieves the list of contents
     * of all the matching tags encountered.
     *
     * @return The list of contents of all matching tags [not null]
     **/
    public List<String> getList() {
        return m_ids;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if(qName.equalsIgnoreCase(m_lookfor)) {
            m_keyName = new StringBuilder();
            m_storeChars = true;
        }
    }

    @Override
    public void endElement(String url, String localName, String qName) {
        if(qName.equalsIgnoreCase(m_lookfor)) {
            m_ids.add(m_keyName.toString());
            m_storeChars = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if(m_storeChars) {
            m_keyName.append(ch, start, length);
        }
    }
}

