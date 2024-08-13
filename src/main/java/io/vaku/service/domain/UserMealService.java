package io.vaku.service.domain;

import io.vaku.model.domain.UserMeal;
import io.vaku.repository.UserMealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserMealService {

    @Autowired
    private UserMealRepository repository;

    @Transactional
    public void createOrUpdate(UserMeal userMeal) {
        repository.save(userMeal);
    }
}
