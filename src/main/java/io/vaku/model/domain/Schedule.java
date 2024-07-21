package io.vaku.model.domain;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Schedule {

    private Date startDate;
    private Date endDate;
    private String description;

    public Schedule(Date startDate, Date endDate, String description) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public Schedule(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
