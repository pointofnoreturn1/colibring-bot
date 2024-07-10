package io.vaku.service;

import io.vaku.model.User;
import io.vaku.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public Optional<User> findByChatId(long chatId) {
        return repository.findByChatId(chatId);
    }
}
