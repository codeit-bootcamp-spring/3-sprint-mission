package com.sprint.mission.discodeit.dto.request;

import java.io.Serializable;

public record BinaryContentCreateRequest(
    String fileName, // 예: "123.jpg"
    byte[] content,  // 실제 파일 데이터 (저장소에 저장할 때 필요)
    String fileType  // 예: "image/jpeg"
){
  public boolean isValid() {
    return fileName != null && !fileName.isBlank()
        && content != null && content.length > 0
        && fileType != null && !fileType.isBlank();
  }
}
