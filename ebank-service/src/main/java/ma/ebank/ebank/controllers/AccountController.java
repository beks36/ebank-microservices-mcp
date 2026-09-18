package ma.ebank.ebank.controllers;

import lombok.AllArgsConstructor;
import ma.ebank.ebank.dto.AccountWithCustomerDTO;
import ma.ebank.ebank.dto.CustomerDTO;
import ma.ebank.ebank.entities.Account;
import ma.ebank.ebank.entities.Transaction;
import ma.ebank.ebank.entities.TransactionType;
import ma.ebank.ebank.feign.CustomerRestClient;
import ma.ebank.ebank.repositories.AccountRepository;
import ma.ebank.ebank.repositories.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@CrossOrigin("*")
public class AccountController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRestClient customerRestClient;

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id " + id));
    }

    // Endpoint clé pour la démo : combine Account (local) + Customer (via Feign/Consul)
    @GetMapping("/{id}/details")
    public AccountWithCustomerDTO getAccountWithCustomer(@PathVariable Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id " + id));
        CustomerDTO customer = customerRestClient.getCustomerById(account.getCustomerId());
        return AccountWithCustomerDTO.builder()
                .id(account.getId())
                .rib(account.getRib())
                .balance(account.getBalance())
                .customer(customer)
                .build();
    }

    @GetMapping("/customer/{customerId}")
    public List<Account> getAccountsByCustomer(@PathVariable Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountRepository.save(account);
    }

    @GetMapping("/{id}/transactions")
    public List<Transaction> getTransactions(@PathVariable Long id) {
        return transactionRepository.findByAccountId(id);
    }

    @PostMapping("/{id}/depot")
    public Transaction deposit(@PathVariable Long id, @RequestParam double amount) {
        return performTransaction(id, amount, TransactionType.DEPOT);
    }

    @PostMapping("/{id}/retrait")
    public Transaction withdraw(@PathVariable Long id, @RequestParam double amount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id " + id));
        if (account.getBalance() < amount) {
            throw new RuntimeException("Solde insuffisant");
        }
        return performTransaction(id, amount, TransactionType.RETRAIT);
    }

    private Transaction performTransaction(Long accountId, double amount, TransactionType type) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id " + accountId));

        double newBalance = type == TransactionType.DEPOT
                ? account.getBalance() + amount
                : account.getBalance() - amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .date(new Date())
                .type(type)
                .account(account)
                .build();
        return transactionRepository.save(transaction);
    }
}
