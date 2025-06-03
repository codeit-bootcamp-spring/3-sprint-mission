package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// 프로필 이미지 & 첨부 파일 표현 도메인 모델 ( 수정 불가 : updated 필드 X )
// 참조 관계상 User, Message 모델이 BinaryContent를 참조함
@Getter
@Schema(description = "파일( 프로필 이미지 or 첨부파일 )의 정보 도메인 모델")
public class BinaryContent implements Serializable {

  private static final long serialVersionUID = 1L;
  //
  @Schema(
      description = "파일의 고유 식별 번호",
      example = "f8d5b8f0-89c2-4f83-b881-9a9e10a34687",
      type = "string",
      format = "uuid"
  )
  private final UUID id;

  @Schema(
      description = "파일이 업로드된 시각",
      example = "2025-05-14T13:00:00Z",
      type = "string",
      format = "date-time"
  )
  private final Instant createdAt;
  //
  @Schema(
      description = "업로드된 파일명",
      example = "test-image.png"
  )
  @NotBlank(message = "파일명은 필수입니다")
  private String fileName;

  @Schema(
      description = "업로된 파일의 크기( byte 단위 )",
      example = "1024",
      type = "string",
      format = "long"
  )
  @NotBlank(message = "파일 크기는 필수입니다")
  @Min(value = 1, message = "파일 최소 크기는 1바이트 이상이어야 합니다")
  private Long size;


  @Schema(
      description = "업로드된 파일의 MIME 타입",
      example = "image/png"
  )
  @NotBlank(message = "파일 타입은 필수입니다")
  private String contentType;


  @Schema(
      description = "업로드된 파일의 바이너리 데이터",
      type = "string",
      format = "byte"
  )
  @NotBlank(message = "파일 데이터가 비어있을 수는 없습니다")
  private byte[] bytes;


  // 생성자
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
