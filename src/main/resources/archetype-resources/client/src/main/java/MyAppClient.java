#set($d = '$')
package ${package}.client.http;

import ${package}.client.api.RegisterCustomerRequest;
import ${package}.client.api.RegisterCustomerResponse;
import ${package}.client.api.CustomerResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "${rootArtifactId}",
        url = "${d}{${rootArtifactId}.client.url:http://localhost:8080}")
public interface MyAppClient {

    @PostMapping("/api/v1/customers")
    RegisterCustomerResponse registerCustomer(@RequestBody RegisterCustomerRequest request);

    @GetMapping("/api/v1/customers/{customerId}")
    CustomerResponse getCustomer(@PathVariable UUID customerId);
}
