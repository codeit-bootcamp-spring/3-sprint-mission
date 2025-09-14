package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "binary_contents")
@Getter
public class BinaryContent extends BaseUpdatableEntity {

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BinaryContentStatus status = BinaryContentStatus.PROCESSING;


    public BinaryContent(String fileName, Long size, String contentType) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
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
