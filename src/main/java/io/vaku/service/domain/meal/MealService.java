package io.vaku.service.domain.meal;

import io.vaku.model.domain.Meal;
import io.vaku.repository.MealMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MealService {

    @Autowired
    private MealMenuRepository repository;

    public List<Meal> findAllSorted() {
        return repository.findAll(Sort.by(Sort.Order.asc("dayOfWeek"), Sort.Order.asc("mealType")));
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Transactional
    public void saveAll(List<Meal> menu) {
        repository.saveAll(menu);
    }
}
