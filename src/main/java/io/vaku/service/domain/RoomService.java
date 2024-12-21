package io.vaku.service.domain;

import io.vaku.model.domain.Room;
import io.vaku.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomService {

    @Autowired
    private RoomRepository repository;

    public List<Room> getAll() {
        return (List<Room>) repository.findAll();
    }

    public Room findByNumber(String number) {
        return repository.findByNumber(number).orElse(null);
    }
}
