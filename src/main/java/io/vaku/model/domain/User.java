package io.vaku.model.domain;

import io.vaku.model.enm.AdminStatus;
import io.vaku.model.enm.Lang;
import io.vaku.model.enm.BookingStatus;
import io.vaku.model.enm.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
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

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    @Column(name = "birth_date")
    private Date birthDate;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @Column(name = "bio")
    private String bio;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "is_admin")
    private boolean isAdmin = false;

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

    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Meal> userMeals;

    public User(long id, long chatId, String tgUserName, String tgFirstName, String tgLastName) {
        this.id = id;
        this.chatId = chatId;
        this.tgUserName = tgUserName;
        this.tgFirstName = tgFirstName;
        this.tgLastName = tgLastName;
    }
}
