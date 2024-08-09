package io.vaku.service.domain;

import io.vaku.model.domain.UserBioQuestion;
import io.vaku.repository.UserBioQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserBioQuestionService {

    @Autowired
    private UserBioQuestionRepository repository;

    public List<UserBioQuestion> getUserBioQuestionsByUserId(UUID userId) {
        return (List<UserBioQuestion>) repository.findAllById(List.of(userId));
    }

    @Transactional
    public void createOrUpdate(UserBioQuestion userBioQuestion) {
        repository.save(userBioQuestion);
    }
}
