package io.vaku.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vaku.model.domain.LaundryBooking;
import io.vaku.model.domain.User;
import io.vaku.service.domain.laundry.LaundryBookingService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import static io.vaku.util.StringConstants.EMOJI_NOTIFICATION;
import static io.vaku.util.StringConstants.TEXT_LAUNDRY_NOTIFICATION;

@Service
public class LaundryNotificationService {

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LaundryBookingService laundryBookingService;

    @Scheduled(fixedRate = 60000) // 1 minute
    public void checkUpcomingWashes() {
        Map<LaundryBooking, User> laundryBookingToUser = laundryBookingService.findAllActiveNotNotified()
                .stream()
                .collect(Collectors.toMap(it -> it, LaundryBooking::getUser));

        for (Map.Entry<LaundryBooking, User> entry : laundryBookingToUser.entrySet()) {
            LaundryBooking booking = entry.getKey();
            if (isInFifteenMinutes(booking)) {
                notify(entry.getValue().getChatId(), EMOJI_NOTIFICATION + TEXT_LAUNDRY_NOTIFICATION);
                booking.setNotified(true);
                laundryBookingService.createOrUpdate(booking);
            }
        }
    }

    @SneakyThrows
    private void notify(long chatId, String msg) {
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

    private boolean isInFifteenMinutes(LaundryBooking booking) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startTime = LocalDateTime.ofInstant(booking.getStartTime().toInstant(), ZoneId.systemDefault());
        LocalDateTime createdAt = LocalDateTime.ofInstant(booking.getCreatedAt().toInstant(), ZoneId.systemDefault());

        if (Duration.between(createdAt, startTime).toMinutes() < 15) {
            return false;
        }

        long duration = Duration.between(now, startTime).toMinutes();

        return duration < 15 && duration > 0;
    }
}
