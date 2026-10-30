package js3;

import js3.internal.S3ResponseParser;
import js3.model.S3ObjectMetaData;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class S3ResponseParserTest {
    private static final String BUCKETS_XML = "<ListAllMyBucketsResult><Buckets><Bucket><Name>b1</Name></Bucket><Bucket><Name>b2</Name></Bucket></Buckets></ListAllMyBucketsResult>";
    private static final String OBJECTS_XML = "<ListBucketResult><Contents><Key>k1</Key><ETag>e1</ETag><Size>10</Size><LastModified>2020-01-01T00:00:00Z</LastModified></Contents></ListBucketResult>";
    private static final String TRUNCATED_XML = "<ListBucketResult><IsTruncated>true</IsTruncated><NextContinuationToken>tok</NextContinuationToken></ListBucketResult>";

    @Test
    void toXmlDocumentAndBuckets() throws Exception {
        Document doc = S3ResponseParser.toXmlDocument(BUCKETS_XML.getBytes());
        assertEquals(List.of("b1", "b2"), S3ResponseParser.toBucketsList(doc));
    }

    @Test
    void toObjectsList() throws Exception {
        Document doc = S3ResponseParser.toXmlDocument(OBJECTS_XML.getBytes());
        List<S3ObjectMetaData> objects = S3ResponseParser.toObjectsList(doc);
        assertEquals(1, objects.size());
        assertEquals("k1", objects.get(0).key);
        assertEquals(10L, objects.get(0).size);
    }

    @Test
    void getNextContinuationToken() throws Exception {
        Document doc = S3ResponseParser.toXmlDocument(TRUNCATED_XML.getBytes());
        assertEquals("tok", S3ResponseParser.getNextContinuationToken(doc));
        Document doc2 = S3ResponseParser.toXmlDocument("<ListBucketResult><IsTruncated>false</IsTruncated></ListBucketResult>".getBytes());
        assertNull(S3ResponseParser.getNextContinuationToken(doc2));
    }
}
