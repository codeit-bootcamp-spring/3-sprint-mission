package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor
public class BinaryContent extends BaseEntity {
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "bytes", nullable = false)
    private byte[] bytes;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
        this.fileName = Objects.requireNonNull(fileName, "File name must not be null");
        this.size = Objects.requireNonNull(size, "File size must not be null");
        this.contentType = Objects.requireNonNull(contentType, "Content type must not be null");
        this.bytes = Objects.requireNonNull(bytes, "Binary data must not be null");
    }
}
