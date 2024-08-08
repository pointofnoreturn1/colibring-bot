package io.vaku.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
}
