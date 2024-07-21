package io.vaku.service.domain;

import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.repository.MtRoomBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MtRoomBookingService {

    @Autowired
    private MtRoomBookingRepository repository;

    @Transactional
    public void createOrUpdate(MeetingRoomBooking booking) {
        repository.save(booking);
    }

    public List<MeetingRoomBooking> findByUserId(long userId) {
        return repository.findByUserId(userId);
    }
}
