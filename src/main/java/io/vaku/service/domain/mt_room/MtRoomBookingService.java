package io.vaku.service.domain.mt_room;

import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.repository.MtRoomBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MtRoomBookingService {

    @Autowired
    private MtRoomBookingRepository repository;

    @Transactional
    public MeetingRoomBooking createOrUpdate(MeetingRoomBooking booking) {
        return repository.save(booking);
    }

    public MeetingRoomBooking findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<MeetingRoomBooking> findByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    public List<MeetingRoomBooking> findAllActive() {
        return repository.findAllActive();
    }

    @Transactional
    public void removeById(UUID id) {
        repository.removeById(id);
    }
}
