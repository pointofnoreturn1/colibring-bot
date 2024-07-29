package io.vaku.model.domain;

import java.util.Date;
import java.util.UUID;

public interface Booking {

    UUID getId();

    Date getStartTime();

    Date getEndTime();

    boolean isActive();

    String getDescription();

    User getUser();

    Date getCreatedAt();
}
