package io.vaku.model.domain;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Schedule {

    private Date startTime;
    private Date endTime;
    private String description;

    public Schedule(Date startTime, Date endTime, String description) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public Schedule(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
