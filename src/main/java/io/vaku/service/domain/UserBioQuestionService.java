package io.vaku.service.domain;

import io.vaku.model.domain.UserBioQuestion;
import io.vaku.repository.UserBioQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserBioQuestionService {
    private final UserBioQuestionRepository repository;

    @Autowired
    public UserBioQuestionService(UserBioQuestionRepository repository) {
        this.repository = repository;
    }

    public List<UserBioQuestion> findByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    @Transactional
    public void createOrUpdate(UserBioQuestion userBioQuestion) {
        repository.save(userBioQuestion);
    }
}
