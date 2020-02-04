package js3;

import js3.pojos.S3Object;
import js3.util.ObjectListParser;
import org.xml.sax.SAXException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;

import static java.util.concurrent.TimeUnit.SECONDS;
import static js3.Functions.*;
import static js3.util.HTTP.toXAmzHeaders;

public final class S3Store {

    private final String host;
    private final String username;
    private final String password;

    private String bucket;

    private static final String SIGNATURE_ALGORITHM = "HmacSHA1";
    private static final long READ_TIMEOUT = SECONDS.toMillis(30);

    public S3Store(final String host, final String username, final String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public S3Store(final String host, final String username, final String password, final String bucket) {
        this(host, username, password);
        this.bucket = bucket;
    }

    /**
     * Sets the bucket to use for operations.
     * 
     * @param bucket The bucket to use [may be null, although some operations 
     *      will fail]
     **/
    public void setBucket(final String bucket) {
        this.bucket = bucket;
    }

    /**
     * Gets the bucket currently in use
     *
     * @return The bucket currently in use [may be null]
     **/
    public String getBucket() {
        return this.bucket;
    }

    /**
     * Creates the current bucket.
     *
     * @return True if the operation succeeded, false if it failed.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
    public boolean createBucket() throws IOException {
        final HttpURLConnection bucketConn = getBucketURLConnection("PUT");

        bucketConn.connect();

        return checkResponse("createBucket", bucketConn);
    }

    /**
     * Deletes the current bucket.
     *
     * @return True if the operation succeeded, false if it failed.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
    public boolean deleteBucket() throws IOException {
        final HttpURLConnection bucketConn = getBucketURLConnection("DELETE");

        bucketConn.connect();

        return checkResponse("deleteBucket", bucketConn);
    }

    /**
     * Lists the buckets owned by the current user.
     *
     * @return A List of Strings of item ids in this bucket or null if there
     * was an error.
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
    public List<String> listBuckets() throws IOException {
        final HttpURLConnection rootConn = getRootURLConnection("GET");

        rootConn.connect();
        if(!checkResponse("listBuckets()", rootConn)) {
            return null;
        }

        final ObjectListParser olp = new ObjectListParser("name");

        final InputStream responseData = rootConn.getInputStream();
        try {
            m_parser.parse(responseData, olp);
        }
        catch(SAXException e) {
            throw new IllegalArgumentException("SAX parser failed", e);
        }
        finally {
            responseData.close();
        }

        return olp.getList();
    }

    /**
     * Stores item data into S3.  No metadata headers are added.
     *
     * @param id The ID to store the item to [may not be null]
     * @param data The binary data to store [may not be null]
     * @return True if the operation succeeded, false if it failed.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/       
    public boolean storeItem(final String id, final byte[] data) throws IOException {
      return storeItem(id, data, (Map<String, List<String>>) null);
    }

    private void addAclHeader(final Map<String, List<String>> headers, final String acl) {
      headers.put("x-amz-acl", Collections.singletonList(acl));
    }
    
    /**
     * Stores item data into S3.  No metadata headers are added.
     *
     * @param id The ID to store the item to [may not be null]
     * @param data The binary data to store [may not be null]
     * @param acl convenience param to specify an acl.  equivalent to including a header of "x-amz-acl" with this value.
     * Must be one of public-read, public-write, authenticated-read, or private (the default).    See:
     * http://docs.amazonwebservices.com/AmazonS3/latest/index.html?S3_ACLs.html for more info.
     * @return True if the operation succeeded, false if it failed.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/    
    public boolean storeItem(final String id, final byte[] data, final String acl) throws IOException {
      final Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
      addAclHeader(headers, acl);
      
      return storeItem(id, data, headers);
    }
    
    /**
     * Stores item data into S3.  No metadata headers are added.
     *
     * @param id The ID to store the item to [may not be null]
     * @param data The binary data to store [may not be null]
     * @param acl convenience param to specify an acl.  equivalent to including a header of "x-amz-acl" with this value.
     * Must be one of public-read, public-write, authenticated-read, or private (the default).  See:
     * http://docs.amazonwebservices.com/AmazonS3/latest/index.html?S3_ACLs.html for more info.
     * @param _headers other headers to send.  may be null or empty.  useful for setting content-type, acls, or other user
     * meta-data.  see http://docs.amazonwebservices.com/AmazonS3/latest/index.html?UsingMetadata.html for more info.
     * @return True if the operation succeeded, false if it failed.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
    public boolean storeItem(final String id, final byte[] data, final String acl, final Map<String, List<String>> _headers) throws IOException {
      final Map<String, List<String>> headers = new HashMap<String, List<String>>();
      
      if (_headers != null) {
        headers.putAll(_headers);
      }
      
      addAclHeader(headers, acl);
      
      return storeItem(id, data, headers);
    }

    /**
     * Stores item data into S3.  No metadata headers are added.
     *
     * @param id The ID to store the item to [may not be null]
     * @param data The binary data to store [may not be null]
     * @param headers other headers to send.  may be null or empty.  useful for setting content-type, acls, or other user
     * meta-data.  see http://docs.amazonwebservices.com/AmazonS3/latest/index.html?UsingMetadata.html for more info.
     * @return True if the operation succeeded, false if it failed.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
      public boolean storeItem(final String id, final byte[] data, final Map<String, List<String>> headers) throws IOException {      
        if(id == null) throw new IllegalArgumentException("id may not be null");
        if(data == null) throw new IllegalArgumentException("data may not be null");
        
        final HttpURLConnection itemConn = getItemURLConnection("PUT", id, data, headers);
        
        itemConn.setDoOutput(true);

        itemConn.connect();
        OutputStream dataout = itemConn.getOutputStream();
        dataout.write(data);
        dataout.close();

        return checkResponse("storeItem", itemConn);
    }
      
      public boolean copyItem(final String fromKey, final String toKey, final String acl) 
      throws IOException {
        if (fromKey == null || toKey == null) {
          throw new IllegalArgumentException("neither fromKey or toKey can be null");
        }
        
        final Map<String, List<String>> headers = new HashMap<String, List<String>>(3);
        
        if (acl != null) {
          addAclHeader(headers, acl);
        }
        
        final String fullDst = String.format("%s/%s", this.bucket, fromKey);
        
        headers.put("x-amz-copy-source", Collections.singletonList(fullDst));
        
        final HttpURLConnection itemConn = getItemURLConnection("PUT", toKey, null, headers);
        itemConn.connect();
        
        return checkResponse("copyItem", itemConn);
      }
      
      public Map<String, List<String>> getMeta(final String key) throws IOException {
        if (key == null) {
          throw new IllegalArgumentException("key must not be null");
        }
        
        final HttpURLConnection itemConn = getItemURLConnection("HEAD", key, null, null);
        
        if (! checkResponse("getMeta", itemConn)) return null;
        
        return itemConn.getHeaderFields();
      }

    /**
     * Gets an item from the current bucket.
     * 
     * @param id The item to get [may not be null]
     * @return The item data, or null if there was an error (e.g. the item
     * doesn't exist)
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
      public S3Object getItemWithHeaders(final String id) throws IOException {
        if(id == null) throw new IllegalArgumentException("id may not be null");

        final HttpURLConnection itemConn = getItemURLConnection("GET", id, null, null);

        itemConn.connect();

        if(!checkResponse("getItem", itemConn)) return null;

        final int responseBytes = itemConn.getContentLength();
        final byte[] retval = new byte[responseBytes];

        final DataInputStream datainput = new DataInputStream(itemConn.getInputStream());
        try {
            datainput.readFully(retval);
        }
        finally {
            datainput.close();
        }
        
        return new S3Object(retval, itemConn.getHeaderFields());
    }
      
      public byte[] getItem(final String id) throws IOException {
        final S3Object ret = getItemWithHeaders(id);
        return (ret == null ? null : ret.data);
      }

    /**
     * Deletes an item from the current bucket.
     *
     * @param id The item to delete [may not be null]
     * @return True on success, false on failure.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
    public boolean deleteItem(final String id) throws IOException {
        if(id == null) throw new IllegalArgumentException("id may not be null");

        final HttpURLConnection itemConn = getItemURLConnection("DELETE", id, null, null);

        itemConn.connect();

        return checkResponse("deleteItem", itemConn);
    }

    /**
     * Lists the contents of the current bucket from the beginning.  The number
     * of items returned may be limited by the server.
     *
     * @return A list of ids or null if there was an error.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
    public List<String> listItems() throws IOException {
        return listItems(null, null, 0);
    }

    /**
     * Lists those contents of the current bucket with IDs starting with
     * the given prefix.
     *
     * @param prefix The prefix to limit searches to.  If null, no restriction
     * is applied.
     * @return A list of ids or null if there was an error.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
    public List<String> listItems(final String prefix) throws IOException {
        return listItems(prefix, null, 0);
    }

    /**
     * Lists those contents of the current bucket with IDs starting with
     * the given prefix that occur strictly lexicographically after the
     * the given marker.
     *
     * @param prefix The prefix to limit searches to.  If null, no restriction
     * is applied.
     * @param marker The marker indicating where to start returning results.
     * If null, no restriction is applied.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
    public List<String> listItems(final String prefix, final String marker) throws IOException {
        return listItems(prefix, marker, 0);
    }

    /**
     * Lists those contents of the current bucket with IDs starting
     * with the given prefix that occur strictly lexicographically after
     * the given marker, limiting the results to the given maximum number.
     * 
     * @param prefix The prefix to limit searches to.  If null, no restriction
     * is applied.
     * @param marker The marker indicating where to start returning results.
     * If null, no restriction is applied.
     * @param max The maximum number of results to return.  If 0, no additional
     * restriction beyond the server default is applied.
     * @throws IllegalArgumentException If there is no bucket set
     * @throws IOException From underlying network problems or if S3 returned
     * an internal server error.
     **/
    public List<String> listItems(final String prefix, final String marker, final int max) throws IOException {
        if(max < 0) throw new IllegalArgumentException("max must be >= 0");

        final HttpURLConnection bucketConn = getBucketURLConnection("GET", prefix, marker, max);

        bucketConn.connect();

        if(!checkResponse("listItems("+prefix+","+marker+","+max+")", bucketConn)) {
            return null;
        }

        // the response comes as an XML document that we have to parse to
        // find the "key" fields we're interested in.  this helper SAX parser
        // just looks for these key tags and collects them into a list
        final ObjectListParser olp = new ObjectListParser("key");

        InputStream responseData = bucketConn.getInputStream();
        try {
            parser.parse(responseData, olp);
        }
        catch (SAXException e) {
            throw new IllegalArgumentException("SAX parser failed", e);
        }
        finally {
            responseData.close();
        }

        return olp.getList();
    }

    /**
     * Given an HttpURLConnection, this method determines whether the request
     * succeeded or not.  A request is a success if it returns a success
     * response code (generally "200 OK" or "204 No Content", depending on
     * the specific operation).  In most cases, this method returns true
     * on success, false on non-recoverable failure, or throws an IOException
     * in cases where a retry might be reasonably expected to succeed.  When
     * this method returns false, it will also print an error message to
     * the logger.
     **/
    private boolean checkResponse(final String operation, final HttpURLConnection conn) throws IOException {
        final int responseCode = conn.getResponseCode();
        //
        // When S3 is overloaded or having other problems of a transient
        // nature, it tends to return this error.  since this can probably
        // be fixed by retry, we throw it as an exception
        if(responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            throw new IOException(operation+": internal server error");
        }
        if(responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
            throw new IOException(operation+": service unavailable");
        }
        if(responseCode == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
            throw new IOException(operation+": gateway timeout");
        }
        
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
        	return false;
        }
        
        // 2xx response codes are ok, everything else is an error
        if(responseCode / 100 != 2) {
          ourLogger.log(Level.SEVERE, String.format("%s: response code %d", operation, responseCode));
            printError(conn);

            return false;
        }

        return true;
    }

    /**
     * If a connection to S3 returned an error response code, this method
     * will parse the error response XML and send the user-visible message
     * to the logger as SEVERE.
     **/
    private void printError(final HttpURLConnection conn) throws IOException {
        final InputStream errorData = conn.getErrorStream();
        // Some errors, like Service Unavailable, are produced by the
        // container and not S3, so they may not include an error stream.
        if(errorData == null) return;

        // Here we use our simple SAX parser to pull the message field
        // out of the error xml S3 returns to us
        final ObjectListParser olp = new ObjectListParser("message");

        try {
            parser.parse(errorData, olp);
        }
        catch(SAXException e) {
            throw new IllegalArgumentException("SAX parser failed", e);
        }
        finally {
            errorData.close();
        }

        for(String msg : olp.getList()) {
          ourLogger.log(Level.SEVERE, msg);
        }
    }

    /**
     * Creates a new HttpURLConnection that refers to the S3 root level for
     * the "list buckets" operation.
     **/
    private HttpURLConnection getRootURLConnection(final String method) throws IOException {
        final URL rootURL = new URL("http://" + host + "/");

        final HttpURLConnection rootConn = (HttpURLConnection)rootURL.openConnection();
        rootConn.setRequestMethod(method);
        rootConn.setReadTimeout((int)READ_TIMEOUT);
        addAuthorization(rootConn, method, null);

        return rootConn;
    }

    /**
     * Creates a new HttpURLConnection that refers to the current bucket for
     * operations such as bucket creation, bucket deletion, and listing
     * bucket contents.
     **/
    private HttpURLConnection getBucketURLConnection(final String method) throws IOException {
        return getBucketURLConnection(method, null, null, 0);
    }

    /**
     * Creates a new HttpURLConnection that refers to the current bucket for
     * specialized listing of bucket contents.
     **/
    private HttpURLConnection getBucketURLConnection(final String method, final String prefix, final String marker, final int max) throws IOException {
        if(bucket == null) {
            throw new IllegalArgumentException("bucket is not set");
        }

        String url = "http://" + host + "/" + bucket;
        final StringBuilder query = new StringBuilder("");

        // Assemble the query string as individual clauses prefixed with
        // "&"'s.  After it's constructed, the first "&" will be changed to
        // the "?" that denotes the start of a query string.
        if(prefix != null) {
            query.append("&prefix=").append(URLEncoder.encode(prefix, "UTF-8"));
        }
        if(marker != null) {
            query.append("&marker=").append(URLEncoder.encode(marker, "UTF-8"));
        }
        if(max != 0) {
            query.append("&max-keys=").append(max);
        }

        if(query.length() > 0) {
            query.setCharAt(0, '?');

            url += query;
        }

        final URL bucketURL = new URL(url);

        final HttpURLConnection bucketConn = (HttpURLConnection)bucketURL.openConnection();
        bucketConn.setRequestMethod(method);
        bucketConn.setReadTimeout((int)READ_TIMEOUT);

        addAuthorization(bucketConn, method, null);

        return bucketConn;
    }

    /**
     * Gets an HttpURLConnection referring to a specific item for storing
     * and retrieving of data.
     **/
    private HttpURLConnection getItemURLConnection(final String method, final String id, final byte[] data, final Map<String, List<String>> headers) throws IOException {
        if (bucket == null) {
            throw new IllegalArgumentException("bucket is not set");
        }
        
        final URL itemURL = new URL("http://" + host + "/" + bucket + "/" + id);

        final HttpURLConnection urlConn = (HttpURLConnection)itemURL.openConnection();
        urlConn.setRequestMethod(method);
        urlConn.setReadTimeout((int)READ_TIMEOUT);
        
        if (headers != null) {
          for (final Map.Entry<String, List<String>> me : headers.entrySet()) {
            for (final String v : me.getValue()) {
              urlConn.setRequestProperty(me.getKey(), v);
            }
          }
        }

        addAuthorization(urlConn, method, data);

        return urlConn;
    }
    
    public String getContentType(final HttpURLConnection conn) {
      for (final Map.Entry<String, List<String>> me : conn.getRequestProperties().entrySet()) {
        if ("Content-Type".equalsIgnoreCase(me.getKey())) {
          return me.getValue().iterator().next();
        }
      }
      return "";
    }

    /**
     * Given an HttpURLConnection, this method adds the appropriate
     * authentication data to it to connect to S3.  If connection data
     * is provided, an MD5 digest is included for additional security, 
     * but the data itself is not written to the connection.
     **/
    private void addAuthorization(final HttpURLConnection conn, final String method, final byte[] data) {
        final String contentType = getContentType(conn);
        final String contentMD5 = data != null ? encodeBase64(md5(data)) : "";
        final String date = newDateHeaderValue();
        final String headers = toXAmzHeaders(conn);
        final String path = conn.getURL().getPath();

        final String reducedRequest = toReducedRequest(method, contentMD5, contentType, date, headers, path);
        final String auth = encodeBase64(newMAC(reducedRequest.getBytes(), password, SIGNATURE_ALGORITHM));

        conn.addRequestProperty("Date", date);
        conn.setRequestProperty("Authorization", "AWS "+username+":"+auth);
        if (data != null) conn.addRequestProperty("Content-MD5", contentMD5);
    }

    // S3 authentication works as a SHA1 hash of the following information in this precise order
    private static String toReducedRequest(final String method, final String contentMD5, final String contentType
            , final String date, final String headers, final String path) {
        final StringBuilder buf = new StringBuilder();
        buf.append(method).append("\n");
        buf.append(contentMD5).append("\n");
        buf.append(contentType).append("\n");
        buf.append(date).append("\n");
        if (headers.length() > 0) buf.append(headers);
        buf.append(path);
        return buf.toString();
    }

    private static byte[] newMAC(final byte[] data, final String password, final String signatureAlgorithm) {
        try {
            final Mac mac = Mac.getInstance(signatureAlgorithm);
            mac.init(new SecretKeySpec(password.getBytes(), signatureAlgorithm));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException("Missing MAC algorithm " + signatureAlgorithm, e);
        }
    }

}

