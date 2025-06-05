package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity(name = "binary_content")
@Table(name = "tbl_binary_contents", schema = "discodeit")
@NoArgsConstructor
@Getter
@DynamicUpdate
public class BinaryContent extends BaseEntity {

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "size")
    private Long size;

    @Column(name = "content_type")
    private String contentType;


    public BinaryContent(String fileName, Long size, String contentType) {
        super.setId(UUID.randomUUID());
        super.setCreatedAt(Instant.now());
        //
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
    }
}
