package ma.ebank.customer;

import ma.ebank.customer.entities.Customer;
import ma.ebank.customer.repositories.CustomerRepository;
import ma.ebank.customer.tools.CustomerTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    // Expose les méthodes @Tool de CustomerTools comme des outils MCP,
    // découverts automatiquement par le starter spring-ai-starter-mcp-server-webmvc
    @Bean
    public ToolCallbackProvider customerToolCallbackProvider(CustomerTools customerTools) {
        return MethodToolCallbackProvider.builder().toolObjects(customerTools).build();
    }

    // Jeu de données de test, chargé au démarrage pour faciliter la démo
    @Bean
    CommandLineRunner initData(CustomerRepository customerRepository) {
        return args -> {
            customerRepository.save(Customer.builder().name("Ahmed Salem").email("ahmed.salem@ebank.mr").phone("0022246001122").build());
            customerRepository.save(Customer.builder().name("Fatimetou Mint Ely").email("fatimetou.ely@ebank.mr").phone("0022246003344").build());
            customerRepository.save(Customer.builder().name("Mohamed Vall").email("mohamed.vall@ebank.mr").phone("0022246005566").build());
        };
    }
}
