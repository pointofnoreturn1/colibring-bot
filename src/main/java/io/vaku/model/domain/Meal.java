package io.vaku.model.domain;

import io.vaku.model.enm.DayOfWeek;
import io.vaku.model.enm.MealType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "meal")
public class Meal {

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_meal",
            joinColumns = @JoinColumn(name = "meal_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meal meal = (Meal) o;
        return price == meal.price && Objects.equals(id, meal.id) && dayOfWeek == meal.dayOfWeek && mealType == meal.mealType && Objects.equals(name, meal.name) && Objects.equals(createdAt, meal.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dayOfWeek, mealType, name, price, createdAt);
    }

    public Meal(UUID id, DayOfWeek dayOfWeek, MealType mealType, String name, int price) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.mealType = mealType;
        this.name = name;
        this.price = price;
    }
}
