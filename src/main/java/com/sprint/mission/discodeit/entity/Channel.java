package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Getter
public class Channel implements Serializable {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;
    private Set<UUID> members;

    @Serial
    private static final long serialVersionUID = 1L;

    private Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
        this.members = new HashSet<UUID>();
    }

    // 정적 팩토리 메서드, 객체를 만들지 않고 호출 가능해야 하기 때문에 static으로 선언
    public static Channel of(String name) { return new Channel(name); }

    public void join(UUID userId) {
        members.add(userId);
    }

    public void leave(UUID userId) {
        members.remove(userId);
    }

    public boolean isMember(UUID userId) {
        return members.contains(userId);
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "[Channel] {" + name + " id=" +  id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}"
                + "\n\tmembers=" + members + "}";
    }
}
