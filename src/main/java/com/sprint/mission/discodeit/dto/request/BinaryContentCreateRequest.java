package com.sprint.mission.discodeit.dto.request;

public record BinaryContentCreateRequest(
    String fileName, // 예: "123.jpg"
    byte[] bytes,  // 실제 파일 데이터 (저장소에 저장할 때 필요)
    String contentType  // 예: "image/jpeg"
){
  public boolean isValid() {
    return fileName != null && !fileName.isBlank()
        && bytes != null && bytes.length > 0
        && contentType != null && !contentType.isBlank();
  }
}
