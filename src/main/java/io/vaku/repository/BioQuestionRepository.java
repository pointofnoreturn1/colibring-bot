package io.vaku.repository;

import io.vaku.model.domain.BioQuestion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BioQuestionRepository extends CrudRepository<BioQuestion, UUID> {
    @Query(
            value = "SELECT * " +
                    "FROM bio_question " +
                    "ORDER BY random() LIMIT 2",
            nativeQuery = true
    )
    List<BioQuestion> getTwoRandomQuestions();
}
