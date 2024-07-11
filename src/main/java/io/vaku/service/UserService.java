package io.vaku.service;

import io.vaku.model.User;
import io.vaku.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository repository;

    public Optional<User> findByChatId(long chatId) {
        return repository.findByChatId(chatId);
    }

    @Transactional
    public void createOrUpdate(User user) {
        repository.save(user);
    }
}
