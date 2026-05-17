package ${package}.adapters.inbound.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = ${package}.adapters.inbound.rest.InboundRestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CustomerRegistrationIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
    }

    @Test
    void openApiDocumentDescribesCustomerRegistrationEndpoint() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/v3/api-docs"))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isBetween(200, 299);
        assertThat(response.body())
                .contains("/api/v1/customers")
                .contains("/api/v1/customers/me")
                .contains("Register a customer")
                .contains("RegisterCustomerRequest")
                .contains("RegisterCustomerResponse")
                .contains("GatewayUserResponse")
                .contains("\"zitadel\"")
                .contains("authorizationCode");
    }

    @Test
    void gatewayUserEndpointRequiresAuthentication() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/customers/me"))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }
}
