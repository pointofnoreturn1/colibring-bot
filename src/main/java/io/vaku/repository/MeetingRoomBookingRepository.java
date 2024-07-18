package io.vaku.repository;

import io.vaku.model.MeetingRoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MeetingRoomBookingRepository extends JpaRepository<MeetingRoomBooking, UUID> {
}
