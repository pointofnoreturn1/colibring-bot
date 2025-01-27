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
                    "WHERE start_date >= :from AND end_date <= :to AND is_notified = FALSE",
            nativeQuery = true
    )
    List<UserMealDebt> findAllNotNotifiedBetween(@Param("from") Date from, @Param("to") Date to);

    @Query(
            value = "SELECT COUNT (*) " +
                    "FROM user_meal_debt " +
                    "WHERE start_date >= :from AND end_date <= :to AND user_id = :userId",
            nativeQuery = true
    )
    int countBetweenByUserId(@Param("from") Date from, @Param("to") Date to, @Param("userId") long userId);
}
