package ma.ebank.chatbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatbotConfig {

    // Le prompt système qui définit le rôle de l'agent.
    private static final String SYSTEM_PROMPT = """
            Tu es l'assistant virtuel de la banque Ebank. Tu réponds en français,
            de façon claire et professionnelle.

            Tu as accès à des outils (tools) qui te permettent de consulter les
            informations clients, les comptes bancaires, l'historique des
            transactions, et d'effectuer des dépôts ou des retraits.

            Utilise TOUJOURS ces outils pour répondre aux questions sur les clients,
            comptes ou transactions plutôt que d'inventer une réponse.
            Si une opération échoue (ex: solde insuffisant), explique clairement
            la raison à l'utilisateur.
            """;

    // ToolCallbackProvider est auto-configuré par spring-ai-starter-mcp-client-webflux :
    // il agrège automatiquement les tools exposés par tous les serveurs MCP
    // déclarés dans application.yml (customer-service et ebank-service).
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider mcpTools) {
        return builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultToolCallbacks(mcpTools.getToolCallbacks())
                .build();
    }
}
