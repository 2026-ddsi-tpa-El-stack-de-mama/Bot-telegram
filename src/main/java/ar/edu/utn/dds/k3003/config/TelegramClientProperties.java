package ar.edu.utn.dds.k3003.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public class TelegramClientProperties {

    private String botUsername;
    private String botToken;

    public String getBotUsername() {
        return botUsername;
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = "Grupo8_DSI_bot";
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = "8915397259:AAGqkLWOyBF7vStNGjAfc7BIjZApECA9smQ";
    }
}
