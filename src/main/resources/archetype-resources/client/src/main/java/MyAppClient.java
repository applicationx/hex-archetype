package ${package}.client.http;

import ${package}.client.api.RegisterCustomerRequest;
import ${package}.client.api.RegisterCustomerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

public final class MyAppClient {

    private final URI baseUri;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MyAppClient(URI baseUri) {
        this(Objects.requireNonNull(baseUri, "baseUri must not be null"),
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build(),
                new ObjectMapper());
    }

    public MyAppClient(URI baseUri, HttpClient httpClient, ObjectMapper objectMapper) {
        this.baseUri = Objects.requireNonNull(baseUri, "baseUri must not be null");
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    public RegisterCustomerResponse registerCustomer(RegisterCustomerRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);

            var httpRequest = HttpRequest.newBuilder()
                    .uri(baseUri.resolve("/api/v1/customers"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), RegisterCustomerResponse.class);
            }
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Client call interrupted", e);
        } catch (IOException e) {
            throw new RuntimeException("Client call failed", e);
        }
    }
}
