package ma.ebank.chatbot.controllers;

import lombok.AllArgsConstructor;
import ma.ebank.chatbot.dto.ChatRequest;
import ma.ebank.chatbot.dto.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@AllArgsConstructor
@CrossOrigin("*")
public class ChatController {

    private final ChatClient chatClient;

    // Endpoint unique consommé à la fois par le dashboard Angular et par le bot Telegram.
    @PostMapping("/ask")
    public ChatResponse ask(@RequestBody ChatRequest request) {
        String reply = chatClient.prompt()
                .user(request.message())
                .call()
                .content();
        return new ChatResponse(reply);
    }
}
