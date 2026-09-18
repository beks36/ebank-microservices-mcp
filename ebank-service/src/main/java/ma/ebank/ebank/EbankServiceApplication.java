package ma.ebank.ebank;

import ma.ebank.ebank.entities.Account;
import ma.ebank.ebank.repositories.AccountRepository;
import ma.ebank.ebank.tools.AccountTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class EbankServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankServiceApplication.class, args);
    }

    // Expose les méthodes @Tool de AccountTools comme des outils MCP
    @Bean
    public ToolCallbackProvider accountToolCallbackProvider(AccountTools accountTools) {
        return MethodToolCallbackProvider.builder().toolObjects(accountTools).build();
    }

    // Jeu de données de test aligné sur les ids créés dans customer-service (1, 2, 3)
    @Bean
    CommandLineRunner initData(AccountRepository accountRepository) {
        return args -> {
            accountRepository.save(Account.builder().rib("MR001").balance(15000.0).customerId(1L).build());
            accountRepository.save(Account.builder().rib("MR002").balance(8500.0).customerId(2L).build());
            accountRepository.save(Account.builder().rib("MR003").balance(23000.0).customerId(3L).build());
        };
    }
}
