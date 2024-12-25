package io.vaku.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

@Service
public class TelegramClient {
    private static final String TG_API = "https://api.telegram.org/bot";
    private final String botToken;
    private final RestTemplate restTemplate;

    @Autowired
    public TelegramClient(@Value("${bot.token}") String botToken, RestTemplate restTemplate) {
        this.botToken = botToken;
        this.restTemplate = restTemplate;
    }

    public void sendMessage(long chatId, String msg) {
        sendMessage(chatId, msg, false);
    }

    @SneakyThrows
    public void sendMessage(long chatId, String msg, boolean enableMarkdown) {
        var mapper = new ObjectMapper();
        var rootNode = mapper.createObjectNode();
        rootNode.put("chat_id", chatId);
        rootNode.put("text", msg);
        if (enableMarkdown) {
            rootNode.put("parse_mode", ParseMode.MARKDOWNV2);
        }
        var jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        var uriBuilder = UriComponentsBuilder.fromHttpUrl(TG_API + botToken + "/sendMessage");
        var entity = new HttpEntity<>(jsonString, getHeaders());

        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, String.class);
    }

    @SneakyThrows
    public void sendPhotoToTopic(long chatId, int threadId, String msg, String photoId) {
        var mapper = new ObjectMapper();
        var rootNode = mapper.createObjectNode();
        rootNode.put("chat_id", chatId);
        rootNode.put("message_thread_id", threadId);
        rootNode.put("caption", msg);
        rootNode.put("photo", photoId);
        var jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        var uriBuilder = UriComponentsBuilder.fromHttpUrl(TG_API + botToken + "/sendPhoto");
        var entity = new HttpEntity<>(jsonString, getHeaders());

        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, String.class);
    }

    private LinkedMultiValueMap<String, String> getHeaders() {
        return new LinkedMultiValueMap<>() {{
            add(HttpHeaders.CONTENT_TYPE, "application/json");
        }};
    }
}
