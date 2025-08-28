package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "binary_contents")
@Getter
public class BinaryContent extends BaseUpdatableEntity {

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @Column(name = "status", nullable = false)
    private BinaryContentStatus status;

    public BinaryContent(String fileName, Long size, String contentType, BinaryContentStatus status) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.status = status;
    }

    public void updateStatus(BinaryContentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
            "fileName='" + fileName + '\'' +
            ", size=" + size +
            ", contentType='" + contentType + '\'' +
            '}';
    }
}
