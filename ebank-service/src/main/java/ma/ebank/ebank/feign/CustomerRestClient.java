package ma.ebank.ebank.feign;

import ma.ebank.ebank.dto.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// "customer-service" = le nom déclaré par spring.application.name côté Customer Service
// Consul se charge de résoudre l'adresse réelle (load balancing côté client)
@FeignClient(name = "customer-service")
public interface CustomerRestClient {

    @GetMapping("/customers/{id}")
    CustomerDTO getCustomerById(@PathVariable("id") Long id);
}
