package com.sprint.mission.discodeit.dto.request;

public record BinaryContentCreateRequest(
    String fileName,
    byte[] bytes,
    String contentType
) {

    public boolean isValid() {
        return fileName != null && !fileName.isBlank()
            && bytes != null && bytes.length > 0
            && contentType != null && !contentType.isBlank();
    }
}
