package org.example.chatbot.Web;


import org.example.chatbot.Agents.ChatOrchestrator;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chatbot")
public class ChatController {

    private final ChatOrchestrator orchestrator;

    public ChatController(ChatOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping("/chat")
    public Mono<String> chat(@RequestParam String query,
                             @RequestHeader(name = "Authorization", required = false) String auth) {
        return orchestrator.chatOnce(query, auth);
    }

}


