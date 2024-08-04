package io.vaku.repository;

import io.vaku.model.domain.MeetingRoomBooking;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MtRoomBookingRepository extends CrudRepository<MeetingRoomBooking, UUID> {

    @Query(
            value = "SELECT * " +
                    "FROM meeting_room_booking " +
                    "WHERE user_id = :userId AND is_active = TRUE AND end_time >= CURRENT_DATE " +
                    "ORDER BY start_time",
            nativeQuery = true
    )
    List<MeetingRoomBooking> findByUserId(@Param("userId") long userId);

    @Query(
            value = "SELECT * " +
                    "FROM meeting_room_booking " +
                    "WHERE is_active = TRUE AND end_time >= CURRENT_DATE " +
                    "ORDER BY start_time",
            nativeQuery = true
    )
    List<MeetingRoomBooking> findAllActive();

    @Modifying
    @Query(
            value = "UPDATE meeting_room_booking " +
                    "SET is_active = FALSE " +
                    "WHERE id = :id",
            nativeQuery = true
    )
    void removeById(@Param("id") UUID id);
}
