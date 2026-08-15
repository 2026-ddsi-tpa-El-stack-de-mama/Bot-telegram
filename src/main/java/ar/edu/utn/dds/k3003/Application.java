package ar.edu.utn.dds.k3003;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /**
   * Registra el TelegramBot contra la API de Telegram una vez que el contexto
   * termino de levantar. El ChatClient (con sus tools MCP) ya se resolvio antes,
   * durante la creacion del bean TelegramBot, contra el mcp-server desplegado
   * como servicio aparte (ver MCP_SERVER_URL).
   */
  @Bean
  CommandLineRunner registrarBotDeTelegram(TelegramBot telegramBot) {
    return args -> {
      TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
      try {
        botsApi.registerBot(telegramBot);
        System.out.println("Bot de Telegram iniciado correctamente");
      } catch (TelegramApiException e) {
        throw new RuntimeException("No se pudo registrar el bot de Telegram", e);
      }
    };
  }
}