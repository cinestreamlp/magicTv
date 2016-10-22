package org.magictvapi.cookies;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by thomas on 02/09/16.
 */
public class CookieManager extends java.net.CookieManager {

    /**
     * Searches and gets all cookies in the cache by the specified uri in the
     * request header.
     *
     * @param uri
     *            the specified uri to search for
     * @param requestHeaders
     *            a list of request headers
     * @return a map that record all such cookies, the map is unchangeable
     * @throws IOException
     *             if some error of I/O operation occurs
     */
    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {

        for (HttpCookie cookie : this.getCookieStore().get(uri)) {
            cookie.setVersion(0);
        }

        Map<String, List<String>> retour = super.get(uri, requestHeaders);
        for (HttpCookie cookie : this.getCookieStore().get(uri)) {
            cookie.setVersion(1);
        }

        return retour;
    }
}
