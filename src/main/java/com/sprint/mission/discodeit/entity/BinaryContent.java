package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(
        name = "BinaryContent",
        description = "이진 컨텐츠 정보를 담고 있는 엔티티"
)
@Entity
@Table(name = "tbl_binary_content")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class BinaryContent extends BaseEntity {


    @Schema(
            description = "파일 이름",
            type = "string",
            example = "profile.jpg"
    )
    @Column(name = "file_name", nullable = false, length = 100)
    private String fileName;

    @Schema(
            description = "파일 크기 (바이트)",
            type = "integer",
            format = "int64",
            example = "1024"
    )
    @Column(name = "size", nullable = false)
    private Long size;

    @Schema(
            description = "컨텐츠 타입",
            type = "string",
            example = "image/jpeg"
    )
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    public BinaryContent(String fileName, Long size, String contentType) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
    }
}
