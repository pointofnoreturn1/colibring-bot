package io.vaku.service.registration;

import io.vaku.model.domain.User;
import io.vaku.service.domain.UserBioQuestionService;
import io.vaku.service.notification.AcquaintanceNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.vaku.model.enm.Role.RESIDENT;

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
        if (user.getRole().equals(RESIDENT)) {
            sendResidentAcquaintance(user);
        } else {
            sendStaffAcquaintance(user);
        }
    }

    private void sendResidentAcquaintance(User user) {
        var sb = new StringBuilder("\uD83C\uDF89 Ура, с нами теперь живёт " + user.getSpecifiedName() + "!\n\n");
        sb.append("\uD83D\uDCDD  ").append(user.getBio());

        for (var userBioQuestion : userBioQuestionService.findByUserId(user.getId())) {
            sb.append("\n\n");
            sb.append("❓ ").append(userBioQuestion.getQuestion().getQuestion());
            sb.append("\n");
            sb.append("❕ ").append(userBioQuestion.getAnswer());
        }

        notificationService.sendResidentMessage(sb.toString(), user.getPhotoFileId());
    }

    private void sendStaffAcquaintance(User user) {
        var role = user.getRole().genNameRu();
        var name = user.getSpecifiedName();
        var bio = user.getBio();

        notificationService.sendStaffMessage(
                "\uD83C\uDF89 Ура, с нами новый " + role + " по имени " + name + "!\n\n" + "\uD83D\uDCDD  " + bio,
                user.getPhotoFileId()
        );
    }
}
