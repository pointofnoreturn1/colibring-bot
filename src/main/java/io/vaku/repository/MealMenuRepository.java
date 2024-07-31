package io.vaku.repository;

import io.vaku.model.domain.MealMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MealMenuRepository extends JpaRepository<MealMenu, UUID> {
}
