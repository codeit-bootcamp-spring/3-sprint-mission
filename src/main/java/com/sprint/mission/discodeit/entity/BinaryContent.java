package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Schema(
        name = "BinaryContent",
        description = "이진 컨텐츠 정보를 담고 있는 엔티티"
)
@Getter
public class BinaryContent implements Serializable {

  private static final long serialVersionUID = 1L;
  @Schema(
          description = "이진 컨텐츠의 고유 식별자",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID id;

  @Schema(
          description = "이진 컨텐츠 생성 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant createdAt;

  @Schema(
          description = "파일 이름",
          type = "string",
          example = "profile.jpg"
  )
  private String fileName;

  @Schema(
          description = "파일 크기 (바이트)",
          type = "integer",
          format = "int64",
          example = "1024"
  )
  private Long size;

  @Schema(
          description = "컨텐츠 타입",
          type = "string",
          example = "image/jpeg"
  )
  private String contentType;

  @Schema(
          description = "파일의 바이트 데이터",
          type = "string",
          format = "byte"
  )
  private byte[] bytes;


  public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.bytes = bytes;
  }
}
