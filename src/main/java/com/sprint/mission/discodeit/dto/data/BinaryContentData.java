package com.sprint.mission.discodeit.dto.data;

public record BinaryContentData(
    String fileName,
    String contentType,
    byte[] bytes
) {

}
