package spring.dacn.mercury.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.dacn.mercury.entities.ChatBot;
import spring.dacn.mercury.repositories.ChatBotRepository;

import java.util.List;

@Service
public class ChatBotService {
    private final ChatBotRepository chatBotRepository;

    @Autowired
    public ChatBotService(ChatBotRepository chatBotRepository) {
        this.chatBotRepository = chatBotRepository;
    }

    public List<ChatBot> getAllChatBots() {
        return chatBotRepository.findAll();
    }

    public ChatBot getChatBotById(Long id) {
        return chatBotRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("ChatBot not found"));
    }

    public ChatBot createChatBot(ChatBot chatBot) {
        return chatBotRepository.save(chatBot);
    }

    public ChatBot updateChatBot(Long id, ChatBot chatBot) {
        ChatBot existingChatBot = getChatBotById(id);
        // Update fields here
        return chatBotRepository.save(existingChatBot);
    }

    public void deleteChatBot(Long id) {
        chatBotRepository.deleteById(id);
    }
}
