package io.vaku.util;

import io.vaku.model.domain.User;

import static io.vaku.util.StringConstants.LARI;

public class StringUtils {
    private StringUtils() {}

    public static String getStringPrice(int price) {
        return "(" + price + LARI + ")";
    }

    // TODO: перенести эту логику в toString() сущности User?
    public static String getStringUser(User user) {
        var sb = new StringBuilder(user.getSpecifiedName());
        if (user.getTgUserName() == null) {
            sb
                    .append(user.getTgFirstName() == null ? "" : user.getTgFirstName())
                    .append(user.getTgLastName() == null ? "" : " " + user.getTgLastName());
        } else {
            sb.append(" @").append(user.getTgUserName());
        }

        return sb.toString();
    }
}
