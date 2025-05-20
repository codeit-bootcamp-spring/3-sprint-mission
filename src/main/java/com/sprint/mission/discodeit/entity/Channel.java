package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Getter
public class Channel implements Serializable {
    private final UUID id;
    private final ChannelType type;
    private String name;
    private String description;
    private Set<UUID> memberIds;
    private final Instant createdAt;
    private Instant updatedAt;

    @Serial
    private static final long serialVersionUID = 5544881428014541716L;

    private Channel(ChannelType channelType, String name, String description, Set<UUID> memberIds) {
        this.id = UUID.randomUUID();
        this.type = channelType;
        this.name = name;
        this.description = description;
        this.memberIds = new HashSet<>(memberIds);
        this.createdAt = Instant.now();;
        this.updatedAt = this.createdAt;
    }

    // 정적 팩토리 메서드, 객체를 만들지 않고 호출 가능해야 하기 때문에 static으로 선언
    public static Channel ofPublic(String name, String description) {
        return new Channel(ChannelType.PUBLIC, name, description, Set.of());
    }
    public static Channel ofPrivate(Set<UUID> memberIds) {
        return new Channel(ChannelType.PRIVATE, null, null, memberIds);
    }

    public void join(UUID userId) {
        memberIds.add(userId);
    }

    public void leave(UUID userId) {
        memberIds.remove(userId);
    }

    public boolean isMember(UUID userId) {
        return memberIds.contains(userId);
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();;
    }

    @Override
    public String toString() {
        return "[Channel] {" + type + " name=" + name + " id=" +  id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}" + "members=" + memberIds + "}";
    }
}
