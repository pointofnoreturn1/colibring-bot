package io.vaku.repository;

import io.vaku.model.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends CrudRepository<Room, UUID> {

    Optional<Room> findByNumber(String number);
}
