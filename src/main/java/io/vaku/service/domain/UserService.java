package io.vaku.service.domain;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void resetUserState(User user) {
        user.setMtRoomBookingStatus(BookingStatus.NO_STATUS);
        user.setTvBookingStatus(BookingStatus.NO_STATUS);

        createOrUpdate(user);
    }
}
