package io.vaku.service.domain;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.User;
import io.vaku.model.enm.AdminStatus;
import io.vaku.model.enm.BookingStatus;
import io.vaku.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.vaku.model.enm.BookingStatus.NO_STATUS;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository repository;

    public User findByUpdate(ClassifiedUpdate update) {
        return repository.findByChatId(update.getChatId()).orElse(null);
    }

    @Transactional
    public void createOrUpdate(User user) {
        repository.save(user);
    }

    @Transactional
    public void updateLastMsgId(long userId, int msgId) {
        repository.updateLastMsgId(userId, msgId);
    }

    @Transactional
    public void resetUserState(User user) {
        user.setMtRoomBookingStatus(NO_STATUS);
        user.setTvBookingStatus(NO_STATUS);
        user.setLaundryBookingStatus(NO_STATUS);
        user.setMealSignUpStatus(NO_STATUS);
        user.setAdminStatus(AdminStatus.NO_STATUS);

        createOrUpdate(user);
    }
}
