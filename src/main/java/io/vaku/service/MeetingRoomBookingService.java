package io.vaku.service;

import io.vaku.model.MeetingRoomBooking;
import io.vaku.repository.MeetingRoomBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MeetingRoomBookingService {

    @Autowired
    private MeetingRoomBookingRepository repository;

    @Transactional
    public void createOrUpdate(MeetingRoomBooking booking) {
        repository.save(booking);
    }
}
