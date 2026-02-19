package senderbot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {
    private String name;
    private String key;
    private long chatId; // id чата для рассылки
}