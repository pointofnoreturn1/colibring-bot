package io.vaku.service.registration;

import io.vaku.model.domain.User;
import io.vaku.service.domain.UserBioQuestionService;
import io.vaku.service.notification.AcquaintanceNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        var sb = new StringBuilder("О себе: " + user.getBio());

        for (var userBioQuestion : userBioQuestionService.findByUserId(user.getId())) {
            sb.append("\n\n");
            sb.append("Вопрос: ").append(userBioQuestion.getQuestion().getQuestion());
            sb.append("\n");
            sb.append("Ответ: ").append(userBioQuestion.getAnswer());
        }

        notificationService.sendMessage(sb.toString(), user.getPhotoFileId());
    }
}
