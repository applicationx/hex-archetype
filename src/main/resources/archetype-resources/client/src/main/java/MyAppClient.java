#set($d = '$')
package ${package}.client.http;

import ${package}.client.api.RegisterCustomerRequest;
import ${package}.client.api.RegisterCustomerResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "${rootArtifactId}",
        url = "${d}{${rootArtifactId}.client.url:http://localhost:8080}")
public interface MyAppClient {

    @PostMapping("/api/v1/customers")
    RegisterCustomerResponse registerCustomer(@RequestBody RegisterCustomerRequest request);
}
