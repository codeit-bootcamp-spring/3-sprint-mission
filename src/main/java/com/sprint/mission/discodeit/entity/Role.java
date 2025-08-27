package com.sprint.mission.discodeit.entity;

public enum Role {
    ADMIN("ROLE_ADMIN"),
    CHANNEL_MANAGER("ROLE_CHANNEL_MANAGER"),
    USER("ROLE_USER");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
