package spring.dacn.mercury.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.dacn.mercury.entities.ChatBot;

public interface ChatBotRepository extends JpaRepository<ChatBot, Long> {
}