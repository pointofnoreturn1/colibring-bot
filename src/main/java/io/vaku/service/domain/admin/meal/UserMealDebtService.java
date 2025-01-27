package io.vaku.service.domain.admin.meal;

import io.vaku.model.domain.UserMealDebt;
import io.vaku.repository.UserMealDebtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserMealDebtService {
    private final UserMealDebtRepository repository;

    @Autowired
    public UserMealDebtService(UserMealDebtRepository repository) {
        this.repository = repository;
    }

    public void saveAll(Iterable<UserMealDebt> userMealDebts) {
        repository.saveAll(userMealDebts);
    }

    @Transactional
    public void createOrUpdate(UserMealDebt userMealDebt) {
        repository.save(userMealDebt);
    }

    public List<UserMealDebt> findAllNotNotifiedBetween(Date from, Date to) {
        return repository.findAllNotNotifiedBetween(from, to);
    }

    public int countBetweenByUserId(Date from, Date to, long userId) {
        return repository.countBetweenByUserId(from, to, userId);
    }
}
