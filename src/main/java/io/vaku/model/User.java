package io.vaku.model;

import io.vaku.model.enm.Lang;
import io.vaku.model.enm.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @Column(name = "meeting_room_booking_expected", nullable = false)
    private boolean meetingRoomBookingExpected = false;

    @Column(name = "tv_booking_expected", nullable = false)
    private boolean tvBookingExpected = false;

    @Column(name = "washing_booking_expected", nullable = false)
    private boolean washingBookingExpected = false;

    @Column(name = "food_restrictions_expected", nullable = false)
    private boolean foodRestrictionsExpected = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "lang", nullable = false)
    private Lang lang;

    @Transient
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @OneToMany(mappedBy = "user")
    private List<MeetingRoomBooking> meetingRoomBookings;

    public User(long id, long chatId, String tgUserName, String tgFirstName, String tgLastName) {
        this.id = id;
        this.chatId = chatId;
        this.tgUserName = tgUserName;
        this.tgFirstName = tgFirstName;
        this.tgLastName = tgLastName;
    }
}
