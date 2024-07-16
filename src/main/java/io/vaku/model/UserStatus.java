package io.vaku.model;

public enum UserStatus {
    REQUIRE_REGISTRATION,
    REQUIRE_PASSWORD,
    REQUIRE_NAME,
    REQUIRE_BIRTHDATE,
    REQUIRE_ROOM,
    REQUIRE_BIO,
    REGISTERED,
    BLOCKED
}
