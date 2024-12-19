package io.vaku.model.domain;

import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.model.enm.MealType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "meal")
public final class Meal {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "day_of_week", nullable = false)
    private CustomDayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private final Date createdAt = new Date();

    @OneToMany(mappedBy = "meal", fetch = FetchType.EAGER)
    private List<UserMeal> userMeals;

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

    public Meal(UUID id, CustomDayOfWeek dayOfWeek, MealType mealType, String name, int price, Date startDate, Date endDate) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.mealType = mealType;
        this.name = name;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
