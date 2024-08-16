package io.vaku.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_meal_debt")
public class UserMealDebt {

    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "amount")
    private int amount;

    @Column(name = "is_notified")
    private boolean isNotified = false;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();

    public UserMealDebt(User user, int amount, boolean isNotified, Date startDate, Date endDate) {
        this.user = user;
        this.amount = amount;
        this.isNotified = isNotified;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
