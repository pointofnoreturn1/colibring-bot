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

    @Autowired
    private UserMealDebtRepository repository;

    @Transactional
    public void createOrUpdate(UserMealDebt userMealDebt) {
        repository.save(userMealDebt);
    }

    public List<UserMealDebt> findAllActive(Date date) {
        return repository.findAllActive(date);
    }
}
