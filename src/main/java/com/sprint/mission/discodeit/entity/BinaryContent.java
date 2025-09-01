package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * packageName    : com.sprint.mission.discodeit.entity
 * fileName       : BinaryContent
 * author         : doungukkim
 * date           : 2025. 4. 23.
 */
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "binary_contents", schema = "discodeit")
public class BinaryContent extends BaseUpdatableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "extensions", nullable = false, length = 20)
    private String extension;

    @Column(name = "status", nullable = false, length = 20)
    private BinaryContentStatus status;

    public void updateStatus(BinaryContentStatus status) {
        this.status = status;
    }

    public BinaryContent(String fileName, Long size, String contentType, String extension) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.extension = extension;
    }


}
