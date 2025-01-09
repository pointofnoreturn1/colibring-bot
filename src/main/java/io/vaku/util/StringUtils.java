package io.vaku.util;

import io.vaku.model.domain.User;

import static io.vaku.util.StringConstants.EMOJI_IS_VEGAN;
import static io.vaku.util.StringConstants.LARI;

public final class StringUtils {
    private StringUtils() {}

    public static String getStringPrice(int price) {
        return "(" + price + LARI + ")";
    }

    public static String getStringUser(User user) {
        return getStringUser(user, false);
    }

    public static String getStringUser(User user, boolean escape) {
        return user.getSpecifiedName() + getUserName(user, escape);
    }

    public static String getStringUserForAdmin(User user) {
        var sb = new StringBuilder();
        sb.append("user: ");
        if (user.isVegan()) {
            sb.append(EMOJI_IS_VEGAN);
        }
        if (user.getSpecifiedName() != null) {
            sb.append(user.getSpecifiedName());
        }
        sb.append(getUserName(user, false));
        sb.append("\nid: ").append(user.getId());

        return sb.toString();
    }

    public static String getUserName(User user, boolean escape) {
        var sb = new StringBuilder();
        if (user.getTgUserName() == null) {
            sb.append(" ");
            if (escape) {
                sb.append("\\");
            }
            sb.append("(");
            if (user.getTgFirstName() != null) {
                sb.append(user.getTgFirstName());
            }
            if (user.getTgLastName() != null) {
                sb.append(" ").append(user.getTgLastName());
            }
            if (escape) {
                sb.append("\\");
            }
            sb.append(")");
        } else {
            sb.append(" @").append(user.getTgUserName());
        }

        return sb.toString();
    }
}
