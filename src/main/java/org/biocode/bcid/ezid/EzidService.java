/**
 * This work was created by the National Center for Ecological Analysis and Synthesis
 * at the University of California Santa Barbara (UCSB).
 *
 *   Copyright 2011 Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.biocode.bcid.ezid;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

/**
 * EzidService provides access to the EZID identifier service maintained by the
 * California Digital Library (<a href="http://n2t.net/ezid/doc/apidoc.html">EZID</a>).
 * The service includes methods for creating identifiers using several different
 * standards such as DOI, ARK, and others.  To use the service, you must have an
 * account with the EZID service, which is first used to login to the service.  Once
 * successfully authenticated, calls can be made to create an identifier, to mint an
 * identifier (same as create, but the EZID service creates a random identifier), to
 * delete an identifier, or to get or set the metadata associated with an identifier.
 * <p/>
 * A typical interaction might proceed as follows:
 * <pre>
 * {@code
 * try {
 *     EzidService ezid = new EzidService();
 *     String newId = "";
 *     ezid.login("username", "password");
 *     newId = ezid.mintIdentifier("doi:10.5072/FK2", null);
 *     HashMap<String, String> metadata = new HashMap<String, String>();
 *     metadata.put("_target", "http://example.com/some/target/resource");
 *     newId = ezid.createIdentifier("doi:10.5072/FK2/TEST/10101", metadata);
 *     metadata = ezid.getMetadata(newId);
 *     HashMap<String, String> moreMetadata = new HashMap<String, String>();
 *     moreMetadata.put("datacite.title", "This is a test identifier");
 *     ezid.setMetadata(newId, moreMetadata);
 * } catch (EzidException e) {
 *     // Handle the error
 * }
 * }
 * </pre>
 *
 * @author Matthew Jones, NCEAS, UC Santa Barbara
 */
public class EzidService implements IEzidService {

    private static final String LOGIN_SERVICE = "https://ezid.cdlib.org/login";
    private static final String LOGOUT_SERVICE = "https://ezid.cdlib.org/logout";
    private static final String ID_SERVICE = "https://ezid.cdlib.org/id";
    private static final String MINT_SERVICE = "https://ezid.cdlib.org/shoulder";

    private static final int GET = 1;
    private static final int PUT = 2;
    private static final int POST = 3;
    private static final int DELETE = 4;
    private static final int CONNECTIONS_PER_ROUTE = 8;

    private DefaultHttpClient httpClient = null;
    private BasicCookieStore cookieStore = null;

    protected static Logger log = LoggerFactory.getLogger(EzidService.class);

    /**
     * Construct an EzidService to be used to access EZID.
     */
    public EzidService() {
        httpClient = createThreadSafeClient();
        cookieStore = new BasicCookieStore();
        httpClient.setCookieStore(cookieStore);
    }

    /**
     * Log into the EZID service using account credentials provided by EZID. The cookie
     * returned by EZID is cached in a local CookieStore for the duration of the EzidService,
     * and so subsequent calls using this instance of the service will function as
     * fully authenticated. An exception is thrown if authentication fails.
     *
     * @param username to identify the user account from EZID
     * @param password the secret password for this account
     *
     * @throws EzidException if authentication fails for any reason
     */
    @Override
    public void login(String username, String password) throws EzidException {
        String msg;
        try {
            URI serviceUri = new URI(LOGIN_SERVICE);
            HttpHost targetHost = new HttpHost(serviceUri.getHost(), serviceUri.getPort(), serviceUri.getScheme());
            httpClient.getCredentialsProvider().setCredentials(
                    new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                    new UsernamePasswordCredentials(username, password));
            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);
            BasicHttpContext localcontext = new BasicHttpContext();
            localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

            // DEBUGGING ONLY, CAN COMMENT OUT WHEN FULLY WORKING....
            //System.out.println("authCache: " + authCache.toString());

            ResponseHandler<byte[]> handler = new ResponseHandler<byte[]>() {
                public byte[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        return EntityUtils.toByteArray(entity);
                    } else {
                        return null;
                    }
                }
            };
            byte[] body = null;

            HttpGet httpget = new HttpGet(LOGIN_SERVICE);
            body = httpClient.execute(httpget, handler, localcontext);
            String message = new String(body);
            msg = parseIdentifierResponse(message);

            // DEBUGGING ONLY, CAN COMMENT OUT WHEN FULLY WORKING....
            /*
                        org.apache.http.client.CookieStore cookieStore = httpClient.getCookieStore();
            System.out.println("\n\nCookies : ");
            List<Cookie> cookies = cookieStore.getCookies();
            for (int i = 0; i < cookies.size(); i++) {
                System.out.println("Cookie: " + cookies.get(i));
            } */


        } catch (URISyntaxException e) {
            //System.out.println("URI SyntaxError Exception in LOGIN");
            throw new EzidException("Bad syntax for uri: " + LOGIN_SERVICE, e);
        } catch (ClientProtocolException e) {
            //System.out.println("ClientProtocol Exception in LOGIN");
            throw new EzidException(e);
        } catch (IOException e) {
            //System.out.println("IO Exception in LOGIN");
            throw new EzidException(e);
        }
        //System.out.println("Seems to be a successful LOGIN, msg= " + msg.toString());
    }

    /**
     * Log out of the EZID service, invalidating the current session.
     */
    @Override
    public void logout() throws EzidException {
        String ezidEndpoint = LOGOUT_SERVICE;
        byte[] response = sendRequest(GET, ezidEndpoint);
        String message = new String(response);
        String msg = parseIdentifierResponse(message);
    }

    /**
     * Request that an identifier be created in the EZID system.  The identifier
     * must be one of the identifier types supported by EZID, such as ARK, DOI,
     * or URN, and for each type accounts may only create identifiers with prefixes
     * that are authorized for their EZID account.  For example, all identifiers
     * created by an account might need to start with the string "doi:10.5072/FK2", so
     * a request to create "doi:10.5072/FK2/MYID1" might succeed whereas a request
     * to create "doi:10.5072/MA/MYID1" might fail, depending on the account. Metadata
     * elements can be passed as a HashMap and will be added when the identifier is created.
     * To omit setting metadata, pass 'null' as the metadata parameter. To have EZID
     * generate a unique ID itself, @see {@link edu.ucsb.nceas.ezid.EZIDService#mintIdentifier(String, HashMap)}
     *
     * @param identifier to be created
     * @param metadata   a HashMap containing name/value pairs to be associated with the identifier
     *
     * @return String identifier that was created
     *
     * @throws EzidException if an error occurs while creating the identifier
     */
    @Override
    public String createIdentifier(String identifier, HashMap<String, String> metadata) throws EzidException {
        String newId = null;
        String ezidEndpoint = ID_SERVICE + "/" + identifier;

        String anvl = serializeAsANVL(metadata);

        //System.out.println("EZID Create identifier: " + identifier + ":"+metadata.toString());

        byte[] response = sendRequest(PUT, ezidEndpoint, anvl);
        String responseMsg = new String(response);

        //System.out.println("ezidEndpoint = " + ezidEndpoint);
        //System.out.println("responseMsg = "  + responseMsg);
        /*System.out.println("DO WE HAVE the SAME cookies?");
          // DEBUGGING ONLY, CAN COMMENT OUT WHEN FULLY WORKING....
            org.apache.http.client.CookieStore cookieStore = httpClient.getCookieStore();
            System.out.println("\n\nCookies : ");
            List<Cookie> cookies = cookieStore.getCookies();
            for (int i = 0; i < cookies.size(); i++) {
                System.out.println("Cookie: " + cookies.get(i));
            }
        */

        log.debug(responseMsg);

        return parseIdentifierResponse(responseMsg);
    }

    /**
     * Create a new, unique, opaque identifier by requesting EZID to generate the
     * identifier itself within the "shoulder" identifier that is provided.  Each EZID account
     * is authorized to mint identifiers that start with certain prefixes, called 'shoulders'
     * by EZID.  The identifiers created are guaranteed unique within the EZID service. Metadata
     * elements can be passed as a HashMap and will be added when the identifier is created.
     * To omit setting metadata, pass 'null' as the metadata parameter.
     *
     * @param shoulder to be used to identifier the identifier
     * @param metadata a HashMap containing name/value pairs to be associated with the identifier
     *
     * @return String identifier that was created
     *
     * @throws EzidException if an error occurs while minting the identifier
     */
    @Override
    public String mintIdentifier(String shoulder, HashMap<String, String> metadata) throws EzidException {
        String ezidEndpoint = MINT_SERVICE + "/" + shoulder;

        String anvl = serializeAsANVL(metadata);

        byte[] response = sendRequest(POST, ezidEndpoint, anvl);
        String responseMsg = new String(response);
        log.debug(responseMsg);
        return parseIdentifierResponse(responseMsg);
    }

    /**
     * Return a HashMap containing the EZID metadata associated with an identifier as
     * a set of name/value pairs.  Each key and associated value in the HashMap
     * represents a single metadata property.
     *
     * @param identifier for which metadata should be returned
     *
     * @return HashMap of name/value pairs of metadata properties
     *
     * @throws EzidException if EZID produces an error during the service call
     */
    @Override
    public HashMap<String, String> getMetadata(String identifier) throws EzidException {
        String ezidEndpoint = ID_SERVICE + "/" + identifier;
        byte[] response = sendRequest(GET, ezidEndpoint);
        String anvl = new String(response);

        HashMap<String, String> metadata = new HashMap<String, String>();
        for (String l : anvl.split("[\\r\\n]+")) {
            String[] kv = l.split(":", 2);
            metadata.put(unescape(kv[0]).trim(), unescape(kv[1]).trim());
        }
        return metadata;
    }

    /**
     * Set a series of metadata properties for the given identifier.  Metadata are
     * passed in as a HashMap representing name/value pairs.  EZID defines a set of
     * keys to be used to associate metadata values with certain namespaces, such
     * as the 'datacite', 'dc', and other namespaces.  For example, the 'datacite.title'
     * property contains the title of the resource that is identified.
     *
     * @param identifier of the resource for which metadata is being set
     * @param metadata   HashMap containing name/value metadata pairs
     *
     * @throws EzidException if the EZID service returns an error on setting metadata
     */
    @Override
    public void setMetadata(String identifier, HashMap<String, String> metadata) throws EzidException {
        String ezidEndpoint = ID_SERVICE + "/" + identifier;

        String anvl = serializeAsANVL(metadata);

        byte[] response = sendRequest(POST, ezidEndpoint, anvl);
        String responseMsg = new String(response);
        log.debug(responseMsg);
        String modifiedId = parseIdentifierResponse(responseMsg);
    }

    /**
     * Delete an identifier from EZID.  This should be an unusual operation, and is
     * only possible for identifiers that have been reserved but not yet made public (such
     * as an internal, temporary identifier).  Identifiers for which the internal "_status"
     * metadata field is set to "public" can not be deleted.
     *
     * @param identifier to be deleted
     *
     * @throws EzidException if the delete operation fails with an error from EZID
     */
    @Override
    public void deleteIdentifier(String identifier) throws EzidException {
        String ezidEndpoint = ID_SERVICE + "/" + identifier;
        byte[] response = sendRequest(DELETE, ezidEndpoint);
        String responseMsg = new String(response);
        String deletedId = parseIdentifierResponse(responseMsg);
    }

    /**
     * Generate an HTTP Client for communicating with web services that is
     * thread safe and can be used in the context of a multi-threaded application.
     *
     * @return DefaultHttpClient
     */
    private static DefaultHttpClient createThreadSafeClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(mgr.getSchemeRegistry());
        connManager.setDefaultMaxPerRoute(CONNECTIONS_PER_ROUTE);
        client = new DefaultHttpClient(connManager, params);
        return client;
    }

    /**
     * Send an HTTP request to the EZID service without a request body.
     *
     * @param requestType the type of the service as an integer
     * @param uri         endpoint to be accessed in the request
     *
     * @return byte[] containing the response body
     */
    private byte[] sendRequest(int requestType, String uri) throws EzidException {
        return sendRequest(requestType, uri, null);
    }

    /**
     * Send an HTTP request to the EZID service with a request body (for POST and PUT requests).
     *
     * @param requestType the type of the service as an integer
     * @param uri         endpoint to be accessed in the request
     * @param requestBody the String body to be encoded into the body of the request
     *
     * @return byte[] containing the response body
     */
    private byte[] sendRequest(int requestType, String uri, String requestBody) throws EzidException {
        HttpUriRequest request = null;
        log.debug("Trying uri: " + uri);
        System.out.println("uri = " + uri);
        switch (requestType) {

            case GET:
                request = new HttpGet(uri);
                break;
            case PUT:
                request = new HttpPut(uri);
                if (requestBody != null && requestBody.length() > 0) {
                    try {
                        StringEntity myEntity = new StringEntity(requestBody, "UTF-8");
                        ((HttpPut) request).setEntity(myEntity);
                    } catch (UnsupportedCharsetException | UnsupportedEncodingException e) {
                        throw new EzidException(e);
                    }
                }
                break;
            case POST:
                request = new HttpPost(uri);

                if (requestBody != null && requestBody.length() > 0) {
                    try {
                        System.out.println("requestBody = " + requestBody);
                        StringEntity myEntity = new StringEntity(requestBody, "UTF-8");
                        ((HttpPost) request).setEntity(myEntity);
                    } catch (UnsupportedCharsetException | UnsupportedEncodingException e) {
                        throw new EzidException(e);
                    }
                }
                break;
            case DELETE:
                request = new HttpDelete(uri);
                break;
            default:
                throw new EzidException("Unrecognized HTTP method requested.");
        }
        request.addHeader("Accept", "text/plain");

        /* HACK -- re-authorize on the fly as this was getting dropped on SI server*/
        /*String auth = this.username + ":" + this.password;
        String encodedAuth = org.apache.commons.codec.binary.Base64.encodeBase64String(auth.getBytes());
        request.addHeader("Authorization", "Basic " + encodedAuth); */

        ResponseHandler<byte[]> handler = new ResponseHandler<byte[]>() {
            public byte[] handleResponse(
                    HttpResponse response) throws ClientProtocolException, IOException {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toByteArray(entity);
                } else {
                    return null;
                }
            }
        };
        byte[] body = null;

        try {
            body = httpClient.execute(request, handler);
        } catch (ClientProtocolException e) {
            throw new EzidException(e);
        } catch (IOException e) {
            throw new EzidException(e);
        }

        return body;
    }

    /**
     * Parse the response from EZID and extract out the identifier that is returned
     * as part of the 'success' message.
     *
     * @param responseMsg the response from EZID
     *
     * @return the identifier from the message
     *
     * @throws EzidException if the response contains an error message
     */
    private String parseIdentifierResponse(String responseMsg) throws EzidException {
        String newId;
        String[] responseArray = responseMsg.split(":", 2);
        String resultCode = unescape(responseArray[0]).trim();
        if (resultCode.equals("success")) {
            String idList[] = (unescape(responseArray[1]).trim()).split("\\|");
            newId = idList[0].trim();
            return newId;
        } else {
            String msg = unescape(responseArray[1]).trim();
            throw new EzidException(msg);
        }
    }

    /**
     * Serialize a HashMap of metadata name/value pairs as an ANVL String value. If the
     * HashMap is null, or if it has no entries, then return a null string.
     *
     * @param metadata the Map of metadata name/value pairs
     *
     * @return an ANVL serialize String
     */
    private String serializeAsANVL(HashMap<String, String> metadata) {
        StringBuffer buffer = new StringBuffer();
        if (metadata != null && metadata.size() > 0) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                buffer.append(escape(entry.getKey()) + ": " + escape(entry.getValue()) + "\n");
            }
        }
        String anvl = null;
        if (buffer != null) {
            anvl = buffer.toString();
        }
        return anvl;
    }

    /**
     * Escape a string to produce it's ANVL escaped equivalent.
     *
     * @param str the string to be escaped
     *
     * @return the escaped String
     */
    private String escape(String str) {
        if (str == null) {
            return "";
        } else {
            return str.replace("%", "%25").replace("\n", "%0A").replace("\r", "%0D").replace(":", "%3A");
        }
    }

    /**
     * Unescape a percent encoded response from the server.
     *
     * @param str the string to be unescaped
     *
     * @return the unescaped String value
     */
    private String unescape(String str) {
        StringBuffer buffer = new StringBuffer();
        int i;
        while ((i = str.indexOf("%")) >= 0) {
            buffer.append(str.substring(0, i));
            buffer.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
            str = str.substring(i + 3);
        }
        buffer.append(str);
        return buffer.toString();
    }
}