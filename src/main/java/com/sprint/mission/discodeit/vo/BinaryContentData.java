package com.sprint.mission.discodeit.vo;

public record BinaryContentData(
    String fileName,
    String contentType,
    byte[] bytes
) {

}
