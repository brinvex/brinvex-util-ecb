package com.brinvex.util.ecb.impl;

import com.brinvex.util.ecb.api.HttpClientFacade;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.Map;

public class DefaultHttpClientFacadeImpl implements HttpClientFacade {

    private static final class Lazy {
        private static final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
                .followRedirects(java.net.http.HttpClient.Redirect.ALWAYS)
                .build();
    }

    @Override
    public Response doGet(URI uri, Map<String, Object> headers, Charset respCharset) throws IOException {
        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder(uri);
        if (headers != null) {
            for (Map.Entry<String, Object> e : headers.entrySet()) {
                reqBuilder.header(e.getKey(), String.valueOf(e.getValue()));
            }
        }
        HttpResponse<String> resp;
        try {
            resp = Lazy.httpClient.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString(respCharset));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Thread was interrupted while fetching %s".formatted(uri), e);
        }
        return new Response(resp.statusCode(), resp.body());
    }
}
