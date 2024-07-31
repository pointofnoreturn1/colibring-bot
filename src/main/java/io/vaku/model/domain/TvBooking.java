package io.vaku.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tv_booking")
public class TvBooking implements Booking {

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();

    public TvBooking(UUID id, Date startTime, Date endTime, String description, User user) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.user = user;
    }
}
