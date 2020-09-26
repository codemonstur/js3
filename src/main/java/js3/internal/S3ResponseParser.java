package js3.internal;

import js3.model.S3ObjectMetaData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public enum S3ResponseParser {;

    public static Document toXmlDocument(final byte[] xml) throws IOException {
        try (final InputStream inputStream = new ByteArrayInputStream(xml)) {
            final Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(inputStream);
            document.normalize();
            return document;
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Unable to parse aws s3 xml ", e);
        }
    }

    public static List<String> toBucketsList(final Document s3XmlDocument) {
        final NodeList elements = s3XmlDocument.getDocumentElement().getElementsByTagName("Buckets").item(0).getChildNodes();
        final List<String> bucketNames = new ArrayList<>(elements.getLength());
        for (int i = 0; i < elements.getLength(); i++) {
            final Element element = (Element) elements.item(i);

            bucketNames.add(element.getElementsByTagName("Name").item(0).getTextContent());
        }
        return bucketNames;
    }

    public static List<S3ObjectMetaData> toObjectsList(final Document s3XmlDocument) {
        final NodeList elements = s3XmlDocument.getDocumentElement().getElementsByTagName("Contents");
        final List<S3ObjectMetaData> objectList = new ArrayList<>(elements.getLength());
        for (int i = 0; i < elements.getLength(); i++) {
            final Element element = (Element) elements.item(i);

            final String key = element.getElementsByTagName("Key").item(0).getTextContent();
            final String etag = element.getElementsByTagName("ETag").item(0).getTextContent();
            final Long size = Long.valueOf(element.getElementsByTagName("Size").item(0).getTextContent());
            final String lastModified = element.getElementsByTagName("LastModified").item(0).getTextContent();

            objectList.add(new S3ObjectMetaData(key, etag, size, lastModified, null, null));
        }
        return objectList;
    }

    public static String getNextContinuationToken(final Document s3XmlDocument) {
        /*
        xml section describing this looks like
        <NextContinuationToken>14A3Bj7/8L49hvCZhqecpzT5OMIu7FwVz483Lmh3zo2HCC0JjlHwTWYZIoYV4+Ao1</NextContinuationToken>
        <KeyCount>1000</KeyCount>
        <MaxKeys>1000</MaxKeys>
        <IsTruncated>true</IsTruncated>
        */
        final Element documentElement = s3XmlDocument.getDocumentElement();
        final String isTruncated = getSimpleXmlItemContent(documentElement, "IsTruncated");
        final String nextContinuationToken = getSimpleXmlItemContent(documentElement, "NextContinuationToken");
        if (Boolean.valueOf(isTruncated).equals(true)) {
            return nextContinuationToken;
        }
        return null;
    }

    private static String getSimpleXmlItemContent(final Element parentElement, final String item) {
        NodeList foundElements = parentElement.getElementsByTagName(item);
        if (foundElements.getLength() > 0) {
            return foundElements.item(0).getTextContent();
        }
        return null;
    }

}
