package io.vaku.service.domain.tv;

import io.vaku.model.domain.TvBooking;
import io.vaku.repository.TvBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TvBookingService {

    @Autowired
    private TvBookingRepository repository;

    @Transactional
    public void createOrUpdate(TvBooking booking) {
        repository.save(booking);
    }

    public TvBooking findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<TvBooking> findByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    public List<TvBooking> findAllActive() {
        return repository.findAllActive();
    }

    @Transactional
    public void removeById(UUID id) {
        repository.removeById(id);
    }
}
