package io.vaku.service.domain;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.User;
import io.vaku.model.enm.AdminStatus;
import io.vaku.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.vaku.model.enm.BookingStatus.NO_STATUS;
import static io.vaku.model.enm.UserStatus.REGISTERED;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository repository;

    public User findByUpdate(ClassifiedUpdate update) {
        return repository.findByChatId(update.getChatId()).orElse(null);
    }

    // TODO: реализовать с помощью динамических фильтров
    public List<User> findAllActive() {
        return ((List<User>) repository.findAll())
                .stream()
                .filter(it -> it.getStatus().equals(REGISTERED))
                .toList();
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
