package io.vaku.service;

import io.vaku.model.Room;
import io.vaku.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoomService {

    @Autowired
    private RoomRepository repository;

    public Iterable<Room> getAll() {
        return repository.findAll();
    }

    public Room findByNumber(String number) {
        return repository.findByNumber(number).orElse(null);
    }
}
