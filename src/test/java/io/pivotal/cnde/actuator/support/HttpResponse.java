package io.pivotal.cnde.actuator.support;

import java.util.Objects;

public class HttpResponse {

    private final int status;
    private final String body;

    public HttpResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpResponse response = (HttpResponse) o;
        return status == response.status
                && Objects.equals(body, response.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, body);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "status=" + status +
                ", body='" + body + '\'' +
                '}';
    }
}
