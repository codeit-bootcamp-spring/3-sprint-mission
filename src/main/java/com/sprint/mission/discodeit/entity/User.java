package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String name;
    private String email;
    private String password;
    private UUID profileId;
    private String introduction;
    private boolean isLogin;
    private List<UUID> friends;
    private List<UUID> channels;
    private List<UUID> messages;

    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.isLogin = true;
        this.friends = new ArrayList<>();
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void updateEmail(String email) {
        this.email = email;
        this.updatedAt = Instant.now();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updatedAt = Instant.now();
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
        this.updatedAt = Instant.now();
    }

    public void updateProfileID(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }

    public void updateisLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    @Override
    public String toString() {
        return "User {\n" +
                "  id=" + id + ",\n" +
                "  createdAt=" + createdAt + ",\n" +
                "  updatedAt=" + updatedAt + ",\n" +
                "  name='" + name + "',\n" +
                "  email='" + email + "',\n" +
                "  password='" + password + "',\n" +
                "  introduction='" + introduction + "',\n" +
                "  isLogin=" + isLogin + ",\n" +
                "  friends=" + friends + ",\n" +
                "  channels=" + channels.stream().toList() + "\n" +
                "  messages=" + messages.stream().toList() + "\n" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
