package io.vaku.repository;

import io.vaku.model.domain.Meal;
import io.vaku.model.enm.DayOfWeek;
import io.vaku.model.enm.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MealMenuRepository extends JpaRepository<Meal, UUID> {
}
