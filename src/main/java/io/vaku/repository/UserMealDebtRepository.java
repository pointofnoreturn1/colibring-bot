package io.vaku.repository;

import io.vaku.model.domain.UserMealDebt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserMealDebtRepository extends CrudRepository<UserMealDebt, UUID> {

    @Query(
            value = "SELECT * " +
                    "FROM user_meal_debt " +
                    "WHERE start_date < :date AND end_date = :date",
            nativeQuery = true
    )
    List<UserMealDebt> findAllActive(@Param("date") Date date);
}
