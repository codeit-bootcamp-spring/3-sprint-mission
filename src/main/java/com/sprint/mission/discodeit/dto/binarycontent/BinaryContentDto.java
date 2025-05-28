package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentDto(String fileName, Long size, String contentType, byte[] bytes) {

}
