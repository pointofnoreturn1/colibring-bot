package io.vaku.repository;

import io.vaku.model.domain.UserBioQuestion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserBioQuestionRepository extends CrudRepository<UserBioQuestion, UUID> {
}
