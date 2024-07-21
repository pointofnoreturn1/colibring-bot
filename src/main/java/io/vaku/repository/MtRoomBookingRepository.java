package io.vaku.repository;

import io.vaku.model.domain.MeetingRoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MtRoomBookingRepository extends JpaRepository<MeetingRoomBooking, UUID> {

    @Query(
            value = "SELECT * FROM " +
                    "meeting_room_booking " +
                    "WHERE user_id = ?1 AND is_active = TRUE " +
                    "ORDER BY start_time",
            nativeQuery = true
    )
    List<MeetingRoomBooking> findByUserId(long userId);
}
