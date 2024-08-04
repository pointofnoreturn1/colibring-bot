package io.vaku.repository;

import io.vaku.model.domain.LaundryBooking;
import io.vaku.model.domain.TvBooking;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LndBookingRepository extends CrudRepository<LaundryBooking, UUID> {

    @Query(
            value = "SELECT * " +
                    "FROM laundry_booking " +
                    "WHERE user_id = :userId AND is_active = TRUE AND end_time >= CURRENT_DATE " +
                    "ORDER BY start_time",
            nativeQuery = true
    )
    List<LaundryBooking> findByUserId(@Param("userId") long userId);

    @Query(
            value = "SELECT * " +
                    "FROM laundry_booking " +
                    "WHERE is_active = TRUE AND end_time >= CURRENT_DATE " +
                    "ORDER BY start_time",
            nativeQuery = true
    )
    List<LaundryBooking> findAllActive();

    @Query(
            value = "SELECT * " +
                    "FROM laundry_booking " +
                    "WHERE is_active = TRUE AND end_time >= CURRENT_DATE AND is_notified = FALSE " +
                    "ORDER BY start_time",
            nativeQuery = true
    )
    List<LaundryBooking> findAllActiveNotNotified();

    @Modifying
    @Query(
            value = "UPDATE laundry_booking " +
                    "SET is_active = FALSE " +
                    "WHERE id = :id",
            nativeQuery = true
    )
    void removeById(@Param("id") UUID id);
}
