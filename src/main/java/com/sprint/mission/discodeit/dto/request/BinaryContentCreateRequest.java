package com.sprint.mission.discodeit.dto.request;

public record BinaryContentCreateRequest(
    String fileName,
    Long size,
    String contentType,
    byte[] bytes
) {

    public boolean isValid() {
        return fileName != null && !fileName.isBlank()
            && bytes != null && bytes.length > 0
            && contentType != null && !contentType.isBlank();
    }
}
