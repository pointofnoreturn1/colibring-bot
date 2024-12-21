package io.vaku.repository;

import io.vaku.model.domain.Meal;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface MealMenuRepository extends JpaRepository<Meal, UUID> { // JpaRepository is used here on purpose

    @Query(
            value = "SELECT COUNT(*) " +
                    "FROM meal " +
                    "WHERE start_date = :date OR start_date > :date",
            nativeQuery = true
    )
    int countByStartDateIsAfter(@Param("date") Date date);

    List<Meal> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(Date startDate, Date endDate, Sort sort);
}
