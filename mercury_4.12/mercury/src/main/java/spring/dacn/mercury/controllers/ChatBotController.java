package spring.dacn.mercury.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.dacn.mercury.entities.ChatBot;
import spring.dacn.mercury.services.ChatBotService;

import java.util.List;

@RestController
@RequestMapping("/api/chatbots")
public class ChatBotController {
    private final ChatBotService chatBotService;

    @Autowired
    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @GetMapping
    public List<ChatBot> getAllChatBots() {
        return chatBotService.getAllChatBots();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatBot> getChatBotById(@PathVariable Long id) {
        ChatBot chatBot = chatBotService.getChatBotById(id);
        return ResponseEntity.ok(chatBot);
    }

    @PostMapping
    public ResponseEntity<ChatBot> createChatBot(@RequestBody ChatBot chatBot) {
        ChatBot createdChatBot = chatBotService.createChatBot(chatBot);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChatBot);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChatBot> updateChatBot(@PathVariable Long id, @RequestBody ChatBot chatBot) {
        ChatBot updatedChatBot = chatBotService.updateChatBot(id, chatBot);
        return ResponseEntity.ok(updatedChatBot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatBot(@PathVariable Long id) {
        chatBotService.deleteChatBot(id);
        return ResponseEntity.noContent().build();
    }
}
