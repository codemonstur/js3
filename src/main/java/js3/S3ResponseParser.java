package js3;

import xmlparser.parsing.EventParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class S3ResponseParser implements EventParser {
    private final List<String> values = new ArrayList<>();

    private final String elementName;
    private boolean shouldStoreText = false;
    private StringBuilder tagText;

    public S3ResponseParser(final String elementName) {
        this.elementName = elementName;
    }

    public List<String> getList() {
        return values;
    }

    @Override
    public void startNode(final String name, final Map<String, String> attrs) {
        if (elementName.equalsIgnoreCase(name)) {
            tagText = new StringBuilder();
            shouldStoreText = true;
        }
    }

    @Override
    public void endNode() {
        if (shouldStoreText) {
            values.add(tagText.toString());
            shouldStoreText = false;
        }
    }

    @Override
    public void someText(final String txt) {
        if (shouldStoreText) tagText.append(txt);
    }

}
