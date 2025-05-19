package com.sprint.mission.discodeit.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "프로필 이미지나 첨부 파일을 등록 생성하는 DTO")
public final class BinaryContentCreateRequest {

  @Schema(description = "업로드될 파일명", example = "profile.jpg")
  @NotBlank(message = "파일명은 필수입니다")
  private String fileName;

  @Schema(description = "업로드될 파일의 바이너리 데이터", type = "string", format = "byte")
  @NotBlank
  private byte[] fileData;

  @Schema(description = "업로드될 파일의 MIME 타입", example = "image/jpeg")
  @NotBlank
  private String fileType;
}
