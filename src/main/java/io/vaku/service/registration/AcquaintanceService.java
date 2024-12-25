package io.vaku.service.registration;

import io.vaku.model.domain.User;
import io.vaku.service.domain.UserBioQuestionService;
import io.vaku.service.notification.AcquaintanceNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.vaku.util.StringConstants.EMOJI_WOW;

@Service
public class AcquaintanceService {
    private final UserBioQuestionService userBioQuestionService;
    private final AcquaintanceNotificationService notificationService;

    @Autowired
    public AcquaintanceService(UserBioQuestionService userBioQuestionService, AcquaintanceNotificationService notificationService) {
        this.userBioQuestionService = userBioQuestionService;
        this.notificationService = notificationService;
    }

    public void sendAcquaintanceMessage(User user) {
        var userName = user.getSpecifiedName();
        var sb = new StringBuilder("\uD83C\uDF89 Поприветствуем нового жильца по имени " + userName + "!\n\n");
        sb.append("\uD83D\uDCDD Вот что ").append(userName).append(" рассказал(а) о себе: ").append(user.getBio());

        for (var userBioQuestion : userBioQuestionService.findByUserId(user.getId())) {
            sb.append("\n\n");
            sb.append("❓ Мы спросили у ").append(userName).append(": ");
            sb.append(userBioQuestion.getQuestion().getQuestion());
            sb.append("\n");
            sb.append(EMOJI_WOW + " Ответ убил: ").append(userBioQuestion.getAnswer());
        }

        notificationService.sendMessage(sb.toString(), user.getPhotoFileId());
    }
}
