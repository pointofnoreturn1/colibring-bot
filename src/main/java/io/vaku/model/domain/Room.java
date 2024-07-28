package io.vaku.model.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room")
public class Room {

    @Id
    @Column(name = "id")
    private UUID id;

    @OneToMany(mappedBy = "room")
    private List<User> users;

    @Column(name = "number")
    private String number;

    @Column(name = "is_hostel")
    private boolean isHostel;
}
