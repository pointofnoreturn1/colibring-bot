package io.vaku.model.domain;

import io.vaku.model.enm.DayOfWeek;
import io.vaku.model.enm.MealType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "meal_menu")
public class MealMenu {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();

    public MealMenu(UUID id, DayOfWeek dayOfWeek, MealType mealType, String name, int price) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.mealType = mealType;
        this.name = name;
        this.price = price;
    }
}
