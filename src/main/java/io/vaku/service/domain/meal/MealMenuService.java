package io.vaku.service.domain.meal;

import io.vaku.model.domain.MealMenu;
import io.vaku.repository.MealMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealMenuService {

    @Autowired
    private MealMenuRepository repository;

    public List<MealMenu> findAllSorted() {
        return repository.findAll(Sort.by(Sort.Order.asc("dayOfWeek"), Sort.Order.asc("mealType")));
    }
}
