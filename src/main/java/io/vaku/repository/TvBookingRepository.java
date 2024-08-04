package io.vaku.repository;

import io.vaku.model.domain.TvBooking;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TvBookingRepository extends CrudRepository<TvBooking, UUID> {

    @Query(
            value = "SELECT * " +
                    "FROM tv_booking " +
                    "WHERE user_id = :userId AND is_active = TRUE AND end_time >= CURRENT_DATE " +
                    "ORDER BY start_time",
            nativeQuery = true
    )
    List<TvBooking> findByUserId(@Param("userId") long userId);

    @Query(
            value = "SELECT * " +
                    "FROM tv_booking " +
                    "WHERE is_active = TRUE AND end_time >= CURRENT_DATE " +
                    "ORDER BY start_time",
            nativeQuery = true
    )
    List<TvBooking> findAllActive();

    @Modifying
    @Query(
            value = "UPDATE tv_booking " +
                    "SET is_active = FALSE " +
                    "WHERE id = :id",
            nativeQuery = true
    )
    void removeById(@Param("id") UUID id);
}
