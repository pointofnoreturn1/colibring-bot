package io.vaku.service;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.User;
import io.vaku.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository repository;

    // TODO
    public User findByUpdate(ClassifiedUpdate update) {
        return null;
    }

    @Transactional
    public void createOrUpdate(User user) {
        repository.save(user);
    }
}
