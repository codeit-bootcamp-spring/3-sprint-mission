package com.sprint.mission.discodeit.dto.entity;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private String fileName;
    private String filePath;
    private final Instant createdAt;

    @Serial
    private static final long serialVersionUID = -7687297973903907685L;

    private BinaryContent(UUID userId, UUID channelId, String name, String path) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.fileName = name;
        this.filePath = path;
        this.createdAt = Instant.now();
    }

    public static BinaryContent forUserProfileImage(UUID userId, BinaryContentCreateRequest binaryContentCreateRequest) {
        return new BinaryContent(userId,null, binaryContentCreateRequest.getFileName(), binaryContentCreateRequest.getFilePath());
    }

    public static BinaryContent forMessageFile(UUID userId, UUID channelId, String name, String path) {
        return new BinaryContent(userId, channelId, name, path);
    }

    public BinaryContent update(String name, String path) {
        this.fileName = name;
        this.filePath = path;
        return this;
    }

    @Override
    public String toString() {
        return "[BinaryContent] {" + "id=" + id + ", fileName=" + fileName + ", filePath=" + filePath + ",\n" +
                "\tuserId=" + userId + ", channelId=" + channelId + ", createdAt=" + createdAt + '}';
    }
}