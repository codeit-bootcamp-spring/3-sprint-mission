package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentDto(String fileName, Long size, String contentType, byte[] bytes) {

//  public static BinaryContent toEntity(BinaryContentDTO binaryContentDTO) {
//    BinaryContent binaryContent = new BinaryContent(binaryContentDTO.fileName(),
//        binaryContentDTO.contentType(),
//        binaryContentDTO.bytes());
//
//    return binaryContent;
//  }
}
