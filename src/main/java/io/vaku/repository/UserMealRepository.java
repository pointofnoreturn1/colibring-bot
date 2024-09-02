package io.vaku.repository;

import io.vaku.model.domain.UserMeal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserMealRepository extends CrudRepository<UserMeal, UUID> {
    @Query(
            value = "SELECT * " +
                    "FROM user_meal " +
                    "WHERE start_date >= :from AND end_date <= :to",
            nativeQuery = true
    )
    List<UserMeal> findAllBetween(@Param("from") Date from, @Param("to") Date to);
}
