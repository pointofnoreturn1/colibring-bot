package io.vaku.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Getter
@Component
public final class EnvHolder {
    private final String botDatasourceUrl;
    private final String botDatasourceUsername;
    private final String botDatasourcePassword;
    private final String botName;
    private final String botToken;
    private final String botRegisterPassword;
    private final String[] botCookDaysOff;
    private final Long botUserGroupId;
    private final Long botUserNotificationsTopicId;
    private final Long botUserResidentsTopicId;
    private final Long botUserStaffTopicId;
    private final Long botAdminGroupId;
    private final Integer botConnectionTimeout;

    @Autowired
    public EnvHolder(Environment env) {
        this.botDatasourceUrl = env.getProperty("BOT_DATASOURCE_URL", String.class);
        this.botDatasourceUsername = env.getProperty("BOT_DATASOURCE_USERNAME", String.class);
        this.botDatasourcePassword = env.getProperty("BOT_DATASOURCE_PASSWORD", String.class);
        this.botName = env.getProperty("BOT_NAME", String.class);
        this.botToken = env.getProperty("BOT_TOKEN", String.class);
        this.botRegisterPassword = env.getProperty("BOT_REGISTER_PASSWORD", String.class);
        this.botCookDaysOff = env.getProperty("BOT_COOK_DAYS_OFF", String[].class);
        this.botUserGroupId = env.getProperty("BOT_USER_GROUP_ID", Long.class);
        this.botUserNotificationsTopicId = env.getProperty("BOT_USER_NOTIFICATIONS_TOPIC_ID", Long.class);
        this.botUserResidentsTopicId = env.getProperty("BOT_USER_RESIDENTS_TOPIC_ID", Long.class);
        this.botUserStaffTopicId = env.getProperty("BOT_USER_STAFF_TOPIC_ID", Long.class);
        this.botAdminGroupId = env.getProperty("BOT_ADMIN_GROUP_ID", Long.class);
        this.botConnectionTimeout = env.getProperty("BOT_CONNECTION_TIMEOUT", Integer.class);
    }
}
