package io.vaku.service.domain.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
        UriComponentsBuilder telegramRequestBuilder = UriComponentsBuilder.fromHttpUrl("https://api.telegram.org/bot" + botToken + "/sendMessage");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("text", msg);
        rootNode.put("chat_id", chatId);
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity<?> entity = new HttpEntity<>(jsonString, headers);
        restTemplate.exchange(telegramRequestBuilder.toUriString(), HttpMethod.POST, entity, String.class);
    }
}
