package ma.ebank.customer.tools;

import lombok.AllArgsConstructor;
import ma.ebank.customer.entities.Customer;
import ma.ebank.customer.repositories.CustomerRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

// Cette classe expose des méthodes comme "tools" MCP : l'agent IA
// (dans ebank-chatbot-service) pourra les appeler en langage naturel.
@Service
@AllArgsConstructor
public class CustomerTools {

    private final CustomerRepository customerRepository;

    @Tool(description = "Récupère les informations d'un client (nom, email, téléphone) à partir de son identifiant")
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'id " + customerId));
    }

    @Tool(description = "Liste tous les clients enregistrés dans la banque")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
