package io.vaku.service.domain.laundry;

import io.vaku.model.domain.LaundryBooking;
import io.vaku.repository.LndBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class LaundryBookingService {

    @Autowired
    private LndBookingRepository repository;

    @Transactional
    public void createOrUpdate(LaundryBooking booking) {
        repository.save(booking);
    }

    public LaundryBooking findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<LaundryBooking> findByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    public List<LaundryBooking> findAllActive() {
        return repository.findAllActive();
    }

    @Transactional
    public void removeById(UUID id) {
        repository.removeById(id);
    }
}
