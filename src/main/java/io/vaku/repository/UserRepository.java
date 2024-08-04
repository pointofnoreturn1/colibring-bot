package io.vaku.repository;

import io.vaku.model.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByChatId(long chatId);

    @Modifying
    @Query(
            value = "UPDATE \"user\" " +
                    "SET last_msg_id = :msgId " +
                    "WHERE id = :userId",
            nativeQuery = true
    )
    void updateLastMsgId(@Param("userId") long userId, @Param("msgId") int msgId);
}
