package io.vaku.service.domain;

import io.vaku.model.domain.BioQuestion;
import io.vaku.repository.BioQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BioQuestionService {
    private final BioQuestionRepository bioQuestionRepository;

    @Autowired
    public BioQuestionService(BioQuestionRepository bioQuestionRepository) {
        this.bioQuestionRepository = bioQuestionRepository;
    }

    public List<BioQuestion> getTwoRandomQuestions() {
        return bioQuestionRepository.getTwoRandomQuestions();
    }
}
