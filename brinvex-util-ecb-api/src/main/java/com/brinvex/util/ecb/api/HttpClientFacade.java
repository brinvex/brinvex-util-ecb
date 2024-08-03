package com.brinvex.util.ecb.api;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

@FunctionalInterface
public interface HttpClientFacade {

    record Response(int status, String body) {
    }

    Response doGet(URI uri, Map<String, Object> headers, Charset respCharset) throws IOException;

}
