package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;

public record BinaryContentDTO(String fileName, String contentType, byte[] bytes) {

//  public static BinaryContent toEntity(BinaryContentDTO binaryContentDTO) {
//    BinaryContent binaryContent = new BinaryContent(binaryContentDTO.fileName(),
//        binaryContentDTO.contentType(),
//        binaryContentDTO.bytes());
//
//    return binaryContent;
//  }
}
