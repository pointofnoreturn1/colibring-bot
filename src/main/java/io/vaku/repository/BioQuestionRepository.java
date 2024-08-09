package io.vaku.repository;

import io.vaku.model.domain.BioQuestion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BioQuestionRepository extends CrudRepository<BioQuestion, UUID> {

    @Query(
            value = "SELECT * " +
                    "FROM bio_question " +
                    "ORDER BY random() LIMIT 1",
            nativeQuery = true
    )
    BioQuestion getRandomQuestion();
}
