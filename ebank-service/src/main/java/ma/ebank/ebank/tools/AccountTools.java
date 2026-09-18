package ma.ebank.ebank.tools;

import lombok.AllArgsConstructor;
import ma.ebank.ebank.entities.Account;
import ma.ebank.ebank.entities.Transaction;
import ma.ebank.ebank.entities.TransactionType;
import ma.ebank.ebank.repositories.AccountRepository;
import ma.ebank.ebank.repositories.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

// Outils exposés en MCP : l'agent IA (ebank-chatbot-service) peut les
// appeler pour répondre à des questions ou effectuer des opérations bancaires.
@Service
@AllArgsConstructor
public class AccountTools {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Tool(description = "Récupère les informations d'un compte bancaire (solde, RIB) à partir de son identifiant")
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Compte introuvable avec l'id " + accountId));
    }

    @Tool(description = "Liste tous les comptes bancaires appartenant à un client, à partir de l'identifiant du client")
    public List<Account> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    @Tool(description = "Récupère l'historique des transactions (dépôts et retraits) d'un compte")
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Tool(description = "Effectue un dépôt d'argent sur un compte bancaire et retourne la transaction créée")
    public Transaction deposit(Long accountId, double amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Compte introuvable avec l'id " + accountId));
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .date(new Date())
                .type(TransactionType.DEPOT)
                .account(account)
                .build();
        return transactionRepository.save(transaction);
    }

    @Tool(description = "Effectue un retrait d'argent sur un compte bancaire (échoue si le solde est insuffisant) et retourne la transaction créée")
    public Transaction withdraw(Long accountId, double amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Compte introuvable avec l'id " + accountId));
        if (account.getBalance() < amount) {
            throw new RuntimeException("Solde insuffisant pour effectuer ce retrait");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .date(new Date())
                .type(TransactionType.RETRAIT)
                .account(account)
                .build();
        return transactionRepository.save(transaction);
    }
}
