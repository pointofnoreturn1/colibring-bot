package io.vaku.model.domain;

import io.vaku.model.enm.*;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@FilterDef(
        name = "userMealsFilter",
        parameters = {@ParamDef(name = "from", type = Date.class), @ParamDef(name = "to", type = Date.class)}
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user", schema = "public")
public class User {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "chat_id", unique = true, nullable = false)
    private long chatId;

    @Column(name = "last_msg_id")
    private int lastMsgId;

    @Column(name = "tg_user_name")
    private String tgUserName;

    @Column(name = "tg_first_name")
    private String tgFirstName;

    @Column(name = "tg_last_name")
    private String tgLastName;

    @Column(name = "specified_name")
    private String specifiedName;

    @Column(name = "birth_day")
    private int birthDay;

    @Column(name = "birth_month")
    private int birthMonth;

    @Column(name = "birth_year")
    private int birthYear;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @Column(name = "bio")
    private String bio;

    @Column(name = "photo_file_id")
    private String photoFileId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "is_vegan")
    private boolean isVegan = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "mt_room_booking_status", nullable = false)
    private BookingStatus mtRoomBookingStatus = BookingStatus.NO_STATUS;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tv_booking_status", nullable = false)
    private BookingStatus tvBookingStatus = BookingStatus.NO_STATUS;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "lnd_booking_status", nullable = false)
    private BookingStatus laundryBookingStatus = BookingStatus.NO_STATUS;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "meal_sign_up_status", nullable = false)
    private BookingStatus mealSignUpStatus = BookingStatus.NO_STATUS;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "admin_status", nullable = false)
    private AdminStatus adminStatus = AdminStatus.NO_STATUS;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "lang", nullable = false)
    private Lang lang;

    @Transient
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @OneToMany(mappedBy = "user")
    private List<MeetingRoomBooking> mtRoomBookings;

    @OneToMany(mappedBy = "user")
    private List<TvBooking> tvBookings;

    @OneToMany(mappedBy = "user")
    private List<LaundryBooking> laundryBookings;

    @Filter(name = "userMealsFilter", condition = "start_date >= :from AND end_date <= :to")
    @OneToMany(mappedBy = "user")
    private List<UserMeal> userMeals;

    @OneToMany(mappedBy = "user")
    private List<UserBioQuestion> userBioQuestions;

    @OneToMany(mappedBy = "user")
    private List<UserMealDebt> userMealDebts;

    public User(long id, long chatId, String tgUserName, String tgFirstName, String tgLastName) {
        this.id = id;
        this.chatId = chatId;
        this.tgUserName = tgUserName;
        this.tgFirstName = tgFirstName;
        this.tgLastName = tgLastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
