package io.pivotal.cnde.actuator;

import com.jayway.jsonpath.DocumentContext;
import io.pivotal.cnde.actuator.support.HttpClient;
import io.pivotal.cnde.actuator.support.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "info.app.name=Cognizant Tracker Test",
                "info.app.description=This is for testing.",
                "info.app.version=0.0.1",
                "spring.security.user.name=user",
                "spring.security.user.password=password",
                "spring.security.user.roles=ACTUATOR"
        })
public class SecureActuatorApplicationTests {

    private final HttpClient httpClient = new HttpClient();

    @LocalServerPort
    private int port;

    private String actuatorUrl(String endpoint) {
        return "http://localhost:" + port + "/actuator/" + endpoint;
    }

    @Test
    public void health() {
        HttpResponse response = httpClient.get(actuatorUrl("health"));
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("\"UP\"");
    }

    @Test
    public void health_whenAuthenticated() {
        HttpResponse response = httpClient.get(actuatorUrl("health"),
                "user", "password");
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("\"details\"");
    }

    @Test
    public void info() {
        HttpResponse response = httpClient.get(actuatorUrl("info"));

        assertThat(response.getStatus()).isEqualTo(200);
        DocumentContext createJson = parse(response.getBody());
        assertThat(createJson.read("$.app.name", String.class)).isEqualTo("Cognizant Tracker Test");
        assertThat(createJson.read("$.app.description", String.class))
                .isEqualTo("This is for testing.");
        assertThat(createJson.read("$.app.version", String.class)).isEqualTo("0.0.1");
    }
}