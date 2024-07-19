package io.vaku.model.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "meeting_room_booking")
public class MeetingRoomBooking {

    @Id
    @Column(name = "id")
    private UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Transient
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    public MeetingRoomBooking(UUID id, Date startTime, Date endTime, User user) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
    }
}
