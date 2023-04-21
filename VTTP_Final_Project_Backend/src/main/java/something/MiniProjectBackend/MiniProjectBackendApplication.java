package something.MiniProjectBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import something.MiniProjectBackend.telegrambot.FoodFinderTele;

@SpringBootApplication
public class MiniProjectBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniProjectBackendApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void telegramBot() {
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new FoodFinderTele());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
