package io.vaku.model.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bio_question")
public class BioQuestion {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "question")
    private String question;

    @OneToMany(mappedBy = "question")
    private List<UserBioQuestion> userBioQuestions;
}
