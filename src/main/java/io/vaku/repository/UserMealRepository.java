package io.vaku.repository;

import io.vaku.model.domain.UserMeal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserMealRepository extends CrudRepository<UserMeal, UUID> {
}
