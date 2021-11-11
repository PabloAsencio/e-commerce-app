package com.example.demo;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

public class TestUtils {

    // See https://kreuzwerker.de/en/post/integration-tests-with-spring-boot and
    // https://www.baeldung.com/spring-rest-template-builder
    public static void authenticate(TestRestTemplate restTemplate, int port) {
        String credentials = "{ \"username\": \"default\", \"password\": \"password\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(credentials, headers);
        final ResponseEntity<Object> response = restTemplate.exchange("http://localhost:" + port + "/login", HttpMethod.POST, entity, Object.class);
        final List<String> authorizations = response.getHeaders().get(HttpHeaders.AUTHORIZATION);
        String token = authorizations.get(0);
        restTemplate.getRestTemplate().getInterceptors().add((httpRequest, body, execution) -> {
            httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return execution.execute(httpRequest, body);
        });
    }
}
