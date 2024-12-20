package io.vaku.service.domain.notification;

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

@Service
public class NotificationService {

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private RestTemplate restTemplate;

    @SneakyThrows
    public void notify(long chatId, String msg) {
        var telegramRequestBuilder = UriComponentsBuilder
                .fromHttpUrl("https://api.telegram.org/bot" + botToken + "/sendMessage");

        var mapper = new ObjectMapper();
        var rootNode = mapper.createObjectNode();
        rootNode.put("text", msg);
        rootNode.put("chat_id", chatId);
        rootNode.put("parse_mode", "MarkdownV2");
        var jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

        var headers = new LinkedMultiValueMap<String, String>();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity<?> entity = new HttpEntity<>(jsonString, headers);
        restTemplate.exchange(telegramRequestBuilder.toUriString(), HttpMethod.POST, entity, String.class);
    }
}
