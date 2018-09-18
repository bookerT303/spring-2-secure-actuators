package io.pivotal.cnde.actuator.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class HttpClient {

    private static final MediaType JSON = MediaType.parse("application/json");
    private static final Charset UTF_8 = Charset.forName("UTF-8");


    private final OkHttpClient okHttp = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HttpResponse get(String url) {
        return fetch(new Request.Builder().url(url));
    }

    public HttpResponse get(String url, String accepts) {
        return fetch(new Request.Builder().url(url).addHeader(ACCEPT, accepts));
    }

    public HttpResponse get(String url, String username, String password) {
        return fetch(new Request.Builder().url(url).addHeader(AUTHORIZATION,
                "Basic " + buildToken(username, password)));
    }

    private String buildToken(String username, String password) {
        return Base64.getEncoder().encodeToString(
                String.format("%s:%s", username, password).getBytes(UTF_8));
    }

    private HttpResponse fetch(Request.Builder requestBuilder) {
        try {
            Request request = requestBuilder.build();

            Response response = okHttp.newCall(request).execute();
            ResponseBody body = response.body();

            if (body == null) {
                return new HttpResponse(response.code(), "");
            }

            return new HttpResponse(response.code(), body.string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}