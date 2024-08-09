package io.vaku.service.domain;

import io.vaku.model.domain.BioQuestion;
import io.vaku.repository.BioQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BioQuestionService {

    @Autowired
    private BioQuestionRepository bioQuestionRepository;

    public BioQuestion getRandomQuestion() {
        return bioQuestionRepository.getRandomQuestion();
    }
}
