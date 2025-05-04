package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;              // 고유 ID
    private final UUID messageId;       // 어떤 메시지에 첨부된 파일인지
    private final String filename;      // 파일 이름
    private final String contentType;   // 컨텐츠 타입
    private final byte[] data;          // 실제 파일 데이터
    private final long createdAt;       // 생성 시각

    public BinaryContent(UUID messageId, String filename, String contentType, byte[] data) {
        this.id = UUID.randomUUID();
        this.messageId = messageId;
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", data(length)=" + (data != null ? data.length : 0) +
                ", createdAt=" + createdAt +
                '}';
    }
}