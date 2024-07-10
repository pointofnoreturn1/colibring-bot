package io.vaku.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
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
    private int number;

    @Column(name = "is_hostel")
    private boolean isHostel;
}
