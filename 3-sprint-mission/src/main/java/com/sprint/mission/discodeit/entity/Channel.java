package com.sprint.mission.discodeit.entity;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Getter
public class Channel implements Serializable {
    private final UUID id;
    private String name;
    private final UUID maker;
    private final boolean isPrivate;
    private List<UUID> entry;
//    ㄴ DB로 이관하는 경우, 필드값에 List가 들어가는 건 적절치 않음.
//    차라리 table을 따로 만들어서 관리하는 게 나음.
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant enteredAt;

    public Channel(UUID userId, String name, boolean isPrivate) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.maker = userId;
        this.isPrivate = false;
        this.entry = new ArrayList<>();
        this.entry.add(userId);
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.enteredAt = Instant.now();
    }

    // Date 타입 포매팅
    public String getCreatedAt() {
        String formattedCreatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        return formattedCreatedTime;
    }

    public String getUpdatedAt() {
        String formattedUpdatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updatedAt);
        return formattedUpdatedTime;
    }

    @Override
    public String toString() {
        return "Channel{" +
//                "id='" + id + '\'' +
                "name='" + name + '\'' +
//                ", maker='" + getMaker() + '\'' +
                // 포매팅된 date 사용
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }

    public void addEntry(UUID userId) {
        this.entry.add(userId);
    }

    public void addEntry(List<UUID> userIds) {
        this.entry.addAll(userIds);
    }

    public void updateName(String name) {
        this.name = name;
        updateDateTime();
    }

    public void updateDateTime() {
        this.updatedAt = Instant.now();
    }

    public void updateEnteredAt() {
        this.enteredAt = Instant.now();
    }
}
