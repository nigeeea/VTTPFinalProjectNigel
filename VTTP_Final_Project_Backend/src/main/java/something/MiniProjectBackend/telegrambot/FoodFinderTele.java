package something.MiniProjectBackend.telegrambot;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;



public class FoodFinderTele extends TelegramLongPollingBot{

    @Override
    public void onUpdateReceived(Update update){

        System.out.println("backend tele working");
        System.out.println(update.getMessage().getText());
        System.out.println(update.getMessage().getFrom().getFirstName());
        String user = update.getMessage().getFrom().getFirstName();
        String chatId = update.getMessage().getChatId().toString();
        
        //sending greeting
        String command = update.getMessage().getText();
        String greeting = "hello "+user;
        SendMessage greetingMsg = new SendMessage();
        greetingMsg.setChatId(chatId);
        greetingMsg.setText(greeting);
        try {
            execute(greetingMsg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        //creating api call to backend
        String URL = "https://foodfinderbynigel.up.railway.app/api/teleGet";
        // String URL = "http://localhost:8085/api/teleGet";
        String url = UriComponentsBuilder.fromUriString(URL)
                                            .queryParam("email", command)
                                            .toUriString();
        
        RequestEntity<Void> req1 = RequestEntity.get(url)
                                                .build();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req1, String.class);
        System.out.println(resp.getStatusCode());
        String payload1 = resp.getBody();
        System.out.println(payload1);

        //sending back list of favourite recipes
        String reply = "Your Favourite Recipes:\n"+payload1;
        SendMessage replyMsg = new SendMessage();
        replyMsg.setChatId(update.getMessage().getChatId().toString());
        replyMsg.setText(reply);
        try {
            execute(replyMsg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public String getBotUsername() {

        return "FoodFinderAppBot";
    }

    @Override
    public String getBotToken() {

        return "5887155579:AAHV_wdbxt4Px-qIWoUWCVvJZb725F00d_s";
    }

}
